package com.teksiak.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}