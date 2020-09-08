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
        return getClaim(token) { claims -> claims.subject }
    }

    fun getIssuedAt(token: String) : Instant {
        return getClaim(token) { claims -> claims.issuedAt.toInstant() }
    }

    fun getExpiration(token: String) : Instant {
        return getClaim(token) { claims -> claims.expiration.toInstant() }
    }

    fun getUserId(token: String) : Long {
        return getClaim(token) { claims -> claims["userId"] as Long }
    }

    fun isExpired(token: String): Boolean {
        return  getExpiration(token).isBefore(Instant.now())
    }

    fun validate(token: String, userDetails: UserDetails) : Boolean{
       return getUsername(token) == userDetails.username && isExpired(token)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun <T> getClaim(token: String, block: (Claims) -> T) : T {
        val claims = getAllClaimsFromToken(token)
        return block.invoke(claims)
    }

    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }



}