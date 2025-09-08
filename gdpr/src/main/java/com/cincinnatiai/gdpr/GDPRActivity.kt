package com.cincinnatiai.gdpr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cincinnatiai.gdpr.ui.GDPRRoute
import com.cincinnatiai.gdpr.ui.accountdetails.AccountDetailsScreen
import com.cincinnatiai.gdpr.ui.accounts.AccountsListScreen
import com.cincinnatiai.gdpr.ui.accounts.AccountsViewModel
import com.cincinnatiai.gdpr.ui.currentuser.CurrentUserScreen
import com.cincinnatiai.gdpr.ui.currentuser.CurrentUserViewModel
import com.cincinnatiai.gdpr.ui.theme.GDPRAppTheme
import com.cincinnatiai.gdpr.viewmodelprovider.AccountsViewModelProvider
import com.cincinnatiai.gdpr.viewmodelprovider.CurrentUserViewModelProvider

class GDPRActivity : ComponentActivity() {

    private val accountsViewModelProvider by lazy {
        ViewModelProvider(this, AccountsViewModelProvider())
    }
    private val currentUserViewModelProvider by lazy {
        ViewModelProvider(this, CurrentUserViewModelProvider())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GDPRAppTheme {
                GDPRPrivacyApp(accountsViewModelProvider, currentUserViewModelProvider) { email ->
                    GDPRLibrary.instance().gdprListener.onUserDeleteRequested(email)
                    finish()
                }
            }
        }
    }
}

@Composable
fun GDPRPrivacyApp(
    accountsViewModelProvider: ViewModelProvider,
    currentUserViewModelProvider: ViewModelProvider,
    onUserDeleted: (email: String) -> Unit
) {
    val navController = rememberNavController()
    val accountsViewModel: AccountsViewModel by lazy {
        accountsViewModelProvider[AccountsViewModel::class]
    }
    val currentUserViewModel: CurrentUserViewModel by lazy {
        currentUserViewModelProvider[CurrentUserViewModel::class]
    }

    NavHost(
        navController,
        startDestination = GDPRRoute.ACCOUNTS.route
    ) {
        composable(GDPRRoute.ACCOUNTS.route) {
            AccountsListScreen(accountsViewModel, onAccountClick = {
                navController.navigate(GDPRRoute.ACCOUNT_DETAILS.route)
            }, onCurrentUserClicked = {
                navController.navigate(GDPRRoute.CURRENT_USER.route)
            })
        }
        composable(GDPRRoute.ACCOUNT_DETAILS.route) {
            if (accountsViewModel.getSelectedAccount() != null) {
                AccountDetailsScreen(
                    accountsViewModel.getSelectedAccount()!!,
                    {
                        navController.popBackStack()
                    }) { requestType, other ->

                }
            }
        }
        composable(GDPRRoute.CURRENT_USER.route) {
            CurrentUserScreen(currentUserViewModel, { email ->
                onUserDeleted(email)
            })
        }
    }
}