package sut.ist912m.zelen.app.jwt

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import sut.ist912m.zelen.app.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails as UserDetails

@Component
class JwtUserDetailsService(
        private val userRepository: UserRepository
) : UserDetailsService {

    //TODO add null handling
    override fun loadUserByUsername(userName: String): JwtUser {
        val user = userRepository.findByUsername(userName)
                ?: throw UsernameNotFoundException("User with username [$userName]")
        return JwtUser(user)
    }
}