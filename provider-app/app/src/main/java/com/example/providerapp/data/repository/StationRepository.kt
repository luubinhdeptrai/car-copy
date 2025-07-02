package com.example.providerapp.data.repository

import com.example.providerapp.data.api.ApiService
import com.example.providerapp.data.model.StationListResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllStations(): Response<StationListResponse> = apiService.getAllStations()
}