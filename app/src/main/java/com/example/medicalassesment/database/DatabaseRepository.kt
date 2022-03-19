package com.example.medicalassesment.database

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.medicalassesment.models.QuestionModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(val application: Application) {
    val medicalDataBase = MedicalDataBase.getInstance(application)

    suspend fun getQuestions(tampletId: Int): LiveData<List<QuestionModel>> {
        return medicalDataBase.getDao().getQuestions(tampletId.toString())
    }

    suspend fun getQuestionsNonLive(tampletId: Int): List<QuestionModel> {
        medicalDataBase.getDao().getQuestions(tampletId.toString()).value.let {
            return it?:ArrayList();
        }
    }
     fun truncateTable( openHelper: SupportSQLiteOpenHelper, tableName: String) {
        val database = SQLiteDatabase.openOrCreateDatabase(
            application.getDatabasePath(openHelper.databaseName), null
        )

        if (database != null) {
            database.execSQL(String.format("DELETE FROM %s;", tableName))
            database.execSQL(
                "UPDATE sqlite_sequence SET seq = 0 WHERE name = ?;",
                arrayOf(tableName)
            )

            Log.e("TAG", "Done $tableName")
        }
    }

    fun clearAllTable() {
        medicalDataBase.clearAllTables()
       /* truncateTable(
            medicalDataBase.openHelper,
            "QuestionModel"
        )
        var createSql= "CREATE TABLE IF NOT EXISTS `QuestionModel` (`questiontype` TEXT, `template` TEXT, `fail` TEXT, `defaultAnswer` TEXT, `isimagerequired` TEXT, `priority` TEXT, `comments` TEXT, `type` TEXT, `guidlines` TEXT, `subTittle` TEXT, `title` TEXT, `template_id` TEXT, `question_id` TEXT, `dealBreaker` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `answer` TEXT, `comment` TEXT, `isUploaded` INTEGER NOT NULL, `imageUris` TEXT, `dropDownItems` TEXT)";

        truncateTable(
            medicalDataBase.openHelper,
            "TemplateModel"
        )
        truncateTable(
            medicalDataBase.openHelper,
            "FeedBackModel"
        )
        truncateTable(
            medicalDataBase.openHelper,
            "PreliminaryInfoModel"
        )
        truncateTable(
            medicalDataBase.openHelper,
            "Facility"
        )
        truncateTable(
            medicalDataBase.openHelper,
            "room_master_table"
        )*/
    }

    companion object {
        lateinit var  APPLICATION: Application
    }
}