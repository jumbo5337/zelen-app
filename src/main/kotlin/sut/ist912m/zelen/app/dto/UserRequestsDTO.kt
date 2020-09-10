package sut.ist912m.zelen.app.dto

import java.time.LocalDate

data class UserCreateRequest(
        val username: String,
        val password1: String,
        val password2: String
)

data class UserNewPasswordRequest(
        val password1: String,
        val password2: String
)

data class UserChangePasswordRequest(
        val oldPassword: String,
        val password1: String,
        val password2: String
)

data class UserInfoRequest(
        val firstName : String,
        val secondName: String,
        val email: String,
        val country : String?,
        val birthDate : LocalDate?,
        val city : String
)

data class UserPrefsRequest(
        val timeShift: String,
        val privacy: Int
)