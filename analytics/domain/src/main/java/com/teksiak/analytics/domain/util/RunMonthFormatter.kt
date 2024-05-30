package com.teksiak.analytics.domain.util

import java.time.ZonedDateTime

fun ZonedDateTime.toFormattedMonth(): String {
    return "${this.month} ${this.year}"
}
