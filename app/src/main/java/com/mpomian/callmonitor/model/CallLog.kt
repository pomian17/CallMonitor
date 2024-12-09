package com.mpomian.callmonitor.model

data class CallLog(
    val name: String?,
    val number: String,
    val duration: Int,
    val timestamp: Long,
)