package com.teksiak.analytics.presentation.compare_run.mapper

import com.teksiak.analytics.presentation.compare_run.model.RunUi
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.core.presentation.ui.toFormattedDateTime

fun Run.toRunUi(): RunUi {
    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = dateTimeUtc.toFormattedDateTime(),
        mapPictureUrl = mapPictureUrl
    )
}