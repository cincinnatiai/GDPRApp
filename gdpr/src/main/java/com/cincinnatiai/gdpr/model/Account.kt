package com.cincinnatiai.gdpr.model

data class Account(
    val partitionKey: String,
    val rangeKey: String,
    val title: String,
    val description: String,
    val metadata: String,
    val type: String,
    val created: String,
    val modified: String,
    val status: String
)