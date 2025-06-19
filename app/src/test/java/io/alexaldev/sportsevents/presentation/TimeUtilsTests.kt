package io.alexaldev.sportsevents.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class TimeUtilsTests {
    @Test
    fun `unixTimeToPresentable returns correct format and value`() {
        val now = 1000
        assertThat(unixTimeToPresentable(1005, now)).isEqualTo("00:00:05")
        assertThat(unixTimeToPresentable(1100, now)).isEqualTo("00:01:40")
        assertThat(unixTimeToPresentable(900, now)).isEqualTo("00:00:00")
    }

}