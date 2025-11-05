package nadinee.studentmaterialssearch.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    val title: String,
    val url: String,
    val content: String
) : Parcelable