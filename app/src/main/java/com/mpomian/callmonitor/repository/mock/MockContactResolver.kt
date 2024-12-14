package com.mpomian.callmonitor.repository.mock

import com.mpomian.callmonitor.repository.base.ContactResolver

class MockContactResolver : ContactResolver {
    override fun getContactName(phoneNumber: String): String? = null
}