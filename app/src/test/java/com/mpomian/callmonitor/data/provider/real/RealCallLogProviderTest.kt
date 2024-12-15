package com.mpomian.callmonitor.data.provider.real

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import com.mpomian.callmonitor.utils.Utils.toFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RealCallLogProviderTest {

    @Mock
    private lateinit var mockContentResolver: ContentResolver

    @Mock
    private lateinit var mockCallLogObserver: CallLogObserver

    @Mock
    private lateinit var mockCursor: Cursor

    private lateinit var sut: RealCallLogProvider

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = CoroutineScope(testDispatcher)

    @AfterEach
    fun afterEach() {
        reset(mockContentResolver, mockCallLogObserver, mockCursor)
    }

    @Test
    fun `when cursor is empty getCallLogs returns empty list`() = runTest {
        `when`(mockCursor.moveToNext()).thenReturn(false)
        `when`(mockContentResolver.query(any(), any(), any(), any(), any())).thenReturn(mockCursor)
        `when`(mockCallLogObserver.callLogsFlow).thenReturn(MutableSharedFlow<Unit>())
        sut = RealCallLogProvider(mockContentResolver, mockCallLogObserver, testCoroutineScope)

        val callLogs = sut.getCallLogs().first()

        assertTrue(callLogs.isEmpty())
    }

    @Test
    fun `getCallLogs returns 2 correct call logs from cursor`() = runTest {
        // called 3 times: first 2 times true (for 2 rows), then false
        `when`(mockCursor.moveToNext()).thenReturn(true, true, false)
        // Column indices
        `when`(mockCursor.getColumnIndex(CallLog.Calls.NUMBER)).thenReturn(0)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.DURATION)).thenReturn(1)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.DATE)).thenReturn(2)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)).thenReturn(3)
        // Row data
        `when`(mockCursor.getString(0)).thenReturn("1234567890", "0987654321")
        `when`(mockCursor.getLong(1)).thenReturn(60L, 120L)
        `when`(mockCursor.getLong(2)).thenReturn(1734283940000L, 1733283940000L)
        `when`(mockCursor.getString(3)).thenReturn("John Doe", "Jane Smith")

        `when`(mockContentResolver.query(any(), any(), any(), any(), any())).thenReturn(mockCursor)
        `when`(mockCallLogObserver.callLogsFlow).thenReturn(MutableSharedFlow<Unit>())
        sut = RealCallLogProvider(mockContentResolver, mockCallLogObserver, testCoroutineScope)

        val callLogs = sut.getCallLogs().first()

        assertEquals(2, callLogs.size)
        // check first log entry
        assertEquals("1234567890", callLogs[0].number)
        assertEquals(60L, callLogs[0].duration)
        assertEquals(1734283940000L.toFormattedDate(), callLogs[0].beginning)
        assertEquals("John Doe", callLogs[0].name)
        // check second log entry
        assertEquals("0987654321", callLogs[1].number)
        assertEquals(120L, callLogs[1].duration)
        assertEquals(1733283940000L.toFormattedDate(), callLogs[1].beginning)
        assertEquals("Jane Smith", callLogs[1].name)
    }

    @Test
    fun `getCallLogs handles null name`() = runTest {
        `when`(mockCursor.moveToNext()).thenReturn(true, false)

        `when`(mockCursor.getColumnIndex(CallLog.Calls.NUMBER)).thenReturn(0)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.DURATION)).thenReturn(1)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.DATE)).thenReturn(2)
        `when`(mockCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)).thenReturn(3)

        `when`(mockCursor.getString(0)).thenReturn("1234567890")
        `when`(mockCursor.getLong(1)).thenReturn(60L)
        `when`(mockCursor.getLong(2)).thenReturn(1734283940000L)
        `when`(mockCursor.getString(3)).thenReturn(null)

        `when`(mockContentResolver.query(any(), any(), any(), any(), any())).thenReturn(mockCursor)
        `when`(mockCallLogObserver.callLogsFlow).thenReturn(MutableSharedFlow<Unit>())

        sut = RealCallLogProvider(mockContentResolver, mockCallLogObserver, testCoroutineScope)

        val callLogs = sut.getCallLogs().first()

        assertEquals(1, callLogs.size)

        assertEquals("1234567890", callLogs[0].number)
        assertEquals(60L, callLogs[0].duration)
        assertEquals(1734283940000L.toFormattedDate(), callLogs[0].beginning)
        assertEquals(null, callLogs[0].name)
    }

    @Test
    fun `callLogsFlow triggers getCallLogs emission`() = runTest {
        `when`(mockCursor.moveToNext()).thenReturn(false)
        `when`(mockContentResolver.query(any(), any(), any(), any(), any())).thenReturn(mockCursor)
        val callLogsFlow = MutableSharedFlow<Unit>()
        `when`(mockCallLogObserver.callLogsFlow).thenReturn(callLogsFlow)
        sut = RealCallLogProvider(mockContentResolver, mockCallLogObserver, testCoroutineScope)

        callLogsFlow.emit(Unit)

        verify(mockContentResolver, times(2)).query(any(), any(), any(), any(), any())
    }
}
