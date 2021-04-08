package com.example.mylib.openapi.openweather

import com.example.mylib.openapi.OpenApi
import com.example.mylib.openapi.openweather.data.WeatherCast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "1d76ad95a3c6c918fb7335c192134683"

interface OpenWeatherService {
//    http://api.openweathermap.org/data/2.5/weather?q=Seoul&APPID=1d76ad95a3c6c918fb7335c192134683&lang=kr
    @GET("/data/2.5/weather")
    fun getWeatherCast( // Call 객체를 만드는 factory method
        @Query("q") city: String,
        @Query("APPID") apiKey: String = API_KEY,
        @Query("lang") lang: String = "kr"
    ): Call<WeatherCast> // 실제 호출하는 객체를 리턴

}

object OpenWeather: OpenApi() {
    override val TAG = javaClass.simpleName
    override val BASE_URL = "http://api.openweathermap.org"

    private val service = retrofit.create(OpenWeatherService::class.java)

    fun getWeatherCast(city: String, callback: (WeatherCast)->Unit) {
        service.getWeatherCast(city)
                .enqueue(ApiCallback<WeatherCast>(callback))
    }
}

//object OpenWeather {
//    val TAG = javaClass.simpleName
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("http://api.openweathermap.org")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    fun getService(): OpenWeatherService = retrofit.create(
//        OpenWeatherService::class.java)
//
//    fun getWeatherCast(city: String, callback: (WeatherCast)->Unit) {
//        getService()
//            .requestWeatherCast(city)
//            .enqueue(object: Callback<WeatherCast> {
//
//                override fun onFailure(call: Call<WeatherCast>, t: Throwable) {
//                    Log.e(TAG, t.toString())
//                }
//
//                override fun onResponse(
//                    call: Call<WeatherCast>,
//                    response: Response<WeatherCast>
//                ) {
//                    if (response.isSuccessful) {    // code: 200 체크
//                        val result = response.body()// body()의 리턴 타입: WeatherCast?
//                        callback(result!!)
//                    } else {
//                        Log.w(TAG, "${response.code()}, ${response.message()}")
//                        Log.w(TAG, "${response.toString()}")
//                    }
//                }
//            })
//    }
//}