package com.adeleke.samad.plaboscope.database

import androidx.room.*
import com.adeleke.samad.plaboscope.models.Note

@Dao
interface NotesDao {

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM $NOTE_TABLE")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM $NOTE_TABLE WHERE tag = :tag")
    fun getAllNotesByTag(tag: String): List<Note>

    @Query("SELECT * FROM $NOTE_TABLE WHERE entryId = :id")
    fun getNoteById(id: String): Note

}