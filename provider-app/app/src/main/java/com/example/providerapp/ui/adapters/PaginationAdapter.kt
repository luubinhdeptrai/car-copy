package com.example.providerapp.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.providerapp.R

class PaginationAdapter(
    private val onPageClick: (Int) -> Unit
) : ListAdapter<Int, PaginationAdapter.PageViewHolder>(PageDiffCallback) { // <-- SỬ DỤNG ĐỐI TƯỢNG CỤ THỂ

    var currentPage = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_page_number, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pageNumber: TextView = itemView.findViewById(R.id.text_page_number)

        fun bind(page: Int) {
            pageNumber.text = page.toString()

            if (page == currentPage) {
                pageNumber.setBackgroundColor(Color.BLUE) // Màu của trang đang được chọn
                pageNumber.setTextColor(Color.WHITE)
            } else {
                pageNumber.setBackgroundColor(Color.LTGRAY) // Màu của các trang khác
                pageNumber.setTextColor(Color.BLACK)
            }

            itemView.setOnClickListener { onPageClick(page) }
        }
    }

    // ĐỊNH NGHĨA MỘT ĐỐI TƯỢNG CỤ THỂ CHO DiffUtil.ItemCallback
    companion object {
        private val PageDiffCallback = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                // Với kiểu Int đơn giản, chúng ta chỉ cần so sánh giá trị
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                // Với kiểu Int, nội dung và item là một
                return oldItem == newItem
            }
        }
    }
}