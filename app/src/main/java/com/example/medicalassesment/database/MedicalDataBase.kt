package com.example.medicalassesment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medicalassesment.models.*

@Database(
    entities = [QuestionModel::class, TemplateModel::class, FeedBackModel::class, PreliminaryInfoModel::class, Facility::class],
    version = 1,
    exportSchema = true
)
abstract class MedicalDataBase : RoomDatabase() {
    abstract fun getDao(): Dao


    companion object {

        @Volatile
        private var INSTANCE: MedicalDataBase? = null

        fun getInstance(context: Context): MedicalDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        fun getInstance(): MedicalDataBase? = INSTANCE
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MedicalDataBase::class.java, "Medical.db"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}