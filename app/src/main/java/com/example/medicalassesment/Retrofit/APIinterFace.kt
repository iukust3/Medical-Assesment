package com.example.medicalassesment.Retrofit

import com.example.medicalassesment.database.Template_questions
import com.example.medicalassesment.models.Fecilities
import com.example.medicalassesment.models.UserModel
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIinterFace {
    @GET("userprofile")
    suspend fun Login(
        @Query("email", encoded = true) userName: String, @Query(
            "password",
            encoded = true
        ) password: String
    ): Response<UserModel>

    @GET
    suspend fun getQuestionsForTamplete(@Url url: String): Response<Template_questions>

    @GET("templates")
    suspend fun getUserInspection(): ResponseBody

    @FormUrlEncoded
    @POST("savedtemplate")
    suspend fun upladTemplete(@FieldMap(encoded = true) url: HashMap<String, String>): Response<ResponseBody>


    @POST("savequestion")
    suspend fun uploadQustions(
        @Part("question_id") q_id: RequestBody?, @Part("answer") answer: RequestBody?, @Part("image") image: RequestBody?, @Part(
            "template_id"
        ) templeteID: RequestBody?
    ): Response<ResponseBody>


    @POST("savequestion")
    suspend fun uploadQustions(
        @Query("question_id") q_id: String?, @Query("answer") answer: String?, @Query("image") image: String?, @Query(
            "template_id"
        ) templeteID: String?
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("savequestion")
    suspend fun uploadQustions(@FieldMap map: HashMap<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("savefbs")
    suspend fun uploadFeedBack(@FieldMap map: HashMap<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("savepi")
    suspend fun uploadPrelemryInfo(@FieldMap map: HashMap<String, String>): Response<ResponseBody>

    @GET("statelgas")
    suspend fun getState(): Response<ResponseBody>

    @GET
    suspend fun getSavedTempletes(@Url userId: String): Response<ResponseBody>

    @GET("categories")
    suspend fun getCatogaries(): Response<ResponseBody>

    @GET("facility")
    suspend fun getFecilities(): Response<Fecilities>
}