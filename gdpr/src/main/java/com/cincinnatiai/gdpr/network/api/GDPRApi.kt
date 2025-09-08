package com.cincinnatiai.gdpr.network.api

import com.cincinnatiai.gdpr.model.CreateDeleteRequest
import com.cincinnatiai.gdpr.model.CreateInfoRequest
import com.cincinnatiai.gdpr.model.DeleteRequest
import com.cincinnatiai.gdpr.model.FetchAllInfoRequestRequest
import com.cincinnatiai.gdpr.model.InfoRequest
import com.cincinnatiai.gdpr.model.PaginatedResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GDPRApi {

    @POST("gdpr?action=create")
    suspend fun createInfoRequest(@Body request: CreateInfoRequest): InfoRequest

    @POST("gdpr?action=fetchAll")
    suspend fun fetchAllInfoRequests(@Body request: FetchAllInfoRequestRequest): PaginatedResponse<InfoRequest>

    @POST("gdpr?controller=delete&action=create")
    suspend fun createDeleteRequest(@Body request: CreateDeleteRequest): DeleteRequest
}