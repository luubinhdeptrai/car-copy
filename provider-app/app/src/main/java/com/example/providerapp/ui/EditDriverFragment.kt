package com.example.providerapp.features.driver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.providerapp.R
import com.example.providerapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.example.providerapp.utils.EventObserver
import com.example.providerapp.di.NetworkModule
@AndroidEntryPoint
class EditDriverFragment : BaseDriverFragment() {

    private val args: EditDriverFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_driver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
    }

    private fun populateData() {
        val driverToEdit = args.driver
        nameEditText.setText(driverToEdit.name)
        ageEditText.setText(driverToEdit.age.toString())

        driverToEdit.photoUrl?.let { url ->
            val fullUrl = NetworkModule.BASE_URL +url // Thay BASE_URL
            Toast.makeText(context, "Ảnh tải từ: $fullUrl", Toast.LENGTH_SHORT).show()
            Glide.with(this).load(fullUrl).into(imageViewPreview)
            imageViewPreview.visibility = View.VISIBLE
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        saveButton.setOnClickListener {
            if (validateInput()) {
                val selectedStationId = stationList[stationSpinner.selectedItemPosition].id
                val selectedStatusValue = statuses[statusSpinner.selectedItem.toString()] ?: "available"

                viewModel.updateDriver(
                    driverId = args.driver.id,
                    name = nameEditText.text.toString().trim(),
                    age = ageEditText.text.toString().trim(),
                    currentStation = selectedStationId,
                    status = selectedStatusValue,
                    photoFile = selectedPhotoFile
                )
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        // Lắng nghe danh sách stations để chọn giá trị cũ
        viewModel.stations.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let {
                    // Chọn đúng bến đỗ cũ
                    val stationPosition = stationList.indexOfFirst { it.id == args.driver.currentStation.id }
                    if (stationPosition != -1) stationSpinner.setSelection(stationPosition)

                    // Chọn đúng trạng thái cũ
                    val statusPosition = statuses.values.toList().indexOf(args.driver.status)
                    if(statusPosition != -1) statusSpinner.setSelection(statusPosition)
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner, EventObserver { resource ->
            when (resource) {
                is Resource.Loading -> {
                    saveButton.isEnabled = false
                    saveButton.text = "Đang lưu..."
                }
                is Resource.Success -> {
                    saveButton.isEnabled = true
                    saveButton.text = "Lưu thay đổi"
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    saveButton.isEnabled = true
                    saveButton.text = "Lưu thay đổi"
                    Toast.makeText(context, "Lỗi: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}