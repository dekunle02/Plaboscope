package com.adeleke.samad.plaboscope.ui.note.list

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.Note
import com.adeleke.samad.plaboscope.models.Question
import kotlinx.coroutines.*

class NoteListViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext

    // Database
    private val noteDatabase = PlaboscopeDatabase.getInstance(application).notesDao
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerData = MutableLiveData<List<Note>>()
    val recyclerData: LiveData<List<Note>>
        get() = _recyclerData

    fun populateRecyclerView() {
        uiScope.launch {
            _recyclerData.value = getAllNotes()
            sortByTag()
        }
    }

    private suspend fun getAllNotes(): List<Note> {
        return withContext(Dispatchers.IO) {
            return@withContext noteDatabase.getAllNotes()
        }
    }

    fun deleteNote(note: Note) {
        uiScope.launch {
            deleteNoteFromDb(note)
        }
        _snackMessage.value = context.getString(R.string.note_deleted)
    }


    fun addNote(pos: Int, note: Note) {
        uiScope.launch {
            addNoteToDb(note)
        }
        val listCopy = _recyclerData.value?.toMutableList()
        listCopy?.add(pos, note)
        _recyclerData.value = listCopy?.toList()
    }


    private suspend fun addNoteToDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.insert(note)
        }
    }

    private suspend fun deleteNoteFromDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.delete(note)
        }
    }

    private fun sortByTag() {
        val sortedList =
                _recyclerData.value!!.sortedWith(compareBy { it.tag })
        _recyclerData.value = sortedList.toMutableList()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}