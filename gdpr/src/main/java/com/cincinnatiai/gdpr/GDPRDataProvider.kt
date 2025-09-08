package com.cincinnatiai.gdpr

import com.cincinnatiai.cincinnatiaccountcommons.usecase.FetchAccountsWithProfilesUseCase

interface GDPRDataProvider {

    fun getUrl(): String

    fun getFetchAccountsUseCase(): FetchAccountsWithProfilesUseCase

}