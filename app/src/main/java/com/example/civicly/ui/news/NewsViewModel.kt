package com.example.civicly.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicly.data.BiasRating
import com.example.civicly.data.NewsArticle
import com.example.civicly.data.SupabaseClient
import com.example.civicly.data.toUserMessage
import com.example.civicly.debug.DebugLog
import com.example.civicly.ui.bills.UiState
import retrofit2.HttpException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewsItem(val article: NewsArticle, val bias: BiasRating?)

class NewsViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<NewsItem>>>(UiState.Loading)
    val state: StateFlow<UiState<List<NewsItem>>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            // #region agent log
            DebugLog.log(
                location = "NewsViewModel.kt:load",
                message = "News load started",
                hypothesisId = "A",
            )
            // #endregion
            _state.value = try {
                coroutineScope {
                    val articlesDeferred = async { SupabaseClient.api.getNewsArticles() }
                    val biasesDeferred = async { SupabaseClient.api.getBiasRatings() }
                    val articles = articlesDeferred.await()
                    val biases = biasesDeferred.await()
                    val biasMap = biases.associateBy { it.sourceName }
                    // #region agent log
                    DebugLog.log(
                        location = "NewsViewModel.kt:load",
                        message = "News load succeeded",
                        data = mapOf(
                            "articleCount" to articles.size,
                            "biasCount" to biases.size,
                        ),
                        hypothesisId = "C",
                    )
                    // #endregion
                    UiState.Data(
                        articles.map { NewsItem(it, biasMap[it.sourceName]) }
                    )
                }
            } catch (e: Exception) {
                // #region agent log
                DebugLog.log(
                    location = "NewsViewModel.kt:load",
                    message = "News load failed",
                    data = mapOf(
                        "exceptionType" to e.javaClass.simpleName,
                        "message" to e.message,
                        "httpCode" to (e as? HttpException)?.code(),
                    ),
                    hypothesisId = "A,B,C,D,E",
                )
                // #endregion
                UiState.Error(e.toUserMessage("Failed to load news"))
            }
        }
    }
}
