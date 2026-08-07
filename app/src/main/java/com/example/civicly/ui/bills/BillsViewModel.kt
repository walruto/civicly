package com.example.civicly.ui.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicly.data.Bill
import com.example.civicly.data.SupabaseClient
import com.example.civicly.data.toUserMessage
import com.example.civicly.debug.DebugLog
import retrofit2.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Data<T>(val value: T) : UiState<T>
}

class BillsViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Bill>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Bill>>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            // #region agent log
            DebugLog.log(
                location = "BillsViewModel.kt:load",
                message = "Bills load started",
                hypothesisId = "A",
            )
            // #endregion
            _state.value = try {
                val bills = SupabaseClient.api.getBills()
                // #region agent log
                DebugLog.log(
                    location = "BillsViewModel.kt:load",
                    message = "Bills load succeeded",
                    data = mapOf("count" to bills.size),
                    hypothesisId = "C",
                )
                // #endregion
                UiState.Data(bills)
            } catch (e: Exception) {
                // #region agent log
                DebugLog.log(
                    location = "BillsViewModel.kt:load",
                    message = "Bills load failed",
                    data = mapOf(
                        "exceptionType" to e.javaClass.simpleName,
                        "message" to e.message,
                        "httpCode" to (e as? HttpException)?.code(),
                    ),
                    hypothesisId = "A,B,C,D,E",
                )
                // #endregion
                UiState.Error(e.toUserMessage("Failed to load bills"))
            }
        }
    }
}
