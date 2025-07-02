// file: data/repository/DriverRepository.kt
package com.example.providerapp.data.repository

import com.example.providerapp.data.api.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDrivers(page: Int, limit: Int) = apiService.getDrivers(page, limit)

    suspend fun createDriver(
        name: RequestBody,
        age: RequestBody,
        currentStation: RequestBody,
        status: RequestBody,
        photo: MultipartBody.Part?
    ) = apiService.createDriver(name, age, currentStation, status, photo)

    suspend fun updateDriver(
        driverId: String,
        partMap: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ) = apiService.updateDriver(driverId, partMap, photo)

    suspend fun deleteDriver(driverId: String) = apiService.deleteDriver(driverId)

}