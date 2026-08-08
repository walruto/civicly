package com.example.civicly.data

// NOTE: `id` fields typed as String. `bills` uses text PKs (e.g. "Prop 2"), the
// rest use uuid PKs (Supabase default) — both serialize fine as String.
// Field names are camelCase; GsonBuilder uses LOWER_CASE_WITH_UNDERSCORES so
// e.g. officialTitle <-> official_title automatically.

data class Bill(
    val id: String,
    val billType: String?,
    val electionDate: String?,
    val county: String?,
    val city: String?,
    val jurisdictionLevel: String?,
    val officialTitle: String?,
    val plainSummary: String?,
    val yesVoteMeans: String?,
    val noVoteMeans: String?,
    val fiscalImpactSummary: String?,
    val topicTags: List<String>?,
    val sourceUrl: String?,
    val dataStatus: String?,
    val lastVerified: String?,
)

data class NewsArticle(
    val id: String,
    val title: String,
    val url: String?,
    val sourceName: String?,
    val publishedAt: String?,
    val summary: String?,
    val relatedMeasureId: String?,
)

data class BiasRating(
    val id: String,
    val sourceName: String,
    val biasRating: String?,
    val factualRating: String?,
    val ratingSource: String?,
)

data class Candidate(
    val id: String,
    val name: String,
    val office: String?,
    val party: String?,
    val bio: String?,
    // jsonb column -> nested map, e.g. {"housing": "yimby", ...}
    val positions: Map<String, String>?,
)
