package com.cincinnatiai.gdpr.usecase

import com.cincinnatiai.gdpr.model.CreateInfoRequest
import com.cincinnatiai.gdpr.model.InfoRequest
import com.cincinnatiai.gdpr.network.api.GDPRApi

interface CreateInformationRequestUseCase {
    suspend operator fun invoke(
        partitionKey: String,
        type: String,
        createdBy: String,
    ): InfoRequest
}

class CreateInformationRequestUseCaseImpl(
    private val gdprApi: GDPRApi,
    private val apiKey: String,
) : CreateInformationRequestUseCase {

    override suspend fun invoke(
        partitionKey: String,
        type: String,
        createdBy: String,
    ): InfoRequest = gdprApi.createInfoRequest(
        CreateInfoRequest(
            partitionKey,
            type,
            createdBy,
            apiKey
        )
    )
}