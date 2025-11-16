package nadinee.studentmaterialssearch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import nadinee.studentmaterialssearch.data.OrganicResult
import nadinee.studentmaterialssearch.data.SerpStackResponse
import nadinee.studentmaterialssearch.data.SearchResult

// network/SerpStackClient.kt
// network/SerpStackClient.kt
object SerpStackClient {
    private const val API_KEY = "8f01dfa59291f7df5626368121a0f8c3"
    private const val BASE_URL = "https://api.serpstack.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: SerpStackApi = retrofit.create(SerpStackApi::class.java)

    suspend fun search(query: String, engine: String? = "google"): List<SearchResult> {
        val response = api.search(API_KEY, query, engine = engine)

        // Проверка на ошибку
        response.error?.let { error ->
            throw Exception("Serpstack: ${error.message ?: "Unknown error"}")
        }

        // Проверка на пустые результаты
        val results = response.organic_results
        if (results.isNullOrEmpty()) {
            throw Exception("Нет результатов для запроса: $query")
        }

        return results.map { result ->
            SearchResult(
                title = result.title ?: "Без заголовка",
                url = result.url ?: "",  // ← ИЗМЕНИЛ: было result.link
                content = result.snippet ?: "Без описания"
            )
        }
    }
}