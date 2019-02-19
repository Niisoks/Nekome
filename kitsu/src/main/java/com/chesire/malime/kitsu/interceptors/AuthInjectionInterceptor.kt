package com.chesire.malime.kitsu.interceptors

import com.chesire.malime.kitsu.AuthProvider
import okhttp3.Interceptor
import okhttp3.Response

class AuthInjectionInterceptor(private val provider: AuthProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticatedRequest = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer ${provider.accessToken}")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
