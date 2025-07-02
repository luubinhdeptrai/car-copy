package com.example.providerapp.features.vehicle.ui

import android.Manifest
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
import com.example.providerapp.R
import com.example.providerapp.data.model.StationShort
import com.example.providerapp.features.vehicle.viewmodel.VehicleViewModel
import com.example.providerapp.utils.FileUtil
import com.example.providerapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers


@AndroidEntryPoint
class AddVehicleFragment : Fragment() {

    private val viewModel: VehicleViewModel by activityViewModels()
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
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Khởi chạy một coroutine để xử lý file trong luồng nền
            viewLifecycleOwner.lifecycleScope.launch {
                // Hiển thị loading (nếu có)

                // Chuyển sang luồng I/O để xử lý file
                val file = withContext(Dispatchers.IO) {
                    FileUtil.uriToFile(requireContext(), selectedUri)
                }

                // Quay lại luồng Main để cập nhật UI
                selectedImageFile = file
                imageViewPreview.setImageURI(selectedUri)
                imageViewPreview.visibility = View.VISIBLE

                // Ẩn loading
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
        return inflater.inflate(R.layout.fragment_add_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupSpinners()
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

            viewModel.createVehicle(
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
        viewModel.createResult.observe(viewLifecycleOwner) { resource ->
            // Chỉ xử lý khi resource không phải là null
            resource?.let {
                when (it) {
                    is Resource.Loading -> {
                        // Hiển thị loading, không reset
                    }
                    is Resource.Success -> {
                        Toast.makeText(context, "Thêm xe thành công!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        viewModel.onResultConsumed() // <-- DI CHUYỂN VÀO ĐÂY
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