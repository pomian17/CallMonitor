package com.mpomian.callmonitor.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract

class ContactResolver(private val contentResolver: ContentResolver) {

    /**
     * Provides the contact name for the given phone number.
     * @param phoneNumber The phone number to resolve.
     * @return The contact name if found, or null if not found.
     */
    fun getContactName(phoneNumber: String): String? {

        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                return cursor.getString(nameIndex)
            }
        }
        return null
    }
}
