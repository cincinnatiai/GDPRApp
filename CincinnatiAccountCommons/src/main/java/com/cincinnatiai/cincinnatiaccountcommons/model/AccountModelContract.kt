package com.cincinnatiai.cincinnatiaccountcommons.model

interface AccountModelContract {
    val partitionKey: String
    val rangeKey: String
    val title: String
    val description: String
    val metadata: String
    val type: String
}