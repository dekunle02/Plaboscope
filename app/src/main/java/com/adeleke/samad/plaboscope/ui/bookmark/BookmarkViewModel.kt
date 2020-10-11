package com.adeleke.samad.plaboscope.ui.bookmark

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.Question
import com.adeleke.samad.plaboscope.models.Stat
import kotlinx.coroutines.*

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext

    // Database
    private val questionDatabase = PlaboscopeDatabase.getInstance(application).questionsDao
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerData = MutableLiveData<List<Question>>()
    val recyclerData: LiveData<List<Question>>
        get() = _recyclerData


    fun populateRecyclerViewData() {
        uiScope.launch {
            _recyclerData.value = getBookmarkedQuestions()
        }
    }


    private suspend fun getBookmarkedQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            return@withContext questionDatabase.getBookmarkedQuestions("true")
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun removeQuestionFromBookmarked(questionSwiped: Question) {
        questionSwiped.bookmarked = "false"
        uiScope.launch {
            updateQuestion(questionSwiped)
        }
    }

    fun bookmarkQuestion(pos: Int, questionSwiped: Question) {
        questionSwiped.bookmarked ="true"
        uiScope.launch {
            updateQuestion(questionSwiped)
        }
        val listCopy = _recyclerData.value?.toMutableList()
        listCopy?.add(pos, questionSwiped)
        _recyclerData.value = listCopy?.toList()
    }

    private suspend fun updateQuestion(question: Question) {
        withContext(Dispatchers.IO) {
            questionDatabase.update(question)
        }
        populateRecyclerViewData()
    }
}