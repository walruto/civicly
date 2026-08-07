package com.example.civicly.data

import com.example.civicly.debug.DebugLog
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://fmlegfjtpryuifixnnrp.supabase.co/"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZtbGVnZmp0cHJ5dWlmaXhubnJwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODYxMjkxODYsImV4cCI6MjEwMTcwNTE4Nn0.1l70HxM9HJr6GKdSa8QZC3xphz3P0rbx_w9ta7T71_w"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(req)
        }
        // #region agent log
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val bodySnippet = response.peekBody(512).string()
            DebugLog.log(
                location = "SupabaseClient.kt:interceptor",
                message = "Supabase HTTP response",
                data = mapOf(
                    "url" to request.url.toString(),
                    "method" to request.method,
                    "statusCode" to response.code,
                    "success" to response.isSuccessful,
                    "bodySnippet" to bodySnippet,
                ),
                hypothesisId = "A,E",
            )
            response
        }
        // #endregion
        .build()

    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    val api: SupabaseApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(SupabaseApi::class.java)
}
