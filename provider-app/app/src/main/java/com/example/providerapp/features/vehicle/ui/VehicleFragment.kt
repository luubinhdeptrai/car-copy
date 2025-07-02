package com.example.providerapp.features.vehicle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.providerapp.R
import com.example.providerapp.data.model.Vehicle
import com.example.providerapp.features.vehicle.viewmodel.VehicleViewModel
import com.example.providerapp.ui.adapters.PaginationAdapter
import com.example.providerapp.utils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class VehicleFragment : Fragment() {

    private val viewModel: VehicleViewModel by activityViewModels()
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var paginationAdapter: PaginationAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setupObservers()
        viewModel.fetchVehicles(page = 1, limit = 10)
    }

    private fun setupUI(view: View) {
        // Bước 1: Tìm tất cả các view từ layout
        recyclerView = view.findViewById(R.id.recycler_view_vehicles)
        val paginationRecyclerView: RecyclerView = view.findViewById(R.id.recycler_view_pagination)
        progressBar = view.findViewById(R.id.progress_bar)
        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_vehicle)

        // Bước 2: Khởi tạo các adapter
        vehicleAdapter = VehicleAdapter(
            onItemClick = { vehicle ->
                val action = VehicleFragmentDirections.actionVehicleFragmentToEditVehicleFragment(vehicle)
                findNavController().navigate(action)
            },
            onDeleteClick = { vehicle ->
                showDeleteConfirmationDialog(vehicle)
            }
        )
        paginationAdapter = PaginationAdapter { page ->
            viewModel.fetchVehicles(page = page, limit = 10)
        }

        // Bước 3: Gán adapter cho RecyclerView
        recyclerView.adapter = vehicleAdapter
        paginationRecyclerView.adapter = paginationAdapter

        // Bước 4: Gán sự kiện click
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_vehicleFragment_to_addVehicleFragment)
        }
    }

    private fun setupObservers() {
        // Lắng nghe duy nhất LiveData `vehicles`
        viewModel.vehicles.observe(viewLifecycleOwner) { resource ->
            // Dùng 'when' để xử lý từng trạng thái của Resource
            when (resource) {
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    // Khi thành công, lấy data từ resource và cập nhật cho adapter
                    resource.data?.let { vehicleList ->
                        vehicleAdapter.submitList(vehicleList)
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    // Khi có lỗi, hiển thị thông báo
                    resource.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    // Khi đang tải, hiển thị progress bar
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        // Observer cho phân trang
        viewModel.totalPages.observe(viewLifecycleOwner) { totalPages ->
            // Lấy view của thanh phân trang
            val paginationRecyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view_pagination)

            // KIỂM TRA ĐIỀU KIỆN Ở ĐÂY
            if (totalPages > 1) {
                // Nếu có nhiều hơn 1 trang, hiển thị và cập nhật
                paginationRecyclerView?.visibility = View.VISIBLE
                val pageNumbers = (1..totalPages).toList()
                paginationAdapter.submitList(pageNumbers)
            } else {
                // Nếu chỉ có 1 trang hoặc không có, ẩn đi
                paginationRecyclerView?.visibility = View.GONE
            }
        }

        viewModel.currentPage.observe(viewLifecycleOwner) { currentPage ->
            paginationAdapter.currentPage = currentPage
            paginationAdapter.notifyDataSetChanged()
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "Xóa xe thành công!", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Lỗi khi xóa: ${resource.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmationDialog(vehicle: Vehicle) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa xe '${vehicle.licensePlate}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteVehicle(vehicle.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}