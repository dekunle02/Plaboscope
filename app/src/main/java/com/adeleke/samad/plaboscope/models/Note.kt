package com.adeleke.samad.plaboscope.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adeleke.samad.plaboscope.database.NOTE_TABLE
import java.util.*

@Entity(tableName = NOTE_TABLE)
data class Note(
        @PrimaryKey
        @ColumnInfo(name = "entryId")
        var id: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "title")
        var title: String = "",
        @ColumnInfo(name = "body")
        var body: String = "",

        @ColumnInfo(name= "tag")
        var tag: String = "Personal"
) {
        override fun toString(): String {
                return "$title\n\n$body\n"
        }
}