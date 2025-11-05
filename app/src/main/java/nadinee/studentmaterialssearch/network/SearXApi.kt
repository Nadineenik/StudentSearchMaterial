package nadinee.studentmaterialssearch.network

import nadinee.studentmaterialssearch.data.SearXResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearXApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("categories") categories: String = "science",  // Образование
        @Query("engines") engines: String = "google,wikipedia"
    ): SearXResponse
}