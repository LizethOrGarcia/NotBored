package com.example.notbored

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getRandomTask(@Url activity: String): Response<Hobby>

    @GET
    suspend fun getTaskByCategory(@Url category: String): Response<Hobby>

    @GET
    suspend fun getTaskByPrice(@Url category: String, @Url price: Float): Response<Hobby>
}