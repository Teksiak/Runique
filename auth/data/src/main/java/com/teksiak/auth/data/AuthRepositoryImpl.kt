package com.teksiak.auth.data

import com.teksiak.auth.domain.AuthRepository
import com.teksiak.core.data.networking.post
import com.teksiak.core.domain.util.DataError
import com.teksiak.core.domain.util.EmptyResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class AuthRepositoryImpl(
    private val httpClient: HttpClient
): AuthRepository {

    override suspend fun reigster(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }
}