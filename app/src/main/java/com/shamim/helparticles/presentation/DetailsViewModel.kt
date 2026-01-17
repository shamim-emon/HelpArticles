package com.shamim.helparticles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.data.network.ErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    private var _uiState: MutableStateFlow<DetailsUIState> = MutableStateFlow(DetailsUIState())
    val uiState: StateFlow<DetailsUIState> = _uiState.asStateFlow()


    fun onIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadArticleDetails -> {
                viewModelScope.launch {
                    repository.getArticleDetails(intent.id).collect {
                        when (it) {
                            is ApiResult.Loading -> {
                                _uiState.value = _uiState.value.copy(isLoading = true)
                            }

                            is ApiResult.Error -> {
                                _uiState.value =
                                    _uiState.value.copy(isLoading = false, error = it.errorResponse)

                            }

                            is ApiResult.Success<ArticleDetails> -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    articleDetails = it.data,
                                    error = null
                                )
                            }

                        }
                    }
                }
            }

        }
    }
}

data class DetailsUIState(
    val articleDetails: ArticleDetails = ArticleDetails.EMPTY,
    val isLoading: Boolean = false,
    val error: ErrorResponse? = null,
)

sealed class DetailsIntent {
    data class LoadArticleDetails(val id: Int) : DetailsIntent()
}
