package com.adeleke.samad.plaboscope.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adeleke.samad.plaboscope.models.Note
import com.adeleke.samad.plaboscope.models.Question
import com.adeleke.samad.plaboscope.models.Stat


@Database(entities = [Question::class, Stat::class, Note::class],
        version = 1,
        exportSchema = false)
abstract class PlaboscopeDatabase : RoomDatabase() {

    abstract val questionsDao: QuestionsDao
    abstract val statsDao: StatsDao
    abstract val notesDao: NotesDao


    companion object{
        private var INSTANCE: PlaboscopeDatabase? = null


        fun getInstance(context: Context): PlaboscopeDatabase {
            synchronized(this) {
                var instance: PlaboscopeDatabase? = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            PlaboscopeDatabase::class.java,
                            DB_NAME
                    )
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_2)
                            .build()
                    INSTANCE = instance
                }
            }
            return INSTANCE!!
        }
    }
}


//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE $STAT_TABLE" +
//                        " ADD COLUMN questionIndex INTEGER")
//            }
//        }
//val MIGRATION_3_4: Migration? = object : Migration(3, 4) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        // Create the new table
//        database.execSQL(
//                "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))")
//        // Copy the data
//        database.execSQL(
//                "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users")
//        // Remove the old table
//        database.execSQL("DROP TABLE users")
//        // Change the table name to the correct one
//        database.execSQL("ALTER TABLE users_new RENAME TO users")
//    }
//}