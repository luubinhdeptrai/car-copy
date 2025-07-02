package com.example.providerapp.features.driver.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
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
import androidx.lifecycle.lifecycleScope
import com.example.providerapp.data.model.StationShort
import com.example.providerapp.features.driver.viewmodel.DriverViewModel
import com.example.providerapp.utils.FileUtil
import com.example.providerapp.utils.Resource
import com.example.providerapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

abstract class BaseDriverFragment : Fragment() {

    // ViewModel được chia sẻ
    protected val viewModel: DriverViewModel by activityViewModels()

    // Các biến chung
    protected var stationList: List<StationShort> = emptyList()
    protected var selectedPhotoFile: File? = null

    // Các view chung
    protected lateinit var nameEditText: EditText
    protected lateinit var ageEditText: EditText
    protected lateinit var stationSpinner: Spinner
    protected lateinit var statusSpinner: Spinner
    protected lateinit var saveButton: Button
    protected lateinit var selectPhotoButton: Button
    protected lateinit var imageViewPreview: ImageView

    protected val statuses = mapOf("Sẵn sàng" to "available", "Đang có chuyến" to "assigned")

    // Launcher chung
    protected val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                val file = withContext(Dispatchers.IO) { FileUtil.uriToFile(requireContext(), it) }
                selectedPhotoFile = file
                imageViewPreview.setImageURI(it)
                imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    protected val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) imagePickerLauncher.launch("image/*")
        else Toast.makeText(context, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupSpinners()
        setupListeners()
        setupObservers()
        viewModel.fetchAllStations()
    }

    // Các hàm chung
    protected open fun bindViews(view: View) {
        nameEditText = view.findViewById(R.id.edit_text_name)
        ageEditText = view.findViewById(R.id.edit_text_age)
        stationSpinner = view.findViewById(R.id.spinner_station)
        statusSpinner = view.findViewById(R.id.spinner_status)
        saveButton = view.findViewById(R.id.button_save)
        selectPhotoButton = view.findViewById(R.id.button_select_photo)
        imageViewPreview = view.findViewById(R.id.image_view_preview)
    }

    protected open fun setupSpinners() {
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses.keys.toList())
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter
    }

    protected open fun setupListeners() {
        selectPhotoButton.setOnClickListener { handleImageSelection() }
    }

    protected open fun setupObservers() {
        viewModel.stations.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let {
                    stationList = it
                    val stationNames = it.map { station -> station.name }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stationNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    stationSpinner.adapter = adapter
                }
            }
        }
    }

    protected fun handleImageSelection() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*")
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    protected fun validateInput(): Boolean {
        nameEditText.error = null
        ageEditText.error = null
        if (nameEditText.text.isBlank()) {
            nameEditText.error = "Tên tài xế không được để trống"
            nameEditText.requestFocus()
            return false
        }
        if (ageEditText.text.isBlank()) {
            ageEditText.error = "Tuổi không được để trống"
            ageEditText.requestFocus()
            return false
        }
        if (stationList.isEmpty() || stationSpinner.selectedItemPosition < 0) {
            Toast.makeText(context, "Vui lòng chọn một bến đỗ", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}