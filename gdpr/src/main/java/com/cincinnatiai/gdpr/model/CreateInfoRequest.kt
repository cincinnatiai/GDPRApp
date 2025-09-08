package com.cincinnatiai.gdpr.model

import com.google.gson.annotations.SerializedName

data class CreateInfoRequest(
    @SerializedName("partition_key")
    val partitionKey: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("api_key")
    val apiKey: String
)

data class FetchInfoRequest(
    val partitionKey: String,
    val rangeKey: String,
    val apiKey: String
)

data class FetchAllInfoRequestRequest(
    val partitionKey: String,
    val lastRangeKey: String? = null,
    val apiKey: String
)

data class FetchByTypeRequest(
    val type: String,
    val lastRangeKey: String? = null,
    val apiKey: String
)

data class FetchByCreatorRequest(
    val createdBy: String,
    val lastRangeKey: String? = null,
    val apiKey: String
)

data class UpdateInfoRequestRequest(
    val partitionKey: String,
    val rangeKey: String,
    val type: String? = null,
    val status: String? = null,
    val apiKey: String
)

data class CreateDeleteRequest(
    @SerializedName("partition_key")
    val partitionKey: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("api_key")
    val apiKey: String
)

data class DeleteRequest(
    val partitionKey: String,
    val rangeKey: String,
    val isHardDelete: Boolean,
    val apiKey: String
)

// Response Data Classes
data class InfoRequest(
    val partitionKey: String,
    val rangeKey: String,
    val type: String,
    val status: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String? = null
)

data class ApiResponse<T>(
    val statusCode: Int,
    val data: T? = null,
    val message: String? = null
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val lastEvaluatedKey: String? = null,
    val hasMore: Boolean = false
)