package nadinee.studentmaterialssearch.data

data class SerpStackResponse(
    val search_information: SearchInfo? = null,
    val organic_results: List<OrganicResult>? = null,
    val error: ErrorResponse? = null
)

data class SearchInfo(
    val total_results: Int? = null,
    val time_total: Double? = null
)

// data/SerpStackResponse.kt
data class OrganicResult(
    val position: Int? = null,
    val title: String? = null,
    val url: String? = null,        // ← ИЗМЕНИЛ: было link
    val domain: String? = null,
    val displayed_url: String? = null,
    val snippet: String? = null,
    val sitelinks: Sitelinks? = null,
    val cached_page_url: String? = null,
    val related_pages_url: String? = null,
    val prerender: Boolean? = null
)

data class Sitelinks(
    val inline: List<Sitelink>? = null,
    val expanded: List<Sitelink>? = null
)

data class Sitelink(
    val title: String? = null,
    val url: String? = null,
    val tracking_url: String? = null
)
data class ErrorResponse(
    val type: String? = null,
    val message: String? = null
)