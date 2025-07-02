package com.example.providerapp.utils

import androidx.lifecycle.Observer

/**
 * Một Observer tùy chỉnh để xử lý các sự kiện Event<T> một cách an toàn.
 * Nó chỉ gọi callback onEventUnhandledContent nếu nội dung của Event chưa được xử lý.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(value: Event<T>) {
        value.getContentIfNotHandled()?.let { content ->
            onEventUnhandledContent(content)
        }
    }
}