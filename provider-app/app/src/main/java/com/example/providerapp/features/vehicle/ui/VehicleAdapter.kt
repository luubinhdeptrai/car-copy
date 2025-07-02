package com.example.providerapp.features.vehicle.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.providerapp.R
import com.example.providerapp.data.model.Vehicle
import com.bumptech.glide.Glide


class VehicleAdapter(
    private val onItemClick: (Vehicle) -> Unit,
    private val onDeleteClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(VehicleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vehicleType: TextView = itemView.findViewById(R.id.text_view_type)
        private val licensePlate: TextView = itemView.findViewById(R.id.text_view_license_plate)
        private val capacity: TextView = itemView.findViewById(R.id.text_view_capacity)
        private val currentStation: TextView = itemView.findViewById(R.id.text_view_current_station)
        private val statusTextView: TextView = itemView.findViewById(R.id.text_view_status)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)
        private val vehicleImageView: ImageView = itemView.findViewById(R.id.image_view_vehicle)

        fun bind(vehicle: Vehicle) {
            vehicleType.text = vehicle.type
            licensePlate.text = vehicle.licensePlate
            capacity.text = "Sức chứa: ${vehicle.capacity}"
            currentStation.text = "Bến đỗ: ${vehicle.currentStation.name}"

            when (vehicle.status) {
                "available" -> {
                    statusTextView.text = "Sẵn sàng"
                    statusTextView.setBackgroundColor(Color.parseColor("#C8E6C9"))
                    statusTextView.setTextColor(Color.parseColor("#2E7D32"))
                }
                "in-use" -> {
                    statusTextView.text = "Đang chạy"
                    statusTextView.setBackgroundColor(Color.parseColor("#BBDEFB"))
                    statusTextView.setTextColor(Color.parseColor("#1976D2"))
                }
                "maintenance" -> {
                    statusTextView.text = "Bảo trì"
                    statusTextView.setBackgroundColor(Color.parseColor("#FFCDD2"))
                    statusTextView.setTextColor(Color.parseColor("#D32F2F"))
                }
                else -> {
                    statusTextView.text = vehicle.status
                    statusTextView.setBackgroundColor(Color.LTGRAY)
                    statusTextView.setTextColor(Color.BLACK)
                }
            }

            vehicle.imageUrl?.let { url ->
                // Giả sử BASE_URL là http://192.168.1.8:3080/
                // và url từ backend là /uploads/images/ten_anh.jpg
                val fullUrl = "http://192.168.1.8:3000$url" // <-- Nối BASE_URL với URL tương đối
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_placeholder) // Ảnh hiển thị trong lúc tải
                    .error(R.drawable.ic_error_placeholder) // Ảnh hiển thị khi có lỗi
                    .into(vehicleImageView)
            }

            itemView.setOnClickListener { onItemClick(vehicle) }
            deleteButton.setOnClickListener { onDeleteClick(vehicle) }
        }
    }
}

class VehicleDiffCallback : DiffUtil.ItemCallback<Vehicle>() {
    override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
        return oldItem == newItem
    }
}