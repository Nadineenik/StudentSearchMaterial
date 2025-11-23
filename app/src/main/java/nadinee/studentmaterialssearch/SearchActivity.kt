/*package nadinee.studentmaterialssearch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nadinee.studentmaterialssearch.SimpleAdapter


class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val resultsRecyclerView = findViewById<RecyclerView>(R.id.resultsRecyclerView)

        // Настройка RecyclerView
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)
        val dummyData = mutableListOf("Книга 1", "Книга 2", "Книга 3")
        val adapter = SimpleAdapter(dummyData) { item ->
            // Переход в DetailsActivity при клике на элемент
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("item_name", item)
            startActivity(intent)
        }
        resultsRecyclerView.adapter = adapter

        // Поиск (для примера просто фильтруем dummyData)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            val filtered = dummyData.filter { it.contains(query, ignoreCase = true) }
            adapter.updateList(filtered)
        }
    }
}


 */