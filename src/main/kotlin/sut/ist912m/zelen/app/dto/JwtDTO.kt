package sut.ist912m.zelen.app.dto

data class JwtRequest (
        val username : String,
        val password : String
)

data class JwtResponse(
        val token : String
)