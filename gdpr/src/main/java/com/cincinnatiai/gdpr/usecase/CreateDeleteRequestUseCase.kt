package com.cincinnatiai.gdpr.usecase

import com.cincinnatiai.gdpr.model.CreateDeleteRequest
import com.cincinnatiai.gdpr.model.CreateInfoRequest
import com.cincinnatiai.gdpr.model.DeleteRequest
import com.cincinnatiai.gdpr.model.InfoRequest
import com.cincinnatiai.gdpr.network.api.GDPRApi

interface CreateDeleteRequestUseCase {

    suspend operator fun invoke(
        partitionKey: String,
        type: String,
        createdBy: String
    ): DeleteRequest
}

class CreateDeleteRequestUseCaseImpl(
    private val gdprApi: GDPRApi,
    private val apiKey: String,
) : CreateDeleteRequestUseCase {

    override suspend fun invoke(
        partitionKey: String,
        type: String,
        createdBy: String,
    ): DeleteRequest = gdprApi.createDeleteRequest(
        CreateDeleteRequest(
            partitionKey,
            type,
            createdBy,
            apiKey
        )
    )
}