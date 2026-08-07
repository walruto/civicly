package com.example.civicly.debug

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object DebugLog {
    private const val TAG = "CiviclyDebug"
    private const val ENDPOINT =
        "http://10.0.2.2:7259/ingest/d64f7f8d-cd5c-49b1-a7a3-a3b4c6cb6199"
    private const val SESSION_ID = "5de9a6"
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json".toMediaType()

    fun log(
        location: String,
        message: String,
        data: Map<String, Any?> = emptyMap(),
        hypothesisId: String,
        runId: String = "pre-fix",
    ) {
        val payload = JSONObject().apply {
            put("sessionId", SESSION_ID)
            put("location", location)
            put("message", message)
            put("hypothesisId", hypothesisId)
            put("runId", runId)
            put("timestamp", System.currentTimeMillis())
            put("data", JSONObject(data))
        }
        Log.d(TAG, "$location | $message | $data")
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val request = Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Debug-Session-Id", SESSION_ID)
                    .post(payload.toString().toRequestBody(jsonMediaType))
                    .build()
                client.newCall(request).execute().close()
            }
        }
    }
}
