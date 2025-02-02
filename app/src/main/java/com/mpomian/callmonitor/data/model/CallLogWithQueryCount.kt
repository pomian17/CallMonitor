package com.mpomian.callmonitor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CallLogWithQueryCount(
    val beginning: String,
    val duration: Long,
    val number: String,
    val name: String?,
    val timesQueried: Int
)