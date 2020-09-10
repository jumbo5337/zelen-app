package sut.ist912m.zelen.app.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.Instant
import java.util.*


@Component
class JwtTokenUtils : Serializable {

    companion object {
        private val serialVersionUID = -2550185165626007488L;
    }

    val JWT_TOKEN_VALIDITY = 5 * 60 * 60

    @Value("\${jwt.secret}")
    private lateinit var secret: String



    fun getUsername(token: String) : String{
       return getAllClaimsFromToken(token).subject
    }

    fun getIssuedAt(token: String) : Instant {
        return getAllClaimsFromToken(token).issuedAt.toInstant()
    }

    fun getExpiration(token: String) : Instant {
        return getAllClaimsFromToken(token).expiration.toInstant()
    }

    fun getUserId(token: String) : Long {
        return getAllClaimsFromToken(token)["userId"] as Long
    }

    fun isExpired(token: String): Boolean {
        return  getExpiration(token).isBefore(Instant.now())
    }

    fun validate(token: String, userDetails: UserDetails) : Boolean{
       return getUsername(token) == userDetails.username && !isExpired(token)
    }

    fun generateToken(userDetails: JwtUser): String {
        val claims = mutableMapOf<String, Any>()
        claims["userId"] = userDetails.user.id
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }



}