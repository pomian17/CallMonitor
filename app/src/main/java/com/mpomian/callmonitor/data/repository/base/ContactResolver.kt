package com.mpomian.callmonitor.data.repository.base

interface ContactResolver {
    fun getContactName(phoneNumber: String): String?
}