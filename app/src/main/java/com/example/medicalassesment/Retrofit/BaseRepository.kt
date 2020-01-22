package com.example.medicalassesment.Retrofit

import android.util.Log
import retrofit2.Response
import java.io.IOException

 class BaseRepository {
     /*companion object{
         suspend fun <T : Any> safeApiCall(call : suspend()-> Response<T>, error : String) :  T?{

             Log.e("TAG","Safe Call" )

             val result = newsApiOutput(call, error)
             var output : T? = null
             when(result){
                 is Output.Success ->
                     output = result.output
                 is Output.Error -> Log.e("Error", "The $error and the ${result.error}")
             }
             return output

         }
         private suspend fun<T : Any> newsApiOutput(call: suspend()-> Response<T> , error: String) : Output<T>{
             val response = call.invoke()
             Log.e("TAG","Url "+response.raw().request().url())

             return if (response.isSuccessful)
                 Output.Success(response.body()!!)
             else
                 Output.Error(IOException("OOps .. Something went wrong due to  $error"))
         }
     }
*/
}