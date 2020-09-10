package sut.ist912m.zelen.app.service

import org.springframework.dao.DuplicateKeyException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import sut.ist912m.zelen.app.dto.UserCreateRequest
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.exceptions.EntityAlreadyExistsException
import sut.ist912m.zelen.app.exceptions.VerificationException
import sut.ist912m.zelen.app.jwt.JwtUser
import sut.ist912m.zelen.app.repository.UserRepository

@Component
class UserService(
        private val userRepository: UserRepository
) : UserDetailsService {

    //TODO add null handling
    override fun loadUserByUsername(userName: String): JwtUser {
        val user = userRepository.findByUsername(userName)
                ?: throw UsernameNotFoundException("User with username [$userName]")
        return JwtUser(user)
    }

    fun createUser(form: UserCreateRequest) : Long {
        if (form.password2 != form.password1) {
            throw VerificationException("Passwords aren't equals")
        } else {
            val salt = BCrypt.gensalt(8)
            val password = BCrypt.hashpw(form.password1, salt)
            try {
               return userRepository.createUser(
                        username = form.username,
                        password = password,
                        role = Role.USER
                )
            } catch (exc : DuplicateKeyException) {
                throw EntityAlreadyExistsException("User with name [${form.username}] already exists")
            }
        }
    }


}