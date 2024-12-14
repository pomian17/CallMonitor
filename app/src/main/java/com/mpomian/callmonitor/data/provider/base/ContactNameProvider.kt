package com.mpomian.callmonitor.data.provider.base

interface ContactNameProvider {
    fun getContactName(phoneNumber: String): String?
}