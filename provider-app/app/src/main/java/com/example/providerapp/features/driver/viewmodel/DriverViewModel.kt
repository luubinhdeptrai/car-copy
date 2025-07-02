package com.example.providerapp.features.driver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.providerapp.data.model.Driver
import com.example.providerapp.data.model.ErrorResponse
import com.example.providerapp.data.model.StationShort
import com.example.providerapp.data.repository.DriverRepository
import com.example.providerapp.data.repository.StationRepository
import com.example.providerapp.utils.Event
import com.example.providerapp.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject


@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val stationRepository: StationRepository,
    private val gson: Gson
) : ViewModel() {

    // LiveData cho danh sách tài xế
    private val _drivers = MutableLiveData<Resource<List<Driver>>>()
    val drivers: LiveData<Resource<List<Driver>>> = _drivers

    // LiveData cho các kết quả trả về
    private val _createResult = MutableLiveData<Event<Resource<Driver>>>()
    val createResult: LiveData<Event<Resource<Driver>>> = _createResult

    private val _updateResult = MutableLiveData<Event<Resource<Driver>>>()
    val updateResult: LiveData<Event<Resource<Driver>>> = _updateResult

    private val _deleteResult = MutableLiveData<Event<Resource<Unit>>>()
    val deleteResult: LiveData<Event<Resource<Unit>>> = _deleteResult

    // LiveData cho danh sách bến xe
    private val _stations = MutableLiveData<Resource<List<StationShort>>>()
    val stations: LiveData<Resource<List<StationShort>>> = _stations

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage


    private fun parseError(response: Response<*>): String {
        return try {
            val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            val firstError = errorResponse.errors?.values?.firstOrNull()?.msg
            firstError ?: errorResponse.message
        } catch (e: Exception) {
            "Đã có lỗi không xác định xảy ra"
        }
    }

    fun fetchDrivers(page: Int, limit: Int) {
        viewModelScope.launch {
            _drivers.postValue(Resource.Loading())
            try {
                val response = driverRepository.getDrivers(page, limit)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _drivers.postValue(Resource.Success(body.data))

                    body.pagination?.let {
                        _totalPages.postValue(it.totalPages)
                        _currentPage.postValue(it.currentPage)
                    }
                } else {
                    _drivers.postValue(Resource.Error(parseError(response)))
                }
            } catch (e: Exception) {
                _drivers.postValue(Resource.Error(e.message ?: "Lỗi kết nối"))
            }
        }
    }

    fun fetchAllStations() {
        viewModelScope.launch {
            _stations.postValue(Resource.Loading())
            try {
                val response = stationRepository.getAllStations()
                if (response.isSuccessful && response.body() != null) {
                    _stations.postValue(Resource.Success(response.body()!!.data))
                } else {
                    _stations.postValue(Resource.Error("Lỗi tải danh sách bến xe"))
                }
            } catch (e: Exception) {
                _stations.postValue(Resource.Error(e.message ?: "Lỗi không xác định"))
            }
        }
    }

    fun createDriver(name: String, age: String, currentStation: String, status: String, photoFile: File?) {
        viewModelScope.launch {
            _createResult.postValue(Event(Resource.Loading()))
            try {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val ageBody = age.toRequestBody("text/plain".toMediaTypeOrNull())
                val currentStationBody = currentStation.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())

                var photoPart: MultipartBody.Part? = null
                photoFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData("photo", it.name, requestFile)
                }

                val response = driverRepository.createDriver(nameBody, ageBody, currentStationBody, statusBody, photoPart)
                if (response.isSuccessful && response.body() != null) {
                    _createResult.postValue(Event(Resource.Success(response.body()!!)))
                    fetchDrivers(1, 10)
                } else {
                    _createResult.postValue(Event(Resource.Error(parseError(response))))
                }
            } catch (e: Exception) {
                _createResult.postValue(Event(Resource.Error(e.message ?: "Lỗi kết nối")))
            }
        }
    }

    fun updateDriver(driverId: String, name: String, age: String, currentStation: String, status: String, photoFile: File?) {
        viewModelScope.launch {
            _updateResult.postValue(Event(Resource.Loading()))
            try {
                val partMap = mutableMapOf<String, RequestBody>()
                partMap["name"] = name.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["age"] = age.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["currentStation"] = currentStation.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["status"] = status.toRequestBody("text/plain".toMediaTypeOrNull())

                var photoPart: MultipartBody.Part? = null
                photoFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData("photo", it.name, requestFile)
                }

                val response = driverRepository.updateDriver(driverId, partMap, photoPart)
                if (response.isSuccessful && response.body() != null) {
                    _updateResult.postValue(Event(Resource.Success(response.body()!!)))
                    fetchDrivers(1, 10)
                } else {
                    _updateResult.postValue(Event(Resource.Error(parseError(response))))
                }
            } catch (e: Exception) {
                _updateResult.postValue(Event(Resource.Error(e.message ?: "Lỗi kết nối")))
            }
        }
    }

    fun deleteDriver(driverId: String) {
        viewModelScope.launch {
            _deleteResult.postValue(Event(Resource.Loading()))
            try {
                val response = driverRepository.deleteDriver(driverId)
                if (response.isSuccessful) {
                    _deleteResult.postValue(Event(Resource.Success(Unit)))
                    fetchDrivers(1, 10)
                } else {
                    _deleteResult.postValue(Event(Resource.Error(parseError(response))))
                }
            } catch (e: Exception) {
                _deleteResult.postValue(Event(Resource.Error(e.message ?: "Lỗi kết nối")))
            }
        }
    }
}