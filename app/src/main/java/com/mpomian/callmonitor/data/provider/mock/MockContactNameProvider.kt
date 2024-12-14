package com.mpomian.callmonitor.data.provider.mock

import com.mpomian.callmonitor.data.provider.base.ContactNameProvider

class MockContactNameProvider : ContactNameProvider {
    override fun getContactName(phoneNumber: String): String? = null
}