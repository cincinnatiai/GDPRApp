package com.cincinnatiai.cincinnatiaccountcommons.usecase

import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract

interface FetchAccountsWithProfilesUseCase {

    suspend operator fun invoke(): List<AccountWithProfileContract>
}