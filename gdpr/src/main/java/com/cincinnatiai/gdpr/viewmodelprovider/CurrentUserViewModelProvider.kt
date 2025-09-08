package com.cincinnatiai.gdpr.viewmodelprovider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cincinnatiai.gdpr.GDPRLibrary
import com.cincinnatiai.gdpr.ui.currentuser.CurrentUserViewModel

class CurrentUserViewModelProvider : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentUserViewModel::class.java)) {
            return CurrentUserViewModel(
                GDPRLibrary.instance().createInfoUseCase,
                GDPRLibrary.instance().createDeleteUseCase,
                GDPRLibrary.instance().clientId
            ) as T
        }
        return super.create(modelClass)
    }
}