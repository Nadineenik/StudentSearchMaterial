// SearchQueryEvent.kt — ПРОСТОЙ И НАДЁЖНЫЙ
package nadinee.studentmaterialssearch.screens

object SearchQueryEvent {
    private var pendingQuery: String? = null

    fun sendQuery(query: String) {
        pendingQuery = query.trim().takeIf { it.isNotBlank() }
    }

    fun consumePendingQuery(): String? {
        val result = pendingQuery
        pendingQuery = null
        return result
    }
}