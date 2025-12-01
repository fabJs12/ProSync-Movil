package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.AuthResponse
import com.luna.prosync.data.remote.dto.LoginRequest
import com.luna.prosync.data.remote.dto.UserDto
import com.luna.prosync.data.remote.dto.GoogleLoginRequest
import com.luna.prosync.data.remote.dto.UserRegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(loginRequest: LoginRequest): AuthResponse {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: UserRegisterRequest): Response<ResponseBody> {
        return apiService.register(registerRequest)
    }

    suspend fun getProfile(): UserDto {
        return apiService.getProfile()
    }

    suspend fun loginWithGoogle(token: String, username: String? = null): AuthResponse {
        return apiService.googleLogin(GoogleLoginRequest(token, username))
    }
}