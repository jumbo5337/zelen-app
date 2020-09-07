package sut.ist912m.zelen.app.dto

import java.time.Instant

data class User(
        val id: Long,
        val username: String,
        val password: String,
        val lastSeen: Instant,
        val USER_ROLE: Role
)

data class UserInfo(
        val userId : Long,
        val firstName : String,
        val secondName: String,
        val email: String,
        val country : String?,
        val city : String
)

data class UserPreferences(
        val userId: Long,
        val timeShift: String,
        val privacy: Privacy
)