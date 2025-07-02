package com.example.providerapp.data.api

import com.example.providerapp.data.model.Vehicle
import com.example.providerapp.data.model.VehicleListResponse
import com.example.providerapp.data.model.StationListResponse
import com.example.providerapp.data.model.Driver
import com.example.providerapp.data.model.DriverListResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- Vehicle APIs ---
    @GET("api/vehicles")
    suspend fun getVehicles(@Query("page") page: Int, @Query("limit") limit: Int): Response<VehicleListResponse>

    @GET("api/stations")
    suspend fun getAllStations(): Response<StationListResponse>

    @Multipart
    @POST("api/vehicles")
    suspend fun createVehicle(
        @Part("type") type: RequestBody,
        @Part("licensePlate") licensePlate: RequestBody,
        @Part("capacity") capacity: RequestBody,
        @Part("status") status: RequestBody,
        @Part("currentStation") currentStation: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Vehicle>

    @Multipart
    @PATCH("api/vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") vehicleId: String,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<Vehicle>

    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") vehicleId: String): Response<Unit>







    // --- DRIVER APIs ---
    @GET("api/drivers")
    suspend fun getDrivers(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<DriverListResponse>

    @Multipart
    @POST("api/drivers")
    suspend fun createDriver(
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("currentStation") currentStation: RequestBody,
        @Part("status") status: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<Driver> // Giả định backend trả về driver vừa tạo

    @Multipart
    @PATCH("api/drivers/{id}")
    suspend fun updateDriver(
        @Path("id") driverId: String,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part?
    ): Response<Driver> // Giả định backend trả về driver vừa tạo

    @DELETE("api/drivers/{id}")
    suspend fun deleteDriver(@Path("id") driverId: String): Response<Unit>
}