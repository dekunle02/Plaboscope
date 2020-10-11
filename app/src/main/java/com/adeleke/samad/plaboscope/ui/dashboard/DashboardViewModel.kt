package com.adeleke.samad.plaboscope.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.models.Stat
import kotlinx.coroutines.*
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    private val statDatabase = PlaboscopeDatabase.getInstance(application).statsDao
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var allStat: Stat

    // Two way observables
    val overallProgressValue = MutableLiveData<Int>()
    private val overallProgressText = MutableLiveData<String>()
    val ringProgressValue = MutableLiveData<Float>()
    val ringProgressText = MutableLiveData<String>()
    val statSummary = MutableLiveData<String>()

    init {
        initStat()
    }


    private fun initStat() {
        uiScope.launch {
            getStat()
            setUpViewWithStat(allStat)
        }
    }

    private suspend fun getStat() {
        var stat: Stat? = null
        withContext(Dispatchers.IO) {
            stat = statDatabase.getStat("all")
            allStat = stat!!
        }
    }

    private fun setUpViewWithStat(stat: Stat) {
        val totalQuestions = QuestionHelper.getTotalQuestionsSize(context)
        val questionsAttempted = stat.attemptTally.toInt()
        val questionsFailed = stat.wrongTally.toInt()
        val questionsPassed = stat.correctTally.toInt()
        val timeTaken = stat.time


        var progress = questionsAttempted  * 100 / totalQuestions
        if (totalQuestions == 0) {
            progress = 0
        }
        val progressText = "Overall Progress: ${progress}%"


        var passRate = 0.0F
        var failRate = 0
        var timeExam = 0
        if (questionsAttempted != 0) {
            passRate = questionsPassed.toFloat() / questionsAttempted * 100
            failRate = questionsFailed * 100 / questionsAttempted
            timeExam = timeTaken * 180/ questionsAttempted
        }
        val passRateText = "${passRate.toInt()}%"

        val hours: Int = timeExam / 3600
        val minutes: Int = timeExam % 3600 / 60
        val secs: Int = timeExam % 60
        val displayedTime = java.lang.String
                .format(Locale.getDefault(),
                        "%d hr:%02d min:%02d sec", hours,
                        minutes, secs)

        val summaryText = "Average Score: ${passRate.toInt()}%\n" +
                "Fail rate: ${failRate}%\n" +
                "Est. exam time: ${displayedTime}s"

        overallProgressValue.value = progress
        overallProgressText.value = progressText
        ringProgressValue.value = passRate
        ringProgressText.value = passRateText
        statSummary.value = summaryText
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}