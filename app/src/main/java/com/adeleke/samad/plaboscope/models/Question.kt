package com.adeleke.samad.plaboscope.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adeleke.samad.plaboscope.database.QUESTION_TABLE

@Entity(tableName = QUESTION_TABLE)
data class Question(
        @PrimaryKey
        @ColumnInfo(name = "number")
        var number: String,

        @ColumnInfo(name = "specialty")
        var specialty: String = "",

        @ColumnInfo(name = "body")
        var body: String,

        @ColumnInfo(name = "optionA")
        var optionA: String,

        @ColumnInfo(name = "optionB")
        var optionB: String,

        @ColumnInfo(name = "optionC")
        var optionC: String,

        @ColumnInfo(name = "optionD")
        var optionD: String,

        @ColumnInfo(name = "optionE")
        var optionE: String,

        @ColumnInfo(name = "explanation")
        var explanation: String,

        @ColumnInfo(name = "bookmarked")
        var bookmarked: String = "false",

        @ColumnInfo(name = "correctAnswerKey")
        var correctAnswerKey: String,

        @ColumnInfo(name = "lastChosenAnswer")
        var lastChosenAnswer: String = "",

        @ColumnInfo(name = "attemptTally")
        var attemptTally: Int = 0,

        @ColumnInfo(name = "correctTally")
        var correctTally: Int = 0,

        @ColumnInfo(name = "wrongTally")
        var wrongTally: Int = 0
) {
    override fun toString(): String {
        return "$number. " + body + "\n" +
                "a. " + optionA + "\n" +
                "b. " + optionB + "\n" +
                "c. " + optionC + "\n" +
                "d. " + optionD + "\n" +
                "e. " + optionE + "\n" +
                "Ans-> " + explanation + "\n" +
                "Specialty -> " + specialty + "\n" +
                "Correct Tally -> " + correctTally + "\n" +
                "Attempt Tally -> " + attemptTally + "\n"
    }
}

