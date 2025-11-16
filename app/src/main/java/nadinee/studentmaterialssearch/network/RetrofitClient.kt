// network/RetrofitClient.kt
package nadinee.studentmaterialssearch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://searx.thegreenwebfoundation.org/"  // РАБОЧИЙ

    val api: SearXApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearXApi::class.java)
    }
}