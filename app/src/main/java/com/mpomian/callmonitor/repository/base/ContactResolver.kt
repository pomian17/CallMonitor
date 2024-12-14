package com.mpomian.callmonitor.repository.base

interface ContactResolver {
    fun getContactName(phoneNumber: String): String?
}