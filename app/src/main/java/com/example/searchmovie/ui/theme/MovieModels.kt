package com.example.searchmovie.ui.theme

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("docs") val movies: List<Movie>
)

data class Movie(
    @SerializedName("name") val name: String,
    @SerializedName("year") val year: Int,
    @SerializedName("rating") val rating: Rating,
    @SerializedName("poster") val poster: Poster // Используйте новый класс Poster
)

data class Poster(
    @SerializedName("url") val url: String // Предполагаем, что у объекта есть поле url
)

data class Rating(
    @SerializedName("kp") val kp: Double
)
