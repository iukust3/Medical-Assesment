package com.example.medicalassesment.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.medicalassesment.models.*

@Dao
interface Dao {

    @Query("SELECT * FROM QuestionModel WHERE template_id=:tampletId")
    fun getQuestions(tampletId: String): LiveData<List<QuestionModel>>

    @Query("SELECT * FROM QuestionModel WHERE template_id=:tampletId ORDER BY id ASC")
    fun getQuestionsNonlive(tampletId: String): List<QuestionModel>

    @Query("SELECT * FROM QuestionModel WHERE template_id=:tampletId AND (`fail` IS NULL OR `fail`!='0' OR `fail`!='1')")
    fun getQuestionsNonFail(tampletId: String): List<QuestionModel>


    @Query("SELECT * FROM QuestionModel WHERE template_id=:tampletId AND `fail`==`answer` ")
    fun getFaildItems(tampletId: String): List<QuestionModel>

    @Query("SELECT * FROM QuestionModel WHERE template_id=:tampletId AND `fail`==`answer` AND dealBreaker=:dealBreaker")
    fun getFaildItems(tampletId: String, dealBreaker: String): List<QuestionModel>

    @Query("SELECT * FROM FeedBackModel WHERE template_id=:tampletId")
    fun getFeedBackNonlive(tampletId: String): List<FeedBackModel>

    @Query("SELECT * FROM PreliminaryInfoModel WHERE template_id=:tampletId ")
    fun getPrelimanryInfoNonlive(tampletId: String): List<PreliminaryInfoModel>

    @Query("SELECT * FROM PreliminaryInfoModel WHERE template_id=:tampletId AND questiontype!='default'")
    fun getPrelimanryInfoNonDefult(tampletId: String): List<PreliminaryInfoModel>

    @Query("SELECT * FROM FeedBackModel WHERE template_id=:tampletId")
    fun getFeedBack(tampletId: String): LiveData<List<FeedBackModel>>
@Query("SELECT * FROM FeedBackModel WHERE id=:tampletId")
    fun getFeedBack(tampletId: Int): FeedBackModel

    @Query("SELECT * FROM PreliminaryInfoModel WHERE template_id=:tampletId AND questiontype!='default'")
    fun getPrelimanryInfo(tampletId: String): LiveData<List<PreliminaryInfoModel>>

    @Query("SELECT * FROM PreliminaryInfoModel WHERE template_id=:tampletId AND questiontype='default'")
    fun getDefaultInfo(tampletId: String): List<PreliminaryInfoModel>


    @Query("SELECT * FROM QuestionModel")
    fun getQuestions(): List<QuestionModel>

    @Delete
    suspend fun deleteQuestion(questionModel: QuestionModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQustions(list: List<QuestionModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedBack(list: List<FeedBackModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreliminaryInfo(list: List<PreliminaryInfoModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: Facility)

    @Update
    suspend fun update(model: Facility);

    @Query("SELECT * FROM Facility")
    suspend fun getAllFacilities(): List<Facility>

    @Query("SELECT * FROM Facility WHERE id=:id")
    fun getFacility(id: Int): Facility

    @Query("SELECT * FROM Facility WHERE name =:name")
    fun getFacility(name: String): Facility

    @Update
    suspend fun update(questionModel: QuestionModel)

    @Update
    suspend fun update(questionModel: TemplateModel)

    @Update
    suspend fun update(questionModel: FeedBackModel)

    @Update
    suspend fun update(questionModel: PreliminaryInfoModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTamplate(templateModel: TemplateModel): Long

    @Query("SELECT * FROM TemplateModel")
    suspend fun getTemplate(): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status='completed' AND isUploaded=:isUploaded")
    suspend fun getTemplate(isUploaded: Boolean): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status=:status")
    suspend fun getTemplate(status: String): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status='completed' OR status='InProgress' AND  title LIKE :filter")
    suspend fun getDeshboardTemplate(filter: String): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status='completed' OR status='InProgress'")
    suspend fun getDeshboardAllTemplate(): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status=:status AND  title LIKE :filter")
    suspend fun getTemplate(status: String, filter: String): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE status=:status")
    fun getTamplateLive(status: String): LiveData<List<TemplateModel>>

    @Query("SELECT * FROM TemplateModel WHERE templateId=:id")
    suspend fun getTemplate(id: Int): List<TemplateModel>

    @Query("SELECT * FROM TemplateModel WHERE templateId=:id and status=:status")
    suspend fun getTemplate(id: Int, status: String): List<TemplateModel>

    @Query("SELECT * FROM PreliminaryInfoModel WHERE template_id=:id and title=:title")
    suspend fun getPrelemnoryInfo(id: String, title: String): PreliminaryInfoModel


}