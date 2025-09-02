package com.cincinnatiai.cincinnatiaccountcommons.model

interface AccountProfileContract {
    val accountId: String
    val rangeKey: String
    val firstName: String
    val lastName: String
    val encryptedEmail: String
    val permissions: Int
}