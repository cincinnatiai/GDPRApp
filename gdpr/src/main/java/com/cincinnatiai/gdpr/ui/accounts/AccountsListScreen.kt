package com.cincinnatiai.gdpr.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract
import com.cincinnatiai.gdpr.ui.component.AccountItem
import com.cincinnatiai.gdpr.ui.component.EntireUserAccountItem
import com.cincinnatiai.gdpr.ui.component.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsListScreen(
    viewModel: AccountsViewModel,
    onAccountClick: (AccountWithProfileContract) -> Unit,
    onCurrentUserClicked: () -> Unit
) {
    val state = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchAccounts()
    }

    LaunchedEffect(state.value.error) {
        if (state.value.error.isNullOrEmpty().not()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = state.value.error ?: "",
                    duration = SnackbarDuration.Long,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GDPR Privacy") }
            )
        }
    ) { paddingValues ->

        when {
            state.value.isLoading -> LoadingScreen()
            state.value.accounts != null ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            EntireUserAccountItem {
                                onCurrentUserClicked()
                            }
                        }
                        // TODO Enabled Account Specific Data control when ready Sept 8, 2025
//                        items(
//                            state.value.accounts ?: emptyList<AccountWithProfileContract>()
//                        ) { account ->
//                            AccountItem(
//                                account = account,
//                                onClick = {
//                                    viewModel.selectAccount(account)
//                                    onAccountClick(account)
//                                }
//                            )
//                        }
                    }

                }

            state.value.error.isNullOrEmpty().not() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}