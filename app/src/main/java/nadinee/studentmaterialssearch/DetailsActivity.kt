/*package nadinee.studentmaterialssearch

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val favoriteButton = findViewById<Button>(R.id.favoriteButton)

        // Получаем данные из SearchActivity
        val itemName = intent.getStringExtra("item_name") ?: "Материал"
        titleTextView.text = itemName
        descriptionTextView.text = "Описание для $itemName"

        favoriteButton.setOnClickListener {
            // Позже сюда добавим сохранение в Room
            Toast.makeText(this, "$itemName добавлен в избранное", Toast.LENGTH_SHORT).show()
        }
    }
}


 */