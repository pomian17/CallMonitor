package com.mpomian.callmonitor.data.repository.mock

import com.mpomian.callmonitor.data.repository.base.ContactResolver

class MockContactResolver : ContactResolver {
    override fun getContactName(phoneNumber: String): String? = null
}