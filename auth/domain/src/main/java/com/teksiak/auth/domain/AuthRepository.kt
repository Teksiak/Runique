package com.teksiak.auth.domain

import com.teksiak.core.domain.util.DataError
import com.teksiak.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun login(email: String, password: String): EmptyResult<DataError.Network>
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}