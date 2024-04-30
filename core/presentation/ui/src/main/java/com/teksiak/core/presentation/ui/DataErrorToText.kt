package com.teksiak.core.presentation.ui

import com.teksiak.core.domain.util.DataError

fun DataError.asUiText(): UiText {
    return when(this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(id = R.string.error_disk_full)
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(id = R.string.error_request_timeout)
        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(id = R.string.error_too_many_requests)
        DataError.Network.NO_INTERNET -> UiText.StringResource(id = R.string.error_no_internet)
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(id = R.string.error_payload_too_large)
        DataError.Network.SERVER_ERROR -> UiText.StringResource(id = R.string.error_server_error)
        DataError.Network.SERIALIZATION_ERROR -> UiText.StringResource(id = R.string.error_serialization_error)
        else -> UiText.StringResource(id = R.string.error_unknown_error)
    }
}