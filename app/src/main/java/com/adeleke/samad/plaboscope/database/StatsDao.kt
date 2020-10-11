package com.adeleke.samad.plaboscope.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.adeleke.samad.plaboscope.models.Question
import com.adeleke.samad.plaboscope.models.Stat

@Dao
interface StatsDao {

    @Insert
    fun insert(stat: Stat)

    @Update
    fun update(stat: Stat)

    @Query("SELECT * FROM $STAT_TABLE WHERE specialtyName = :name LIMIT 1")
    fun getStat(name: String): Stat?

    @Query("SELECT * FROM $STAT_TABLE WHERE specialtyName != 'all'")
    fun getAllSpecialtyStat(): List<Stat>
}