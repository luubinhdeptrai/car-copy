package com.example.providerapp.features.driver.ui

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
import com.bumptech.glide.Glide
import com.example.providerapp.R
import com.example.providerapp.data.model.Driver
import com.example.providerapp.di.NetworkModule

class DriverAdapter(
    private val onItemClick: (Driver) -> Unit,
    private val onDeleteClick: (Driver) -> Unit
) : ListAdapter<Driver, DriverAdapter.DriverViewHolder>(DriverDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.text_view_name)
        private val age: TextView = itemView.findViewById(R.id.text_view_age)
        private val station: TextView = itemView.findViewById(R.id.text_view_station)
        private val status: TextView = itemView.findViewById(R.id.text_view_status)
        private val photo: ImageView = itemView.findViewById(R.id.image_view_photo)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)

        fun bind(driver: Driver) {
            name.text = driver.name
            age.text = "Tuổi: ${driver.age}"
            station.text = "Bến đỗ: ${driver.currentStation.name}"

            // Xử lý hiển thị trạng thái
            when (driver.status) {
                "available" -> {
                    status.text = "Sẵn sàng"
                    status.setBackgroundColor(Color.parseColor("#C8E6C9"))
                    status.setTextColor(Color.parseColor("#2E7D32"))
                }
                "assigned" -> {
                    status.text = "Đang có chuyến"
                    status.setBackgroundColor(Color.parseColor("#BBDEFB"))
                    status.setTextColor(Color.parseColor("#1976D2"))
                }
                else -> {
                    status.text = driver.status
                    status.setBackgroundColor(Color.LTGRAY)
                    status.setTextColor(Color.BLACK)
                }
            }

            // Load ảnh
            driver.photoUrl?.let { url ->
                val fullUrl = NetworkModule.BASE_URL + url // <-- Nhớ thay BASE_URL
                Glide.with(itemView.context).load(fullUrl).into(photo)
            } ?: photo.setImageResource(R.mipmap.ic_launcher) // Ảnh mặc định

            // Gán sự kiện
            itemView.setOnClickListener { onItemClick(driver) }
            deleteButton.setOnClickListener { onDeleteClick(driver) }
        }
    }
}

class DriverDiffCallback : DiffUtil.ItemCallback<Driver>() {
    override fun areItemsTheSame(oldItem: Driver, newItem: Driver): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Driver, newItem: Driver): Boolean = oldItem == newItem
}