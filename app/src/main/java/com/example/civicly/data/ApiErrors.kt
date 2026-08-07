package com.example.civicly.data

import retrofit2.HttpException

fun Exception.toUserMessage(fallback: String): String {
    val http = this as? HttpException ?: return message ?: fallback
    val body = http.response()?.errorBody()?.string().orEmpty()
    return when {
        http.code() == 404 && "PGRST205" in body ->
            "Supabase tables are missing. Run supabase/schema.sql in your Supabase SQL Editor, then retry."
        http.code() == 401 || http.code() == 403 ->
            "Supabase rejected the API key. Check the anon key in SupabaseClient.kt."
        body.isNotBlank() -> body
        else -> "HTTP ${http.code()}: ${http.message()}"
    }
}
