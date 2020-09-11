package sut.ist912m.zelen.app.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import sut.ist912m.zelen.app.dto.UserChangePasswordRequest
import sut.ist912m.zelen.app.dto.UserChangeSecretRequest
import sut.ist912m.zelen.app.dto.UserCreateRequest
import sut.ist912m.zelen.app.dto.UserResetPasswordRequest
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.exceptions.VerificationException
import sut.ist912m.zelen.app.jwt.JwtUser
import sut.ist912m.zelen.app.repository.UserRepository

@Component
class UserService(
        private val userRepository: UserRepository
) : UserDetailsService {

    private val  pswdEncoder = BCryptPasswordEncoder()

    //TODO add null handling
    override fun loadUserByUsername(userName: String): JwtUser {
        val user = userRepository.getByUsername(userName)
        return JwtUser(user)
    }

    fun createUser(form: UserCreateRequest): Long {
        verifyPasswords(form.password1, form.password2)
        val salt = BCrypt.gensalt(8)
        val password = BCrypt.hashpw(form.password1, salt)
        val secret = BCrypt.hashpw(form.secretCode, salt)
        return userRepository.createUser(
                username = form.username,
                password = password,
                role = Role.USER,
                secret = secret
        )

    }

    fun changePassword(
            userId: Long,
            form: UserChangePasswordRequest
    ) {
        verifyPasswords(form.password1, form.password2)
        val user = userRepository.getById(userId)
        if (!pswdEncoder.matches(form.oldPassword, user.password)) {
            throw VerificationException("Old password is incorrect")
        }
        val salt = BCrypt.gensalt(8)
        val password = BCrypt.hashpw(form.password1, salt)
        userRepository.updatePassword(userId, password)
    }

    fun resetPassword(form: UserResetPasswordRequest) {
        verifyPasswords(form.password1, form.password2)
        val user = userRepository.getByUsername(form.username)
        if (!pswdEncoder.matches(form.secretCode, user.secret)) {
            throw VerificationException("Secret doesn't match")
        }
        val salt = BCrypt.gensalt(8)
        val password = BCrypt.hashpw(form.password1, salt)
        userRepository.updatePassword(user.id, password)
    }

    fun userChangeSecret(
            userId: Long,
            form: UserChangeSecretRequest
    ) {
        userRepository.getById(userId)
        val salt = BCrypt.gensalt(8)
        val secret = BCrypt.hashpw(form.secretCode, salt)
        userRepository.updateSecretCode(userId, secret)
    }

    fun updateLastSeen(userId: Long) {
        userRepository.updateLastSeen(userId)
    }


    private fun verifyPasswords(p1: String, p2: String) {
        if (p1 != p2) {
            throw VerificationException("Passwords aren't equal")
        }
    }


}