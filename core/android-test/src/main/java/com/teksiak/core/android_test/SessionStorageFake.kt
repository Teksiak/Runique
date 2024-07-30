package com.teksiak.core.android_test

import com.teksiak.core.domain.AuthInfo
import com.teksiak.core.domain.SessionStorage

class SessionStorageFake: SessionStorage {

    private var authInfo: AuthInfo? = null

    override suspend fun get(): AuthInfo? {
        return authInfo
    }

    override suspend fun set(authInfo: AuthInfo?) {
        this.authInfo = authInfo
    }
}