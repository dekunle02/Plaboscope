package com.adeleke.samad.plaboscope.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adeleke.samad.plaboscope.database.STAT_TABLE

@Entity(tableName = STAT_TABLE)
data class Stat(
        @PrimaryKey
        @ColumnInfo(name = "specialtyName")
        var specialtyName: String,
        @ColumnInfo(name = "attempts")
        var attemptTally: Int = 0,
        @ColumnInfo(name = "passed")
        var correctTally: Int = 0,
        @ColumnInfo(name = "failed")
        var wrongTally: Int = 0,
        @ColumnInfo(name = "timeSpent")
        var time: Int = 0,
        @ColumnInfo(name = "questionIndex")
        var currentQuestionIndex: Int = 0
) {
    override fun toString(): String {
        return "Name -> $specialtyName \n" +
                "attempt Tally -> $attemptTally \n" +
                "correctTally -> $correctTally \n" +
                "wrongTally -> $wrongTally \n" +
                "Time -> $time \n"
    }
}

