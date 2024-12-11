package com.mpomian.callmonitor.model

import kotlinx.serialization.Serializable

@Serializable
data class CallLog(
    val beginning: String,
    val duration: Long,
    val number: String,
    val name: String?,
)