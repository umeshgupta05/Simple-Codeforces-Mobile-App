package com.example.codeforces.data.api

import com.example.codeforces.data.model.ExecutionRequest
import com.example.codeforces.data.model.ExecutionResult
import com.example.codeforces.data.model.TestCase
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BackendApiService {
    @POST("execute")
    suspend fun execute(@Body payload: ExecutionRequest): ExecutionResult

    @GET("problems/{problemId}/tests")
    suspend fun getTests(@Path("problemId") problemId: String): List<TestCase>

    @POST("problems/{problemId}/tests")
    suspend fun addTest(
        @Path("problemId") problemId: String,
        @Body test: TestCase
    ): TestCase
}

