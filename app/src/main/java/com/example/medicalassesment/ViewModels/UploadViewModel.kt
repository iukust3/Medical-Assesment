package com.example.medicalassesment.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.APIinterFace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.medicalassesment.Helper.PrefHelper
import com.example.medicalassesment.Retrofit.ApiClient
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.database.MedicalDataBase
import com.example.medicalassesment.models.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.StringBuilder


class UploadViewModel : ViewModel() {
    private val databaseRepository: MedicalDataBase = MedicalDataBase.getInstance()!!
    private val apInterFace: APIinterFace =
        ApiClient.getApiClient().create(APIinterFace::class.java)
    private val inspectionViewState: MutableLiveData<UploadingViewState> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        onError(exception)
        exception.printStackTrace()
    }

    private fun onError(exception: Throwable) {
        exception.printStackTrace()
        inspectionViewState.value = ErrorUploading("Error :" + exception.message)
    }

    fun startUpload(templateModel: TemplateModel) {
        viewModelScope.launch(coroutineExceptionHandler) {
            uploadTemplete(templateModel)
        }
    }

    private var templateID = 0;
    private suspend fun uploadTemplete(templateModel: TemplateModel) {
        var hashMap = LinkedHashMap<String, String>()
        hashMap["user_profile_id"] = MyApplication.USER.id.toString()
        hashMap["description"] = templateModel.description.toString()
        hashMap["template_id"] = templateModel.templateId.toString()
        hashMap["inspection_date"] =
            Utils.getFormattedDateSimple(Utils.getFormattedDateSimple(templateModel.inspectionConductedOn.toString()))
        hashMap["title"] = templateModel.title.toString()
        hashMap["status"] = templateModel.status.toString()
        hashMap["inspection_conducted_by"] = templateModel.inspectionConductedBy.toString()
        if (templateModel.inspectionConductedAt.isNullOrEmpty() || templateModel.inspectionConductedAt?.length == 0) {
            hashMap["inspection_conducted_at"] = "2020-07-05"
            Log.e("TAG", "inspection_conducted_at 2020-07-05 ")
        } else
            hashMap["inspection_conducted_at"] = templateModel.inspectionConductedAt.toString()
        hashMap["category"] = "1"
        hashMap["duration"] =
            PrefHelper(MyApplication.APPLICATION).getTime(templateModel.title.toString())
        var respos = apInterFace.insertTemplete(hashMap)
        var responseMsg = respos.body()?.string()
        if (respos.isSuccessful && respos.code() != 500) {
            try {
                Gson().toJsonTree(responseMsg)
                templateID = JSONObject(responseMsg).getInt("template_id")
                inspectionViewState.postValue(Uploading("Uploading Preliminary Info"))
                uploadPreliminaryInfo(templateModel)
                templateModel.templateId = templateID
                databaseRepository.getDao().update(templateModel)
                inspectionViewState.value = UploadingDone(templateModel)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else inspectionViewState.value = ErrorUploading("Error :" + respos.errorBody()!!.string())
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private suspend fun uploadPreliminaryInfo(templateModel: TemplateModel) {
        val list = databaseRepository.getDao()
            .getPrelimanryInfoNonlive(templateModel.id.toString())
        list.forEach {
            var base64String = StringBuilder("")
            it.imageUris?.let { _ ->
                var file = File(
                    Utils.getFolderPath(
                        MyApplication.APPLICATION,
                        it,
                        it.template_id
                    )
                )
                try {
                    file.listFiles().forEach { file1 ->
                        if (base64String.isNotEmpty()) {
                            base64String.append(" , " + convertImageToString(file1))
                        } else base64String.append(convertImageToString(file1))
                    }
                } catch (e: Exception) {
                }
            }
            if (!it.isUploaded())
                startUpload(base64String, it)
        }
        uploadQuestion(templateModel)
    }

    private suspend fun uploadQuestion(templateModel: TemplateModel) {
        val list = databaseRepository.getDao()
            .getQuestionsNonlive(templateModel.id.toString())
        Log.e("TAG", "qustion size ${list.size}")
        list.forEach {
            var base64String = StringBuilder("")
            if (!it.imageUris.isNullOrEmpty())
                it.imageUris?.let { _ ->
                    try {
                        var file = File(
                            Utils.getFolderPath(
                                MyApplication.APPLICATION,
                                it,
                                it.template_id
                            )
                        )
                        var listofImages=file.listFiles { file, s ->
                            run {
                                var nameSplit = s.split("_")
                                nameSplit.size == 3
                            }
                        }
                        listofImages.forEach { file1 ->
                            if (base64String.isNotEmpty()) {
                                base64String.append(" , " + convertImageToString(file1))
                            } else base64String.append(convertImageToString(file1))
                        }
                    } catch (e: Exception) {

                    }
                }
            if (!it.isUploaded())
                startUpload(base64String, it)
        }
        uploadFeedBack(templateModel)
    }

    private suspend fun uploadFeedBack(templateModel: TemplateModel) {
        val list = databaseRepository.getDao()
            .getFeedBackNonlive(templateModel.id.toString())
        list.forEach {
            var base64String = StringBuilder("")
            it.imageUris?.let { it1 ->
                try {
                    if (it1.size > 0) {
                        val file = File(
                            Utils.getFolderPath(
                                MyApplication.APPLICATION,
                                it,
                                it.template_id
                            )
                        )
                        file.listFiles().forEach { file1 ->
                            if (base64String.isNotEmpty()) {
                                base64String.append(" , " + convertImageToString(file1))
                            } else base64String.append(convertImageToString(file1))
                        }
                    }
                } catch (e: Exception) {
                }
            }
            if (!it.isUploaded())
                startUpload(base64String, it)
        }

    }

    private suspend fun convertImageToString(file: File): String {
        Log.e("TAG", " path " + file.absolutePath)
        var value = ""
        try {
            var bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap = getResizedBitmap(bitmap, 500)

            val baos = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                baos
            ) //bm is the bitmap object
            val b = baos.toByteArray()
            value = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (e: Exception) {
        }

        return value;
    }

    private suspend fun startUpload(image: StringBuilder, questionModel: BaseQustion) {
        if (questionModel.getAnswer().isNullOrEmpty()) {
            questionModel.setAnswer("")
        }
        if (questionModel.getAnswer() == "null") {
            questionModel.setAnswer("")
        }
        Log.e("TAG","Base Qustion type "+questionModel.baseQustionType)
        var map = LinkedHashMap<String, String>()
        when (questionModel.baseQustionType) {
            1 -> {
                map["question_id"] = questionModel.getQ_Id().toString()
                Log.e("TAG", "Qustion id ${questionModel.getQ_Id().toString()}")
            }
            2 -> map["fbs_id"] = questionModel.getQ_Id().toString()
            else -> map["pl_id"] = questionModel.getQ_Id().toString()
        }
        map["answer"] = questionModel.getAnswer().toString()
        map["image"] = image.toString()
        map["template_id"] = templateID.toString()
        var response: Response<ResponseBody>? = null

        response = when (questionModel.baseQustionType) {
            1 -> {
                map["comment"] = (questionModel as QuestionModel).comments.toString()
                apInterFace.uploadQustions(map)
            }
            2 -> apInterFace.uploadFeedBack(map)
            else -> apInterFace.uploadPrelemryInfo(map)
        }
        when (questionModel.baseQustionType) {
            1 -> inspectionViewState.value =
                Uploading("Uploading Question")
            2 -> inspectionViewState.value =
                Uploading("Uploading Feedback Summary")
            else -> inspectionViewState.value = Uploading("Uploading Preliminary Info")
        }


        if (response.code() == 500) {
            Log.e("TAG", "Qustion " + Gson().toJson(questionModel))
            Log.e("TAG", "map $map")
        } else if (response.code() == 200) {
            var jsonResponse = JSONObject(response.body()?.string())
            Log.e("TAG", "Qustion " + response.message())
            Log.e("TAG", "Qustion " + response.code())
            Log.e("TAG", "map $map")
            if (jsonResponse.getString("msg") == "Inserted Successfully")
                questionModel.setUploaded(true)
            else Log.e("TAG","Error while inserting question ${Gson().toJson(questionModel)}")
            when (questionModel.baseQustionType) {
                1 -> {
                    databaseRepository.getDao()
                        .update(questionModel as QuestionModel)
                }
                2 -> databaseRepository.getDao()
                    .update(
                        questionModel as FeedBackModel
                    )
                else -> databaseRepository.getDao()
                    .update(questionModel as PreliminaryInfoModel)
            }
        }
    }

    fun getViewState(): MutableLiveData<UploadingViewState> {
        return inspectionViewState
    }

    public fun saveBitmapToFile(file: File): File {
        try {

            // BitmapFactory options to downsize the image
            var o = BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            var inputStream = FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            var REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            var scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2;
            }

            var o2 = BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = FileInputStream(file);

            var selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            var outputStream = FileOutputStream(file);

            selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (e: Exception) {
            e.printStackTrace()
            return file;
        }
    }
}