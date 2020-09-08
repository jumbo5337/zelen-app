package sut.ist912m.zelen.app.dto

import java.time.Instant
import java.time.LocalDate

data class User (
        val id: Long,
        val username: String,
        val password: String,
        val registerTime : Instant,
        val lastSeen: Instant,
        val role: Role
)

data class UserInfo(
        val userId : Long,
        val firstName : String,
        val secondName: String,
        val email: String,
        val country : String?,
        val birthDate : LocalDate?,
        val city : String
)

data class UserPreferences(
        val userId: Long,
        val timeShift: String,
        val privacy: Privacy
)

data class UserCreateRequest(
        val username: String,
        val password1: String,
        val password2: String
)