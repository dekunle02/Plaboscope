package com.adeleke.samad.plaboscope.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.adeleke.samad.plaboscope.models.Question

@Dao
interface QuestionsDao {

    @Insert
    fun insert(question: Question)

    @Update
    fun update(question: Question)


    @Query("SELECT * FROM $QUESTION_TABLE WHERE number = :number LIMIT 1")
    fun getQuestionByNumber(number: String): Question?


    @Query("SELECT * FROM $QUESTION_TABLE WHERE specialty= :specialty")
    fun getQuestionsBySpecialty(specialty: String): List<Question>

    @Query("SELECT * FROM $QUESTION_TABLE WHERE attemptTally > 0")
    fun getTrueAttemptedQuestionCount(): List<Question>

    @Query("SELECT * FROM $QUESTION_TABLE WHERE bookmarked = :bookmarked")
    fun getBookmarkedQuestions(bookmarked: String): List<Question>

}