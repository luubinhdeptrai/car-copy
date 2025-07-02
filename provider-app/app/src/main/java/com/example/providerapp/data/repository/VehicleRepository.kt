package com.example.providerapp.data.repository

import com.example.providerapp.data.api.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getVehicles(page: Int, limit: Int) = apiService.getVehicles(page, limit)

    suspend fun createVehicle(
        type: RequestBody,
        licensePlate: RequestBody,
        capacity: RequestBody,
        status: RequestBody,
        currentStation: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.createVehicle(type, licensePlate, capacity, status, currentStation, image)

    suspend fun updateVehicle(
        vehicleId: String,
        partMap: Map<String, RequestBody>,
        image: MultipartBody.Part?
    ) = apiService.updateVehicle(vehicleId, partMap, image)

    suspend fun deleteVehicle(vehicleId: String) = apiService.deleteVehicle(vehicleId)
}