package com.example.providerapp.features.driver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.providerapp.R
import com.example.providerapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.example.providerapp.utils.EventObserver


@AndroidEntryPoint
class AddDriverFragment : BaseDriverFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_driver, container, false)
    }

    // Ghi đè lại setupListeners để thêm logic cho nút Save
    override fun setupListeners() {
        super.setupListeners() // Gọi hàm của lớp cha để setup nút chọn ảnh

        saveButton.setOnClickListener {
            if (validateInput()) {
                val selectedStationId = stationList[stationSpinner.selectedItemPosition].id
                val selectedStatusValue = statuses[statusSpinner.selectedItem.toString()] ?: "available"

                viewModel.createDriver(
                    name = nameEditText.text.toString().trim(),
                    age = ageEditText.text.toString().trim(),
                    currentStation = selectedStationId,
                    status = selectedStatusValue,
                    photoFile = selectedPhotoFile
                )
            }
        }
    }

    // Ghi đè lại setupObservers để lắng nghe kết quả tạo mới
    override fun setupObservers() {
        super.setupObservers() // Gọi hàm của lớp cha để lắng nghe danh sách stations

        viewModel.createResult.observe(viewLifecycleOwner, EventObserver { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        saveButton.isEnabled = false
                        saveButton.text = "Đang thêm..."
                    }

                    is Resource.Success -> {
                        saveButton.isEnabled = true
                        saveButton.text = "Thêm mới"
                        Toast.makeText(context, "Thêm tài xế thành công!", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }

                    is Resource.Error -> {
                        saveButton.isEnabled = true
                        saveButton.text = "Thêm mới"
                        Toast.makeText(context, "Lỗi: ${resource.message}", Toast.LENGTH_LONG).show()
                    }
                }
        })
    }
}