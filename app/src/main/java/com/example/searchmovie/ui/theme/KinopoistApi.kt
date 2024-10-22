package com.example.searchmovie.ui.theme

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KinopoiskApi {
    @GET("v1.4/movie/search")
    fun searchMovie(@Query("query") movieName: String, @Query("token") token: String): Call<MovieResponse>
}
