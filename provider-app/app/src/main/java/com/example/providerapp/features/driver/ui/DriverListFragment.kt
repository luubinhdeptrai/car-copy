package com.example.providerapp.features.driver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.providerapp.R
import com.example.providerapp.data.model.Driver
import com.example.providerapp.features.driver.viewmodel.DriverViewModel
import com.example.providerapp.ui.adapters.PaginationAdapter
import com.example.providerapp.utils.EventObserver
import com.example.providerapp.utils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverListFragment : Fragment() {

    private val viewModel: DriverViewModel by viewModels()
    private lateinit var driverAdapter: DriverAdapter
    private lateinit var paginationAdapter: PaginationAdapter

    private lateinit var driversRecyclerView: RecyclerView
    private lateinit var paginationRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setupObservers()
        viewModel.fetchDrivers(page = 1, limit = 10)
    }

    private fun setupUI(view: View) {
        driversRecyclerView = view.findViewById(R.id.recycler_view_drivers)
        paginationRecyclerView = view.findViewById(R.id.recycler_view_pagination)
        progressBar = view.findViewById(R.id.progress_bar)

        driverAdapter = DriverAdapter(
            onItemClick = { driver ->
                val action = DriverListFragmentDirections.actionDriverListFragmentToEditDriverFragment(driver)
                findNavController().navigate(action)
            },
            onDeleteClick = { driver ->
                showDeleteConfirmationDialog(driver)
            }
        )
        driversRecyclerView.adapter = driverAdapter

        paginationAdapter = PaginationAdapter { page ->
            viewModel.fetchDrivers(page = page, limit = 10)
        }
        paginationRecyclerView.adapter = paginationAdapter

        view.findViewById<FloatingActionButton>(R.id.fab_add_driver).setOnClickListener {
            findNavController().navigate(R.id.action_driverListFragment_to_addDriverFragment)
        }
    }

    private fun setupObservers() {
        viewModel.drivers.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    driverAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Lắng nghe kết quả xóa bằng EventObserver để đảm bảo chỉ xử lý một lần
        viewModel.deleteResult.observe(viewLifecycleOwner, EventObserver { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "Xóa tài xế thành công!", Toast.LENGTH_SHORT).show()
                    // Không cần gọi onResultConsumed() nữa
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Lỗi khi xóa: ${resource.message}", Toast.LENGTH_LONG).show()
                }
                else -> {} // Loading state
            }
        })

        viewModel.totalPages.observe(viewLifecycleOwner) { totalPages ->
            if (totalPages > 1) {
                paginationRecyclerView.visibility = View.VISIBLE
                val pageNumbers = (1..totalPages).toList()
                paginationAdapter.submitList(pageNumbers)
            } else {
                paginationRecyclerView.visibility = View.GONE
            }
        }

        viewModel.currentPage.observe(viewLifecycleOwner) { currentPage ->
            paginationAdapter.currentPage = currentPage
            paginationAdapter.notifyDataSetChanged()
        }
    }

    private fun showDeleteConfirmationDialog(driver: Driver) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa tài xế '${driver.name}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteDriver(driver.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}