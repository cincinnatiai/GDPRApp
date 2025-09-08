package com.cincinnatiai.gdpr.ui.currentuser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cincinnatiai.gdpr.GDPRLibrary
import com.cincinnatiai.gdpr.model.DeleteRequest
import com.cincinnatiai.gdpr.model.RequestType
import com.cincinnatiai.gdpr.usecase.CreateDeleteRequestUseCase
import com.cincinnatiai.gdpr.usecase.CreateInformationRequestUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.onFailure

class CurrentUserViewModel(
    private val infoRequestUseCase: CreateInformationRequestUseCase,
    private val createDeleteUseCase: CreateDeleteRequestUseCase,
    private val clientId: String,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<CurrentUserUIState>(CurrentUserUIState())
    val state: StateFlow<CurrentUserUIState> = _state

    fun requestInformation(email: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                infoRequestUseCase.invoke(clientId, RequestType.INFO.name, email)
            }.onSuccess {
                viewModelScope.launch(mainDispatcher) {
                    _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            infoRequestState = InfoRequestState.Success
                        )
                    )
                }
            }.onFailure { error ->
                handleHttpException(error, RequestType.INFO)
            }
        }
    }

    fun onExistingRequestConfirmed(email: String, requestType: RequestType) {
        if (requestType == RequestType.INFO) {
            _state.value = _state.value.copy(
                isLoading = false,
                infoRequestState = InfoRequestState.ExistingInfoConfirmed
            )
        } else {
            _state.value = _state.value.copy(
                isLoading = false,
                deleteRequestState = DeleteRequestState.ExistingDeleteConfirmed(email)
            )
        }
    }

    fun onUserDeleteRequestConfirmed(email: String) {
        _state.value = _state.value.copy(isLoading = true, infoRequestState = InfoRequestState.None, deleteRequestState = DeleteRequestState.Loading)
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                createDeleteUseCase.invoke(clientId, RequestType.DELETE.name, email)
            }.onSuccess {
                viewModelScope.launch(mainDispatcher) {
                    _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            deleteRequestState = DeleteRequestState.Success(email)
                        )
                    )
                }
            }.onFailure { error ->
                handleHttpException(error, RequestType.DELETE)
            }
        }
    }

    fun onUserDeleteNoticeClicked() {
        val deleteSuccess = _state.value.deleteRequestState as? DeleteRequestState.Success ?: return
        _state.value = _state.value.copy(
            isLoading = false,
            deleteRequestState = DeleteRequestState.DeleteSuccessAcknowledged(deleteSuccess.email),
        )
    }

    fun clear() {
        _state.value = CurrentUserUIState()
    }

    private fun handleHttpException(error: Throwable, requestType: RequestType) {
        if (requestType == RequestType.DELETE) {
            handleDeleteException(error)
            return
        }
        viewModelScope.launch(mainDispatcher) {
            if (error is HttpException) {
                if (GDPRLibrary.instance().isDebug) {
                    Log.e("CurrentUserViewModel", "ErrorCode: ${error.code()}")
                }
                when (error.code()) {
                    409 -> _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            infoRequestState = InfoRequestState.ExistingInfoRequest
                        )
                    )

                    else -> _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            infoRequestState = InfoRequestState.Error(error)
                        )
                    )
                }
            } else {
                _state.emit(
                    _state.value.copy(
                        isLoading = false,
                        infoRequestState = InfoRequestState.Error(error)
                    )
                )
            }
        }
    }

    private fun handleDeleteException(error: Throwable,) {
        viewModelScope.launch(mainDispatcher) {
            if (error is HttpException) {
                if (GDPRLibrary.instance().isDebug) {
                    Log.e("CurrentUserViewModel", "ErrorCode: ${error.code()}")
                }
                when (error.code()) {
                    409 -> _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            deleteRequestState = DeleteRequestState.ExistingDeleteRequest,
                        )
                    )

                    else -> _state.emit(
                        _state.value.copy(
                            isLoading = false,
                            deleteRequestState = DeleteRequestState.Error(error),
                        )
                    )
                }
            } else {
                _state.emit(
                    _state.value.copy(
                        isLoading = false,
                        deleteRequestState = DeleteRequestState.Error(error),
                    )
                )
            }
        }
    }
}

sealed class InfoRequestState {
    data object None : InfoRequestState()
    data object Loading : InfoRequestState()
    data object Success : InfoRequestState()
    data object ExistingInfoRequest : InfoRequestState()
    data object ExistingInfoConfirmed : InfoRequestState()
    data class Error(val error: Throwable? = null) : InfoRequestState()
}

sealed class DeleteRequestState {
    data object None : DeleteRequestState()
    data object Loading : DeleteRequestState()
    data class Success(val email: String) : DeleteRequestState()
    data class DeleteSuccessAcknowledged(val email: String): DeleteRequestState()
    data object ExistingDeleteRequest : DeleteRequestState()
    data class ExistingDeleteConfirmed(val email: String): DeleteRequestState()
    data class Error(val error: Throwable? = null) : DeleteRequestState()
}

data class CurrentUserUIState(
    val isLoading: Boolean = false,
    val infoRequestState: InfoRequestState = InfoRequestState.None,
    val deleteRequestState: DeleteRequestState = DeleteRequestState.None
)