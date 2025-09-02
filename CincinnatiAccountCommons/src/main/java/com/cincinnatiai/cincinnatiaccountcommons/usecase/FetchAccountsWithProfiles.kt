package com.cincinnatiai.cincinnatiaccountcommons.usecase

import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract

interface FetchAccountsWithProfiles {

    suspend operator fun invoke(): List<AccountWithProfileContract>
}