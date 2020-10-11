package com.adeleke.samad.plaboscope.ui.note.edit

import android.app.Application
import androidx.lifecycle.*
import com.adeleke.samad.plaboscope.R
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.models.Note
import com.adeleke.samad.plaboscope.NEW_NOTE_ID
import kotlinx.coroutines.*

class EditNoteViewModel(var noteId: String, application: Application) :
        AndroidViewModel(application) {

    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    // Database
    private val noteDatabase = PlaboscopeDatabase.getInstance(application).notesDao
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    lateinit var note: Note
    private var isNewNote: Boolean = true

    // Two way observables for the fields
    val title = MutableLiveData<String>()
    val tag = MutableLiveData<String>()
    val body = MutableLiveData<String>()

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    init {
        initNoteWithId()
    }

    private fun initNoteWithId() {
        if (noteId == NEW_NOTE_ID) {
            note = Note()
            noteId = note.id
            title.value = note.title
            tag.value = "TAG: " + note.tag
            body.value = note.body

        } else {
            isNewNote = false
            uiScope.launch {
                note = getNoteFromDb(noteId)
                title.value = note.title
                tag.value = note.tag
                body.value = note.body
            }
        }
    }

    fun addOrUpdateNote() {
        if (isNewNote) {
            addNote()
        } else {
            updateNote()
        }
        _snackMessage.value = context.getString(R.string.note_saved)
    }

    fun deleteNote() {
        uiScope.launch {
            deleteNoteFromDb(note)
        }
        _snackMessage.value = context.getString(R.string.note_deleted)
    }

    private fun addNote() {
        val newNote = makeNoteFromField()
        uiScope.launch {
            addNoteToDb(newNote!!)
        }
    }

    private fun updateNote() {
        val newNote = makeNoteFromField()
        uiScope.launch {
            updateNoteToDb(newNote!!)
        }
    }

    private fun makeNoteFromField(): Note? {
        val title = title.value
        val tag = tag.value
        val body = body.value

        return Note(
                id = noteId,
                title = title!!,
                tag = tag!!,
                body = body!!
        )
    }


    // Suspend functions that interact with the database
    private suspend fun getNoteFromDb(id: String): Note {
        return withContext(Dispatchers.IO) {
            return@withContext noteDatabase.getNoteById(id)
        }
    }

    private suspend fun addNoteToDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.insert(note)
        }
    }

    private suspend fun updateNoteToDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.update(note)
        }
    }

    private suspend fun deleteNoteFromDb(note: Note) {
        withContext(Dispatchers.IO) {
            noteDatabase.delete(note)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // Factory class to pass id into viewmodel object
    class EditNoteViewModelFactory(
            private val noteId: String,
            private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
                return EditNoteViewModel(noteId, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}