package com.example.civicly.data

import retrofit2.http.GET
import retrofit2.http.Query

interface SupabaseApi {
    @GET("rest/v1/bills")
    suspend fun getBills(
        @Query("select") select: String = "*",
        @Query("order") order: String = "election_date.desc",
        @Query("limit") limit: Int = 100,
        // Pass as "eq.<id>" to fetch one specific bill, e.g. getBills(id = "eq.Prop 2", limit = 1)
        @Query("id") id: String? = null,
    ): List<Bill>

    @GET("rest/v1/articles")
    suspend fun getNewsArticles(
        @Query("select") select: String = "*",
        @Query("order") order: String = "published_at.desc",
        @Query("limit") limit: Int = 100,
        // Pass as "eq.<id>" to fetch one specific article.
        @Query("id") id: String? = null,
    ): List<NewsArticle>

    @GET("rest/v1/bias_ratings")
    suspend fun getBiasRatings(
        @Query("select") select: String = "*",
    ): List<BiasRating>

    @GET("rest/v1/candidates")
    suspend fun getCandidates(
        @Query("select") select: String = "*",
        @Query("order") order: String = "office.asc",
        @Query("limit") limit: Int = 200,
    ): List<Candidate>
}
