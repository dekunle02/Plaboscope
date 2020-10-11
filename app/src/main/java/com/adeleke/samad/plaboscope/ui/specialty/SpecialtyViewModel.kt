package com.adeleke.samad.plaboscope.ui.specialty

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.Stat
import kotlinx.coroutines.*
import kotlin.math.log

class SpecialtyViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext

    // Database
    private val statDatabase = PlaboscopeDatabase.getInstance(application).statsDao
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerData = MutableLiveData<List<Stat>>()
    val recyclerData: LiveData<List<Stat>>
        get() = _recyclerData


    fun populateRecyclerView() {
        uiScope.launch {
            _recyclerData.value = getSpecialtyStats()
        }
    }


    private suspend fun getSpecialtyStats(): List<Stat> {
        return withContext(Dispatchers.IO) {
            return@withContext statDatabase.getAllSpecialtyStat()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}