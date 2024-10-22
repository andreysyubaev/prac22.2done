package com.example.searchmovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.searchmovie.ui.theme.KinopoiskApi
import com.example.searchmovie.ui.theme.Movie
import com.example.searchmovie.ui.theme.MovieResponse
import com.example.searchmovie.ui.theme.Poster
import com.example.searchmovie.ui.theme.Rating
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class searchMovie : AppCompatActivity() { // Измените имя класса на SearchMovie с заглавной буквы

    private lateinit var api: KinopoiskApi
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie) // Убедитесь, что у вас есть этот XML файл

        sharedPreferences = getSharedPreferences("search_history", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.kinopoisk.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(KinopoiskApi::class.java)

        val searchButton: Button = findViewById(R.id.search)
        val searchEditText: EditText = findViewById(R.id.searchMovie)

        searchButton.setOnClickListener {
            val movieName = searchEditText.text.toString().trim()
            if (movieName.isNotEmpty()) { // Проверяем, что строка не пустая
                val call = api.searchMovie(movieName, "9FF39M6-PZ34NZK-Q1MCYNC-M2127Y5") // Замените на ваш токен

                call.enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                        if (response.isSuccessful && response.body() != null && response.body()!!.movies.isNotEmpty()) {
                            val movie = response.body()!!.movies[0]
                            updateUI(movie)
                            saveSearchHistory(movie.name)
                        } else {
                            Log.e("SearchMovieActivity", "Фильм не найден или пустой ответ")
                            Toast.makeText(this@searchMovie, "Фильм не найден", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        Log.e("SearchMovieActivity", "Ошибка: ${t.message}")
                        Toast.makeText(this@searchMovie, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }

        val editTextView: TextView = findViewById(R.id.editOn)
        val acceptButton: Button = findViewById(R.id.changeAccept)

        editTextView.setOnClickListener {
            acceptButton.visibility = View.VISIBLE
            val movieName: TextView = findViewById(R.id.movieName)
            val movieYear: TextView = findViewById(R.id.movieYear)
            val movieRating: TextView = findViewById(R.id.movieRating)

            enableEditing(movieName.text.toString(), movieYear.text.toString(), movieRating.text.toString())
        }

        acceptButton.setOnClickListener {
            acceptButton.visibility = View.GONE
            saveEditedData()
        }
    }

    private fun updateUI(movie: Movie) {
        Log.d("SearchMovieActivity", "Обновление UI с данными фильма")
        val movieName: TextView = findViewById(R.id.movieName)
        val movieYear: TextView = findViewById(R.id.movieYear)
        val movieRating: TextView = findViewById(R.id.movieRating)
        val movieImage: ImageView = findViewById(R.id.movieImage)

        movieName.text = movie.name
        movieYear.text = movie.year.toString()
        movieRating.text = "Рейтинг: ${movie.rating.kp}"

        // Загружаем изображение
        Glide.with(this)
            .load(movie.poster.url) // Используйте правильный URL изображения
            .placeholder(R.drawable.placeholder) // Замените на свой ресурс
            .error(R.drawable.error) // Замените на свой ресурс
            .into(movieImage)
    }

    private fun enableEditing(name: String, year: String, rating: String) {
        val movieNameEditText: EditText = findViewById(R.id.movieNameEditText)
        val movieYearEditText: EditText = findViewById(R.id.movieYearEditText)
        val movieRatingEditText: EditText = findViewById(R.id.movieRatingEditText)

        // Установите текущее значение
        movieNameEditText.setText(name)
        movieYearEditText.setText(year)
        movieRatingEditText.setText(rating)

        // Сделаем EditText видимыми и разрешим редактирование
        movieNameEditText.visibility = View.VISIBLE
        movieYearEditText.visibility = View.VISIBLE
        movieRatingEditText.visibility = View.VISIBLE
    }

    private fun saveEditedData() {
        val movieNameEditText: EditText = findViewById(R.id.movieNameEditText)
        val movieYearEditText: EditText = findViewById(R.id.movieYearEditText)
        val movieRatingEditText: EditText = findViewById(R.id.movieRatingEditText)

        // Получаем новые данные из EditText
        val newMovieName = movieNameEditText.text.toString()
        val newMovieYear = movieYearEditText.text.toString().toIntOrNull() ?: 0
        val newMovieRating = movieRatingEditText.text.toString().toDoubleOrNull() ?: 0.0

        // Здесь вы можете сохранить данные в базу данных или в SharedPreferences
        editor.putString("movie_name", newMovieName)
        editor.putInt("movie_year", newMovieYear)
        editor.putFloat("movie_rating", newMovieRating.toFloat())
        editor.apply()

        // Обновляем интерфейс
        updateUI(Movie(newMovieName, newMovieYear, Rating(newMovieRating), Poster("url")))

        // Скрываем EditText после сохранения
        movieNameEditText.visibility = View.GONE
        movieYearEditText.visibility = View.GONE
        movieRatingEditText.visibility = View.GONE
    }

    private fun saveSearchHistory(movieName: String) {
        val history = sharedPreferences.getStringSet("history", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        history.add(movieName)
        editor.putStringSet("history", history).apply()

        // Добавьте лог для проверки сохранения
        Log.d("SearchMovieActivity", "История поиска обновлена: $history")
    }


    fun displaySearchHistory(view: View) {
        val intent = Intent(this, history::class.java)
        startActivity(intent)
    }
}
