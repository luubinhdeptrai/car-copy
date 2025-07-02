package com.example.providerapp.utils

/**
 * Lớp bao bọc (wrapper) cho dữ liệu được gửi qua LiveData,
 * đảm bảo dữ liệu chỉ được xử lý một lần (như một sự kiện).
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Chỉ cho phép đọc từ bên ngoài

    /**
     * Trả về nội dung và đánh dấu là đã được xử lý.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Trả về nội dung, ngay cả khi nó đã được xử lý.
     */
    fun peekContent(): T = content
}