package com.example.civicly.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicly.data.Candidate
import com.example.civicly.data.SupabaseClient
import com.example.civicly.ui.bills.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnswerOption(val value: String, val label: String)
data class Question(val issueKey: String, val prompt: String, val options: List<AnswerOption>)
data class MatchResult(val candidate: Candidate, val scorePct: Int, val matchedIssues: List<String>)

class MatchViewModel : ViewModel() {

    // issueKey values must match keys in candidate.positions JSON.
    val questions: List<Question> = listOf(
        Question(
            issueKey = "housing",
            prompt = "What's your view on new housing development?",
            options = listOf(
                AnswerOption("yimby", "Build more housing everywhere"),
                AnswerOption("nimby", "Preserve existing neighborhood character"),
                AnswerOption("mixed", "Balance growth with local input"),
            ),
        ),
        Question(
            issueKey = "climate",
            prompt = "How should we tackle climate change?",
            options = listOf(
                AnswerOption("strong_action", "Bold government action now"),
                AnswerOption("market_solutions", "Market-driven innovation"),
                AnswerOption("status_quo", "Continue the current course"),
            ),
        ),
        Question(
            issueKey = "taxes",
            prompt = "Your approach to taxes?",
            options = listOf(
                AnswerOption("raise_corporate", "Raise taxes on corporations and the wealthy"),
                AnswerOption("cut_taxes", "Cut taxes across the board"),
                AnswerOption("current", "Keep the current tax structure"),
            ),
        ),
        Question(
            issueKey = "policing",
            prompt = "Approach to public safety?",
            options = listOf(
                AnswerOption("reform", "Reform police, invest in community services"),
                AnswerOption("status_quo", "Maintain current policing"),
                AnswerOption("expand", "Expand police funding"),
            ),
        ),
        Question(
            issueKey = "education",
            prompt = "Top priority for K-12 education?",
            options = listOf(
                AnswerOption("increase_funding", "Increase public school funding"),
                AnswerOption("school_choice", "Expand school choice / vouchers"),
                AnswerOption("current", "Focus on curriculum quality"),
            ),
        ),
    )

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, String>>(emptyMap())
    val answers: StateFlow<Map<String, String>> = _answers.asStateFlow()

    private val _candidates = MutableStateFlow<UiState<List<Candidate>>>(UiState.Loading)
    val candidates: StateFlow<UiState<List<Candidate>>> = _candidates.asStateFlow()

    fun answer(issueKey: String, value: String) {
        _answers.value = _answers.value + (issueKey to value)
    }

    fun next() {
        if (_index.value < questions.lastIndex) _index.value += 1
    }

    fun back() {
        if (_index.value > 0) _index.value -= 1
    }

    fun reset() {
        _index.value = 0
        _answers.value = emptyMap()
    }

    // Demo shortcut: compute matches client-side only. No writeback to user_candidate_match.
    fun loadCandidates() {
        _candidates.value = UiState.Loading
        viewModelScope.launch {
            _candidates.value = try {
                UiState.Data(SupabaseClient.api.getCandidates())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load candidates")
            }
        }
    }

    fun computeMatches(candidates: List<Candidate>): List<MatchResult> {
        val a = _answers.value
        val total = a.size
        if (total == 0) return emptyList()
        return candidates.map { c ->
            val positions = c.positions.orEmpty()
            val matched = a.entries.filter { (k, v) -> positions[k] == v }.map { it.key }
            MatchResult(c, (matched.size * 100) / total, matched)
        }.sortedByDescending { it.scorePct }
    }
}
