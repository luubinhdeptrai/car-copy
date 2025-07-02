package com.example.providerapp.features.vehicle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.providerapp.data.model.Vehicle
import com.example.providerapp.data.model.StationShort
import com.example.providerapp.data.repository.VehicleRepository
import com.example.providerapp.data.repository.StationRepository
import com.example.providerapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val stationRepository: StationRepository
) : ViewModel() {

    // LiveData cho danh sách xe
    private val _vehicles = MutableLiveData<Resource<List<Vehicle>>>()
    val vehicles: LiveData<Resource<List<Vehicle>>> = _vehicles

    // LiveData cho các kết quả trả về
    private val _createResult = MutableLiveData<Resource<Vehicle>>()
    val createResult: LiveData<Resource<Vehicle>> = _createResult

    private val _updateResult = MutableLiveData<Resource<Vehicle>>()
    val updateResult: LiveData<Resource<Vehicle>> = _updateResult

    private val _deleteResult = MutableLiveData<Resource<Unit>>()
    val deleteResult: LiveData<Resource<Unit>> = _deleteResult

    private val _stations = MutableLiveData<Resource<List<StationShort>>>()
    val stations: LiveData<Resource<List<StationShort>>> = _stations


    // Các LiveData cho phân trang
    val totalPages = MutableLiveData<Int>()
    val currentPage = MutableLiveData<Int>()

    fun fetchVehicles(page: Int, limit: Int) {
        viewModelScope.launch {
            _vehicles.postValue(Resource.Loading())
            try {
                val response = vehicleRepository.getVehicles(page, limit)
                if (response.isSuccessful && response.body() != null) {
                    _vehicles.postValue(Resource.Success(response.body()!!.data))
                    totalPages.postValue(response.body()!!.pagination.totalPages)
                    currentPage.postValue(response.body()!!.pagination.currentPage)
                } else {
                    _vehicles.postValue(Resource.Error("Lỗi: ${response.message()}"))
                }
            } catch (e: Exception) {
                _vehicles.postValue(Resource.Error(e.message ?: "Đã có lỗi không xác định"))
            }
        }
    }

    fun fetchAllStations() {
        viewModelScope.launch {
            _stations.postValue(Resource.Loading())
            try {
                val response = stationRepository.getAllStations()
                if (response.isSuccessful && response.body() != null) {
                    // THAY ĐỔI Ở ĐÂY: Lấy danh sách từ response.body().data
                    _stations.postValue(Resource.Success(response.body()!!.data))
                } else {
                    _stations.postValue(Resource.Error("Lỗi tải danh sách bến xe"))
                }
            } catch (e: Exception) {
                _stations.postValue(Resource.Error(e.message ?: "Lỗi không xác định"))
            }
        }
    }

    fun createVehicle(
        type: String,
        licensePlate: String,
        capacity: String,
        status: String,
        currentStation: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _createResult.postValue(Resource.Loading())
            try {
                val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
                val licensePlateBody = licensePlate.toRequestBody("text/plain".toMediaTypeOrNull())
                val capacityBody = capacity.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
                val currentStationBody = currentStation.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                // KÍCH HOẠT LẠI DÒNG GỌI API THẬT
                val response = vehicleRepository.createVehicle(typeBody, licensePlateBody, capacityBody, statusBody, currentStationBody, imagePart)

                if (response.isSuccessful && response.body() != null) {
                    _createResult.postValue(Resource.Success(response.body()!!))
                    fetchVehicles(1, 10)
                } else {
                    _createResult.postValue(Resource.Error("Lỗi: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _createResult.postValue(Resource.Error(e.message ?: "Lỗi không xác định"))
            }
        }
    }

    fun updateVehicle(
        vehicleId: String,
        type: String,
        licensePlate: String,
        capacity: String,
        status: String,
        currentStation: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _updateResult.postValue(Resource.Loading())
            try {
                val partMap = mutableMapOf<String, RequestBody>()
                partMap["type"] = type.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["licensePlate"] = licensePlate.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["capacity"] = capacity.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["status"] = status.toRequestBody("text/plain".toMediaTypeOrNull())
                partMap["currentStation"] = currentStation.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                // KÍCH HOẠT LẠI DÒNG GỌI API THẬT
                val response = vehicleRepository.updateVehicle(vehicleId, partMap, imagePart)

                if (response.isSuccessful && response.body() != null) {
                    _updateResult.postValue(Resource.Success(response.body()!!))
                    fetchVehicles(1, 10)
                } else {
                    _updateResult.postValue(Resource.Error("Lỗi: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _updateResult.postValue(Resource.Error(e.message ?: "Lỗi không xác định"))
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                val response = vehicleRepository.deleteVehicle(vehicleId)
                if (response.isSuccessful) {
                    _deleteResult.postValue(Resource.Success(Unit))
                    fetchVehicles(1, 10)
                } else {
                    _deleteResult.postValue(Resource.Error("Lỗi: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error(e.message ?: "Lỗi không xác định"))
            }
        }
    }

    fun onResultConsumed() {
        _createResult.value = null
        _updateResult.value = null
        _deleteResult.value = null
    }
}