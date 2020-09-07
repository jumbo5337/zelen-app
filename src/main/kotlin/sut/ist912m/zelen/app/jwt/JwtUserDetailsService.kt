package sut.ist912m.zelen.app.jwt

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails as UserDetails

class JwtUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(userName: String): UserDetails {
        TODO("Not yet implemented")
    }
}