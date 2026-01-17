package com.shamim.helparticles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.data.network.ErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ArticleRepository) : ViewModel() {
    private var _uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect: SharedFlow<HomeSideEffect> = _sideEffect.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    filterArticles(query)
                }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDetails -> {
                emitSideEffect(HomeSideEffect.NavigateToDetails(articleId = intent.id))
            }

            is HomeIntent.LoadArticles -> {
                viewModelScope.launch {
                    loadArticles()
                }
            }

            is HomeIntent.FilterArticles -> {
                _searchQuery.value = intent.query
            }
        }
    }

    private suspend fun loadArticles() {
        repository.getArticles().collect {
            when (it) {
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.errorResponse)
                }

                is ApiResult.Success<List<Article>> -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        allArticles = it.data,
                        filteredArticles = it.data,
                        error = null
                    )
                }

                is ApiResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)

                }
            }
        }
    }

    private fun filterArticles(query: String) {
        val fullList = _uiState.value.allArticles
        val filtered = if (query.isBlank()) {
            fullList
        } else {
            fullList.filter { it.title.contains(query, ignoreCase = true) }
        }
        _uiState.value = _uiState.value.copy(filteredArticles = filtered)
    }

    private fun emitSideEffect(effect: HomeSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }
}

data class HomeUIState(
    val allArticles: List<Article> = emptyList(),
    val filteredArticles: List<Article> = emptyList(),
    val isLoading: Boolean = true,
    val error: ErrorResponse? = null,
)

sealed class HomeIntent {
    data object LoadArticles : HomeIntent()
    data class LoadDetails(val id: Int) : HomeIntent()
    data class FilterArticles(val query: String) : HomeIntent()
}

sealed class HomeSideEffect {
    data class NavigateToDetails(val articleId: Int) : HomeSideEffect()
}