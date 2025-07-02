package com.example.providerapp.features.vehicle.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.providerapp.R
import com.example.providerapp.data.model.StationShort
import com.example.providerapp.features.vehicle.viewmodel.VehicleViewModel
import com.example.providerapp.utils.FileUtil
import com.example.providerapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class EditVehicleFragment : Fragment() {

    private val viewModel: VehicleViewModel by activityViewModels()
    private val args: EditVehicleFragmentArgs by navArgs()

    private var stationList: List<StationShort> = emptyList()
    private var selectedImageFile: File? = null

    private lateinit var typeEditText: EditText
    private lateinit var licensePlateEditText: EditText
    private lateinit var capacityEditText: EditText
    private lateinit var stationSpinner: Spinner
    private lateinit var statusSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var imageViewPreview: ImageView

    private val statuses = mapOf("Sẵn sàng" to "available", "Đang chạy" to "in-use", "Bảo trì" to "maintenance")

    // Launcher để nhận kết quả sau khi chọn ảnh
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewLifecycleOwner.lifecycleScope.launch {
                val file = withContext(Dispatchers.IO) { FileUtil.uriToFile(requireContext(), selectedUri) }
                selectedImageFile = file
                imageViewPreview.setImageURI(selectedUri)
                imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    // Launcher để xin quyền đọc bộ nhớ
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupSpinners()
        populateData()
        setupListeners()
        setupObservers()
        viewModel.fetchAllStations()
    }

    private fun bindViews(view: View) {
        typeEditText = view.findViewById(R.id.edit_text_type)
        licensePlateEditText = view.findViewById(R.id.edit_text_license_plate)
        capacityEditText = view.findViewById(R.id.edit_text_capacity)
        stationSpinner = view.findViewById(R.id.spinner_station)
        statusSpinner = view.findViewById(R.id.spinner_status)
        saveButton = view.findViewById(R.id.button_save)
        selectImageButton = view.findViewById(R.id.button_select_image)
        imageViewPreview = view.findViewById(R.id.image_view_preview)
    }

    private fun setupSpinners() {
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses.keys.toList())
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter
    }

    private fun populateData() {
        val vehicleToEdit = args.vehicle
        typeEditText.setText(vehicleToEdit.type)
        licensePlateEditText.setText(vehicleToEdit.licensePlate)
        capacityEditText.setText(vehicleToEdit.capacity.toString())
        // Hiển thị ảnh cũ nếu có
        // Glide.with(this).load(vehicleToEdit.imageUrl).into(imageViewPreview)
        imageViewPreview.visibility = if (vehicleToEdit.imageUrl != null) View.VISIBLE else View.GONE
    }

    private fun setupListeners() {
        selectImageButton.setOnClickListener { handleImageSelection() }

        saveButton.setOnClickListener {
            val selectedStationPosition = stationSpinner.selectedItemPosition
            if (selectedStationPosition < 0 || selectedStationPosition >= stationList.size) {
                Toast.makeText(context, "Vui lòng chọn một bến xe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val stationId = stationList[selectedStationPosition].id

            val selectedStatusKey = statusSpinner.selectedItem.toString()
            val statusValue = statuses[selectedStatusKey] ?: "available"

            viewModel.updateVehicle(
                vehicleId = args.vehicle.id,
                type = typeEditText.text.toString(),
                licensePlate = licensePlateEditText.text.toString(),
                capacity = capacityEditText.text.toString(),
                status = statusValue,
                currentStation = stationId,
                imageFile = selectedImageFile
            )
        }
    }

    private fun handleImageSelection() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                imagePickerLauncher.launch("image/*")
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun setupObservers() {
        viewModel.updateResult.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it) {
                    is Resource.Loading -> { /* Hiển thị loading */ }
                    is Resource.Success -> {
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        viewModel.onResultConsumed() // <-- ĐẶT VÀO ĐÂY
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_LONG).show()
                        viewModel.onResultConsumed() // <-- VÀ VÀO ĐÂY
                    }
                }
            }
        }

        viewModel.stations.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { stations ->
                        stationList = stations
                        val stationNames = stations.map { it.name }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stationNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        stationSpinner.adapter = adapter

                        // Chọn đúng bến xe hiện tại của xe
                        val currentStationId = args.vehicle.currentStation.id
                        val stationPosition = stations.indexOfFirst { it.id == currentStationId }
                        if (stationPosition != -1) {
                            stationSpinner.setSelection(stationPosition)
                        }

                        // Chọn đúng trạng thái hiện tại của xe
                        val currentStatusValue = args.vehicle.status
                        val statusPosition = statuses.values.toList().indexOf(currentStatusValue)
                        if (statusPosition != -1) {
                            statusSpinner.setSelection(statusPosition)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Lỗi tải danh sách bến xe: ${resource.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
}