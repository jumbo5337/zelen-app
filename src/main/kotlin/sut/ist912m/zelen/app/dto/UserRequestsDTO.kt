package sut.ist912m.zelen.app.dto

import java.time.LocalDate

data class UserCreateRequest(
        val username: String,
        val password1: String,
        val password2: String,
        val secretCode: String,
        val firstName : String,
        val secondName: String,
        val email: String
)

data class UserResetPasswordRequest(
        val username: String,
        val password1: String,
        val password2: String,
        val secretCode: String
)

data class UserChangePasswordRequest(
        val oldPassword: String,
        val password1: String,
        val password2: String
)

data class UserChangeSecretRequest(
        val secretCode: String
)

data class UserInfoRequest(
        val firstName : String,
        val secondName: String,
        val email: String
)