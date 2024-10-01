package com.example.searchmovie

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class searchMovie : AppCompatActivity() {

    private lateinit var search: Button
    private lateinit var searchMovie: EditText
    private lateinit var movieName: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieRating: TextView

    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        search = findViewById(R.id.search)
        searchMovie = findViewById(R.id.searchMovie)
        movieName = findViewById(R.id.movieName)
        movieYear = findViewById(R.id.movieYear)
        movieRating = findViewById(R.id.movieRating)

        token = "9FF39M6-PZ34NZK-Q1MCYNC-M2127Y5"

        search.setOnClickListener {
            val query = searchMovie.text.toString()
            if (query.isNotEmpty()) {
                searchForMovie(query)  // Вызов функции поиска фильма при нажатии кнопки
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функция для выполнения запроса
    private fun searchForMovie(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.kinopoisk.dev/v1.4/movie/search?query=$searchMovie&token=$token") // Обновите URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(MovieApiService::class.java)

        apiService.searchMovie(query).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movie = response.body()

                    // Логирование полного ответа для диагностики
                    Log.d("MyLog", "Response Body: ${response.body()}")
                    Log.d("MyLog", "Response Code: ${response.code()}")
                    Log.d("MyLog", "Response Message: ${response.message()}")

                    // Обновляем UI с данными фильма
                    movieName.text = movie?.title ?: "Название недоступно"
                    movieYear.text = movie?.year.toString() ?: "Год недоступен"
                    movieRating.text = movie?.rating.toString() ?: "Рейтинг недоступен"
                } else {
                    Toast.makeText(this@searchMovie, "Ошибка в ответе сервера", Toast.LENGTH_SHORT).show()

                    // Логирование ошибки ответа сервера
                    Log.e("MyLog", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@searchMovie, "Ошибка запроса: ${t.message}", Toast.LENGTH_SHORT).show()

                // Логирование ошибки запроса
                Log.e("MyLog", "Failure: ${t.message}")
            }
        })
    }
}


// Интерфейс для API запросов
interface MovieApiService {
    @GET("movie/search") // Замените на правильный endpoint
    fun searchMovie(@Query("query") query: String): Call<MovieResponse>
}

// Класс для обработки ответа сервера
data class MovieResponse(
    val title: String,
    val year: Int,
    val rating: Double
)