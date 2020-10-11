package com.adeleke.samad.plaboscope.ui.test

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.*
import com.adeleke.samad.plaboscope.BLOCK_TEST_TYPE
import com.adeleke.samad.plaboscope.MOCK_TEST_TYPE
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.RANDOM_TEST_TYPE
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.Note
import com.adeleke.samad.plaboscope.models.Question
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.models.Stat
import kotlinx.coroutines.*
import java.util.*


class TestViewModel(var testType: String, application: Application) : AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    // Database and questionHelper
    private val questionDatabase = PlaboscopeDatabase.getInstance(context).questionsDao
    private val statsDatabase = PlaboscopeDatabase.getInstance(context).statsDao
    private val noteDatabase = PlaboscopeDatabase.getInstance(application).notesDao
    private val questionHelper = QuestionHelper(context)

    private var questionListSize = 0
    private var posToShow = 0
    private lateinit var questionIterator: ListIterator<String>
    private lateinit var currentQuestion: Question
    var positionAtReviewBeginning: Int = 0

    private var viewMode: ViewMode = ViewMode.ACTIVE
    private var viewDirection: ViewDirection = ViewDirection.FORWARD

    // Current Stat Objects for both the specialty and everything
    private lateinit var mainStat: Stat
    private lateinit var specialtyStat: Stat

    // Coroutine
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Two way bindings with UI
    val questionBody = MutableLiveData<String>()
    val optionA = MutableLiveData<String>()
    val optionB = MutableLiveData<String>()
    val optionC = MutableLiveData<String>()
    val optionD = MutableLiveData<String>()
    val optionE = MutableLiveData<String>()
    val explanation = MutableLiveData<String>()

    val nextButtonText = MutableLiveData<String>()

    val timeTally = MutableLiveData<String>()
    var time = 0
    val correctTally = MutableLiveData<String>()
    var correct = 0
    val wrongTally = MutableLiveData<String>()
    var wrong = 0


    // Observables to the fragment class
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _rightAnswerPosition = MutableLiveData<String?>()
    val rightAnswerPosition: LiveData<String?>
        get() = _rightAnswerPosition

    private val _optionToHide = MutableLiveData<String?>()
    val optionToHide: LiveData<String?>
        get() = _optionToHide

    private val _testResults = MutableLiveData<Map<String, String>>()
    val testResults: LiveData<Map<String, String>>
        get() = _testResults

    private val _lastChosenAnswer = MutableLiveData<String>()
    val lastChosenAnswer: LiveData<String>
        get() = _lastChosenAnswer

    private val _canShadeOptions = MutableLiveData<Boolean>()
    val canShadeOptions: LiveData<Boolean>
        get() = _canShadeOptions

    private val _canShowLastAnswerPicked = MutableLiveData<Boolean>()
    val canShowLastAnswerPicked: LiveData<Boolean>
        get() = _canShowLastAnswerPicked

    private val _canShowCheckAnswerButton = MutableLiveData<Boolean>()
    val canShowCheckAnswerButton: LiveData<Boolean>
        get() = _canShowCheckAnswerButton


    init {
        // Initial test conditions
        startTimer()
        correctTally.value = "Correct: 0"
        wrongTally.value = "Wrong: 0"

        uiScope.launch {
            when (testType) {
                RANDOM_TEST_TYPE -> {
                    mainStat = getStat("all")
                    questionIterator = makeRandomList(10).listIterator()
                }
                MOCK_TEST_TYPE -> {
                    mainStat = getStat("all")
                    questionIterator = makeRandomList(180).listIterator()
                }
                BLOCK_TEST_TYPE -> {
                    mainStat = getStat("all")
                    posToShow = mainStat.currentQuestionIndex
                    questionIterator = get1700List().listIterator(posToShow)
                }
                else -> {
                    mainStat = getStat("all")
                    specialtyStat = getStat(testType)
                    posToShow = specialtyStat.currentQuestionIndex
                    questionIterator = getSpecialtyList(testType).listIterator(posToShow)
                }
            }
            nextQuestionButtonClick()
        }
    }

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

    private fun displayQuestion(type: String) {

//        val position = if (viewDirection == ViewDirection.FORWARD) questionIterator.nextIndex() else questionIterator.previousIndex() + 1
        val total = questionListSize

        questionBody.value = "${posToShow}/$total \n(${currentQuestion.number}) ${currentQuestion.body}"
        optionA.value = currentQuestion.optionA
        optionB.value = currentQuestion.optionB
        optionC.value = currentQuestion.optionC
        optionD.value = currentQuestion.optionD
        optionE.value = currentQuestion.optionE
        explanation.value = currentQuestion.explanation
        _rightAnswerPosition.value = currentQuestion.correctAnswerKey
        _lastChosenAnswer.value = currentQuestion.lastChosenAnswer

        if (type == "clean") {
            _canShadeOptions.value = false
            _canShowLastAnswerPicked.value = false
            _canShowCheckAnswerButton.value = true
        } else if (type == "shaded") {
            _canShadeOptions.value = true
            _canShowLastAnswerPicked.value = true
            _canShowCheckAnswerButton.value = false
        }


        if (currentQuestion.optionC == ".") {
            _optionToHide.value = "C"
        }
        if (currentQuestion.optionD == ".") {
            _optionToHide.value = "D"
        }
        if (currentQuestion.optionE == ".") {
            _optionToHide.value = "E"
        }


    }

    fun checkAnswerButtonClick(chosenAnswer: String) {
        if (testType == BLOCK_TEST_TYPE) {
            mainStat.currentQuestionIndex = questionIterator.nextIndex()
        } else if ((testType != RANDOM_TEST_TYPE) && (testType != MOCK_TEST_TYPE)) {
            specialtyStat.currentQuestionIndex = questionIterator.nextIndex()
        }
        currentQuestion.lastChosenAnswer = chosenAnswer
        currentQuestion.attemptTally = currentQuestion.attemptTally + 1
        mainStat.attemptTally = mainStat.attemptTally + 1
        mainStat.time = mainStat.time + time
        specialtyStat.attemptTally = specialtyStat.attemptTally + 1
        specialtyStat.time = specialtyStat.time + time
        positionAtReviewBeginning = questionIterator.nextIndex()

        if (chosenAnswer == currentQuestion.correctAnswerKey) {
            pickedOptionCorrect()
        } else {
            pickedOptionWrong()
        }
        updateAllStats()
        displayQuestion("shaded")
    }

    private fun pickedOptionCorrect() {
        correct++
        correctTally.value = "Correct: $correct"
        currentQuestion.correctTally = currentQuestion.correctTally + 1
        mainStat.correctTally = mainStat.correctTally + 1
        specialtyStat.correctTally = specialtyStat.correctTally + 1
    }

    private fun pickedOptionWrong() {
        wrong++
        wrongTally.value = "Wrong: $wrong"
        currentQuestion.wrongTally = currentQuestion.wrongTally + 1
        mainStat.wrongTally = mainStat.wrongTally + 1
        specialtyStat.wrongTally = specialtyStat.wrongTally + 1
    }

    fun nextQuestionButtonClick() {
        posToShow++
        if (viewDirection == ViewDirection.BACKWARD)
            questionIterator.next()

        if (questionIterator.nextIndex() >= positionAtReviewBeginning) {
            viewMode = ViewMode.ACTIVE
            if (questionIterator.hasNext()) {
                uiScope.launch {
                    currentQuestion = getQuestionByNumber(questionIterator.next())
                    specialtyStat = getStat(currentQuestion.specialty)
                    displayQuestion("clean")
                }
            } else {
                endTest()
            }
        }
        if (questionIterator.nextIndex() < positionAtReviewBeginning) {
            uiScope.launch {
                currentQuestion = getQuestionByNumber(questionIterator.next())
                specialtyStat = getStat(currentQuestion.specialty)
                displayQuestion("shaded")
            }
        }

        viewDirection = ViewDirection.FORWARD
    }

    fun backButtonClick() {

        if ((viewMode == ViewMode.ACTIVE) || (viewDirection == ViewDirection.FORWARD)) {
            questionIterator.previous()
            uiScope.launch {
                posToShow--
                currentQuestion = getQuestionByNumber(questionIterator.previous())
                specialtyStat = getStat(currentQuestion.specialty)
                displayQuestion("shaded")
            }
            viewMode = ViewMode.REVIEW
        } else {
            if (questionIterator.hasPrevious()) {
                uiScope.launch {
                    posToShow--
                    currentQuestion = getQuestionByNumber(questionIterator.previous())
                    specialtyStat = getStat(currentQuestion.specialty)
                    displayQuestion("shaded")
                }
            }
        }
        viewDirection = ViewDirection.BACKWARD
    }

    private fun endTest() {

        if (testType == BLOCK_TEST_TYPE) {
            mainStat.currentQuestionIndex = 0
        } else if ((testType != RANDOM_TEST_TYPE) && (testType != MOCK_TEST_TYPE)) {
            specialtyStat.currentQuestionIndex = 0
        }
        updateAllStats()

        val percentage = correct * 100 / (correct + wrong)
        _testResults.value = mapOf("time" to "${timeTally.value}",
                "percentage" to "$percentage"
        )
    }

    private fun updateAllStats() {
        uiScope.launch {
            updateQuestion(currentQuestion)
            updateStat(mainStat)
            updateStat(specialtyStat)
        }
    }

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////


    private fun startTimer() {
        time = 0
        val timer = Handler()
        val run: java.lang.Runnable = object : java.lang.Runnable {
            override fun run() {
                timer.postDelayed(this, 1000)
                time++
                val hours: Int = time / 3600
                val minutes: Int = time % 3600 / 60
                val secs: Int = time % 60
                val displayedTime = java.lang.String
                        .format(Locale.getDefault(),
                                "%d hr:%02d min:%02d sec", hours,
                                minutes, secs)
                timeTally.value = "Time: $displayedTime"
            }
        }
        timer.post(run)
    }

    private fun makeRandomList(num: Int): List<String> {
        val random = Random()
        val numberList = mutableListOf<String>()
        for (i in 1..num) {
            val randomNumber = random.nextInt(QuestionHelper.getTotalQuestionsSize(context))
            numberList.add(randomNumber.toString())
        }
        questionListSize = num
        return numberList.toList()
    }

    private fun get1700List(): List<String> {
        val numberList = mutableListOf<String>()
        questionListSize = QuestionHelper.getTotalQuestionsSize(context)

        for (i in 1..questionListSize) {
            numberList.add(i.toString())
        }
        return numberList.toList()
    }

    private fun getSpecialtyList(testType: String): List<String> {
        questionListSize = questionHelper.getSpecialtySize(testType)
        //        return allQuestionList.subList(fromIndex = questionPosition, toIndex = allQuestionList.size)
        return questionHelper.getIndicesOfSpecialty(testType)
    }


    /////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
    //Suspend functions to get and update Statistics and Question
    private suspend fun getStat(specialty: String): Stat {
        var stat: Stat? = null
        withContext(Dispatchers.IO) {
            stat = statsDatabase.getStat(specialty)!!
        }
        return stat!!
    }

    private suspend fun updateStat(stat: Stat) {
        withContext(Dispatchers.IO) {
            statsDatabase.update(stat)
        }
    }

    private suspend fun getQuestionByNumber(number: String): Question {
        return withContext(Dispatchers.IO) {
            return@withContext questionDatabase.getQuestionByNumber(number)!!
        }
    }

    private suspend fun updateQuestion(question: Question) {
        withContext(Dispatchers.IO) {
            questionDatabase.update(question)
        }
    }

    private suspend fun addNoteToDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.insert(note)
        }
    }

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

    // public fun options for each question
    fun bookmarkQuestion() {
        if (currentQuestion.bookmarked == "true") {
            currentQuestion.bookmarked = "false"
            _snackMessage.value = context.getString(R.string.question_unbookmarked)
        } else {
            currentQuestion.bookmarked = "true"
            _snackMessage.value = context.getString(R.string.question_bookmarked)
        }
        uiScope.launch {
            updateQuestion(currentQuestion)
        }
    }

    fun shareQuestion(): String {
        return questionHelper.getQuestionShareString(currentQuestion)
    }

    fun getNoteAndIdFromQuestion(): String {
        val note = Note()
        note.tag = currentQuestion.specialty
        note.body = currentQuestion.body + "\n\n" + currentQuestion.explanation + "\n"
        uiScope.launch {
            addNoteToDb(note)
        }
        return note.id
    }


    // Clear the Job
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // Factory class to pass id into viewModel object
    class TestViewModelFactory(
            private val testType: String,
            private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
                return TestViewModel(testType, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    enum class ViewMode { REVIEW, ACTIVE }
    enum class ViewDirection { FORWARD, BACKWARD }

}