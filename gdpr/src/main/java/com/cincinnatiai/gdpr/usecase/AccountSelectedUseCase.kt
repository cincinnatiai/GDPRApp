package com.cincinnatiai.gdpr.usecase

import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract

interface AccountSelectedUseCase {
    var selectedAccount: AccountWithProfileContract?
}

class AccountSelectedUseCaseImpl : AccountSelectedUseCase {
    override var selectedAccount: AccountWithProfileContract? = null
}