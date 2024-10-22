package com.example.searchmovie

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class history : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyListView: ListView
    private lateinit var historyAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyListView = findViewById(R.id.historyListView)
        sharedPreferences = getSharedPreferences("search_history", MODE_PRIVATE)

        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        val historySet = sharedPreferences.getStringSet("history", mutableSetOf()) ?: mutableSetOf()
        val historyList = historySet.toList()

        if (historyList.isNotEmpty()) {
            // Устанавливаем адаптер для отображения истории
            historyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historyList)
            historyListView.adapter = historyAdapter

            // Лог для проверки
            Log.d("HistoryActivity", "Фильмы в истории: $historyList")
        } else {
            // Временно используем обычный массив для тестирования
            val testList = arrayOf("Тест 1", "Тест 2", "Тест 3")
            historyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, testList)
            historyListView.adapter = historyAdapter

            Toast.makeText(this, "История пуста", Toast.LENGTH_SHORT).show()
        }
    }

}

