package sut.ist912m.zelen.app.jwt

data class JwtRequest (
        val username : String,
        val password : String
)

data class JwtResponse(
        val token : String
)