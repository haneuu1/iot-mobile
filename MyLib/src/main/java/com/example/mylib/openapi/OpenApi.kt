package com.example.mylib.openapi

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class OpenApi {// 부모 클래스로 사용 open
    // 자식이 오버라이드하면서 재정의
    open val TAG = ""
    open val BASE_URL = ""

    protected val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    inner class ApiCallback<T>(val callback: (T)->Unit): Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e(TAG, t.toString())
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val result = response.body()
                callback(result!!)
            } else {
                Log.w(TAG, "${response.code()}, ${response.message()}")
                Log.w(TAG, "${response.toString()}")
            }
        }
    }
}