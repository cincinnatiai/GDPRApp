package com.cincinnatiai.gdpr.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract
import com.cincinnatiai.cincinnatiaccountcommons.usecase.FetchAccountsWithProfilesUseCase
import com.cincinnatiai.gdpr.usecase.AccountSelectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val fetchAccountsWithProfilesUseCase: FetchAccountsWithProfilesUseCase,
    private val accountSelectedUseCase: AccountSelectedUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountsUiState>(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState

    fun fetchAccounts() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(ioDispatcher) {
            val accounts = fetchAccountsWithProfilesUseCase.invoke()
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.emit(_uiState.value.copy(isLoading = false, accounts))
            }
        }
    }

    fun selectAccount(account: AccountWithProfileContract) {
        accountSelectedUseCase.selectedAccount = account
    }

    fun getSelectedAccount(): AccountWithProfileContract? = accountSelectedUseCase.selectedAccount
}

data class AccountsUiState(
    val isLoading: Boolean = false,
    val accounts: List<AccountWithProfileContract>? = null,
    val error: String? = null,
)