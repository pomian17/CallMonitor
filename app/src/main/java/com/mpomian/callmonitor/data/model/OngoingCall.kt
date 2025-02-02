package com.mpomian.callmonitor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OngoingCall(
    val ongoing: Boolean,
    val number: String?,
    val name: String?
)