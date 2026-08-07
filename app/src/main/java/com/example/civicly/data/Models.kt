package com.example.civicly.data

// NOTE: `id` fields typed as String assuming uuid PKs (Supabase default).
// If any table uses bigint id, change that field to Long.

data class Bill(
    val id: String,
    val billType: String?,
    val title: String,
    val summary: String?,
    val status: String?,
    val fullTextUrl: String?,
    val jurisdictionLevel: String?,
    val ocdId: String?,
    val source: String,
    val externalId: String,
    val sponsorId: String?,
    val electionId: String?,
    val lastActionDate: String?,
)

data class NewsArticle(
    val id: String,
    val sourceDomain: String,
    val title: String,
    val url: String,
    val publishedAt: String?,
    val topicTags: List<String>?,
    val relatedElectionId: String?,
    val relatedBillId: String?,
    val summary: String?,
)

data class BiasRating(
    val sourceDomain: String,
    val provider: String?,
    val lean: String,
    val reliabilityScore: Double?,
    val updatedAt: String?,
)

data class Candidate(
    val id: String,
    val name: String,
    val party: String?,
    val office: String?,
    val ocdId: String?,
    val incumbent: Boolean?,
    // jsonb column -> nested map, e.g. {"housing": "yimby", ...}
    val positions: Map<String, String>?,
)
