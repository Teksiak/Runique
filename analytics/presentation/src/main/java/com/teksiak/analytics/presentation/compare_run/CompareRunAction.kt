package com.teksiak.analytics.presentation.compare_run

sealed interface CompareRunAction {
    data object OnBackClick: CompareRunAction
    data class OnOtherRunChoose(val runId: String): CompareRunAction
}
