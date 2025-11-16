package nadinee.studentmaterialssearch.network

import nadinee.studentmaterialssearch.data.SerpStackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SerpStackApi {
    @GET("search")
    suspend fun search(
        @Query("access_key") apiKey: String,
        @Query("query") query: String,
        @Query("num") num: Int = 10,  // Количество результатов
        @Query("country") country: String = "ru",  // Страна (RU для Яндекса/Google RU)
        @Query("language") language: String = "ru",  // Язык
        @Query("engine") engine: String? = null,  // "google", "yandex", "bing" и т.д.
        @Query("device") device: String? = null  // "desktop", "tablet", "mobile"
    ): SerpStackResponse
}
