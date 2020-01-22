package com.example.medicalassesment.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalassesment.database.DatabaseRepository
import com.example.medicalassesment.MyApplication
import com.example.medicalassesment.Retrofit.APIinterFace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.medicalassesment.Utials.Utils
import com.example.medicalassesment.models.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.StringBuilder


class UploadViewModel @Inject constructor(
    private val apInterFace: APIinterFace,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val inspectionViewState: MutableLiveData<UploadingViewState> = MutableLiveData()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        onError(exception)
        Log.e("TAG", "Error " + exception.message)
        exception.printStackTrace()
    }

    private fun onError(exception: Throwable) {
        exception.printStackTrace()
        inspectionViewState.value = ErrorUploading("Error :" + exception.message)
    }

    fun startUpload(templateModel: TemplateModel) {
        viewModelScope.launch(coroutineExceptionHandler) {
            Log.e("TAG", " uploading start ")
            uploadTemplete(templateModel)
        }
    }

    private var templateID = 0;
    private suspend fun uploadTemplete(templateModel: TemplateModel) {
        var hashMap = LinkedHashMap<String, String>()
        hashMap["user_profile_id"] = MyApplication.USER.id.toString()
        hashMap["description"] = templateModel.description.toString()
        hashMap.put("template_id", templateModel.templateId.toString())
        hashMap.put(
            "inspection_date",
            Utils.getFormattedDateSimple(Utils.getFormattedDateSimple(templateModel.inspectionConductedOn.toString()))
        )
        hashMap.put("title", templateModel.title.toString())
        hashMap.put("status", templateModel.status.toString())
        hashMap.put("inspection_conducted_by", templateModel.inspectionConductedBy.toString())
        hashMap.put("inspection_conducted_at", templateModel.inspectionConductedAt.toString())
        hashMap.put("category", "1")
        var respos = apInterFace.upladTemplete(hashMap)
        var responseMsg = respos.body()?.string()
        if (respos.isSuccessful) {
            try {
                var element = Gson().toJsonTree(responseMsg)
                templateID = JSONObject(responseMsg).getInt("template_id")
                inspectionViewState.value = Uploading("Uploading Preliminary Info")
                uploadPrelemanryInfo(templateModel)
                templateModel.templateId = templateID
                databaseRepository.medicalDataBase.getDao().update(templateModel)
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

    private suspend fun uploadPrelemanryInfo(templateModel: TemplateModel) {
        val list = databaseRepository.medicalDataBase.getDao()
            .getPrelimanryInfoNonlive(templateModel.id.toString())
        list.forEach {
            var base64String = StringBuilder("")
            it.imageUris?.let { _ ->
                var file = File(
                    Utils.getFolderPath(
                        databaseRepository.application,
                        it.getId(),
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
        uploadQustion(templateModel)
    }

    private suspend fun uploadQustion(templateModel: TemplateModel) {
        val list = databaseRepository.medicalDataBase.getDao()
            .getQuestionsNonlive(templateModel.id.toString())

        list.forEach {
            var base64String = StringBuilder("")
            it.imageUris?.let { _ ->
                try {
                    var file = File(
                        Utils.getFolderPath(
                            databaseRepository.application,
                            it.getId(),
                            it.template_id
                        )
                    )
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
        uploadFeedBack(templateModel)
    }

    private suspend fun uploadFeedBack(templateModel: TemplateModel) {
        val list = databaseRepository.medicalDataBase.getDao()
            .getFeedBackNonlive(templateModel.id.toString())
        list.forEach {
            var base64String = StringBuilder("")
            it.imageUris?.let { it1 ->
                try {
                    if (it1.size > 0) {
                        val file = File(
                            Utils.getFolderPath(
                                databaseRepository.application,
                                it.getId(),
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
        var map = LinkedHashMap<String, String>()
        when {
            questionModel.baseQustionType == 1 -> map["question_id"] =
                questionModel.getQ_Id().toString()
            questionModel.baseQustionType == 2 -> map["fbs_id"] = questionModel.getQ_Id().toString()
            else -> map["pl_id"] = questionModel.getQ_Id().toString()
        }
        map["answer"] = questionModel.getAnswer().toString()
        map["image"] = image.toString()
        map["template_id"] = templateID.toString()
        var response: Response<ResponseBody>? = null
        when {
            questionModel.baseQustionType == 1 ->
                response = apInterFace.uploadQustions(map)
            questionModel.baseQustionType == 2 -> response = apInterFace.uploadFeedBack(map)
            else -> response = apInterFace.uploadPrelemryInfo(map)
        }
        when {
            questionModel.baseQustionType == 1 -> inspectionViewState.value =
                Uploading("Uploading Question")
            questionModel.baseQustionType == 2 -> inspectionViewState.value =
                Uploading("Uploading Feedback Summary")
            else -> inspectionViewState.value = Uploading("Uploading Preliminary Info")
        }


        if (response.code() == 500) {
            Log.e("TAG", "Qustion " + Gson().toJson(questionModel))
            Log.e("TAG", "map " + map.toString())
        } else if (response.code() == 200) {
            questionModel.setUploaded(true)
            when {
                questionModel.baseQustionType == 1 -> {
                    databaseRepository.medicalDataBase.getDao()
                        .update(questionModel as QuestionModel)
                }
                questionModel.baseQustionType == 2 -> databaseRepository.medicalDataBase.getDao().update(
                    questionModel as FeedBackModel
                )
                else -> databaseRepository.medicalDataBase.getDao().update(questionModel as PreliminaryInfoModel)

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

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (e: Exception) {
            e.printStackTrace()
            return file;
        }
    }
}