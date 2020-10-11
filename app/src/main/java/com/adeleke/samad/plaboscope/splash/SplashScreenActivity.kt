package com.adeleke.samad.plaboscope.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.adeleke.samad.plaboscope.MainActivity
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.database.QuestionsDao
import com.adeleke.samad.plaboscope.database.StatsDao
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.services.AlarmHelper
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var questionsDatabase: QuestionsDao
    private lateinit var statsDatabase: StatsDao
    private lateinit var questionHelper: QuestionHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // set up database if it is first time app is being loaded
        questionHelper = QuestionHelper(this)
        questionsDatabase = PlaboscopeDatabase.getInstance(this).questionsDao
        statsDatabase = PlaboscopeDatabase.getInstance(this).statsDao
        sharedPreferences = getSharedPreferences("com.adeleke.samad.plaboscope", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if (!sharedPreferences.getBoolean("hasSetUpDatabase", false)) {
            setUpQuestionsDatabase()
            setUpStatsDatabase()
            editor.putBoolean("hasSetUpDatabase", true)
            editor.apply()
            val alarmHelper = AlarmHelper(this)
            alarmHelper.setAlarm("daily")
        }
        // This sets the default values, the last parameter should be false so it doesn't set it every time user opens app
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightPref: Boolean = sharedPref.getBoolean("night_switch", false)
        if (nightPref) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }


    private fun setUpQuestionsDatabase() {
        uiScope.launch {
            addAllQuestions()
        }
    }

    private fun setUpStatsDatabase() {
        uiScope.launch {
            addEmptyStats()
        }
    }

    private suspend fun addAllQuestions() {
        val questions = questionHelper.getAllQuestions()
        withContext(Dispatchers.IO) {
            questions.forEach { question ->
                questionsDatabase.insert(question)
            }
        }
    }

    private suspend fun addEmptyStats() {
        val statArr = questionHelper.initializeStatistics()
        withContext(Dispatchers.IO) {
            for (stat in statArr) {
                statsDatabase.insert(stat)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


}