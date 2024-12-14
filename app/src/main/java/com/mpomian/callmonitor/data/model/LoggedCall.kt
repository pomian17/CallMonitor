package com.mpomian.callmonitor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoggedCall(
    val beginning: String,
    val duration: Long,
    val number: String,
    val name: String?,
)