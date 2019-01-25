package pozzo.apps.travelweather.core

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

class LastRunRepositoryTest {
    companion object {
        private const val PREF_KEY = "prefKey"
        private const val LAST_RUN = 199L
    }

    private lateinit var lastRunRepository: LastRunRepository
    private lateinit var lastRunPreferences : SharedPreferences
    private lateinit var context : Context

    @Before fun setup() {
        setupMocks()
        lastRunRepository = LastRunRepository(context)
    }


    private fun setupMocks() {
        lastRunPreferences = mock {
            on { getLong(PREF_KEY, 0L) } doReturn LAST_RUN
        }

        context = mock {
            on { getSharedPreferences(any(), any()) } doReturn lastRunPreferences
        }
    }

    @Test fun shouldReturnZeroWhenNotSet() {
        val lastRun = lastRunRepository.getLastRun("notSet")
        assertEquals(0L, lastRun)
    }

    @Test fun shouldReturnLastSavedOnPreferences() {
        val lastRun = lastRunRepository.getLastRun(PREF_KEY)
        assertEquals(LAST_RUN, lastRun)
    }

    @Test fun shouldReturnNotRunWhenNeverSet() {
        val hasRun = lastRunRepository.hasRun("notSet")
        assertFalse(hasRun)
    }

    @Test fun shouldReturnTruWhenSet() {
        val hasRun = lastRunRepository.hasRun(PREF_KEY)
        assertTrue(hasRun)
    }

    @Test fun shouldSetCurrentTime() {
        val timeRun = ArgumentCaptor.forClass(Long::class.java)
        val editor = mock<SharedPreferences.Editor> {
            on { putLong(any(), timeRun.capture()) } doReturn this.mock
        }
        whenever(lastRunPreferences.edit()).thenReturn(editor)

        val before = System.currentTimeMillis()
        lastRunRepository.setRun(PREF_KEY)
        val gotValue = timeRun.value

        verify(editor).apply()
        assertTrue(gotValue >= before && gotValue <= System.currentTimeMillis())
    }
}
