package com.cincinnatiai.gdpr.viewmodelprovider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cincinnatiai.gdpr.GDPRLibrary
import com.cincinnatiai.gdpr.ui.accounts.AccountsViewModel

class AccountsViewModelProvider : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
            return AccountsViewModel(
                GDPRLibrary.instance().gdprDataProvider.getFetchAccountsUseCase(),
                GDPRLibrary.instance().accountSelectedUseCase
            ) as T
        }
        return super.create(modelClass)
    }
}