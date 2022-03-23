package com.example.notbored

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getTask(@Url category: String): Response<Task>
}