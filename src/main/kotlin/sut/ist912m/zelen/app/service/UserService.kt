package sut.ist912m.zelen.app.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import sut.ist912m.zelen.app.dto.*
import sut.ist912m.zelen.app.entity.*
import sut.ist912m.zelen.app.exceptions.VerificationException
import sut.ist912m.zelen.app.jwt.JwtUser
import sut.ist912m.zelen.app.repository.BalanceRepository
import sut.ist912m.zelen.app.repository.OperationRepository
import sut.ist912m.zelen.app.repository.UserInfoRepository
import sut.ist912m.zelen.app.repository.UserRepository

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userInfoRepository: UserInfoRepository,
        private val balanceRepository: BalanceRepository,
        private val operationRepository: OperationRepository
) : UserDetailsService {

    private val pswdEncoder = BCryptPasswordEncoder()

    //TODO add null handling
    override fun loadUserByUsername(userName: String): JwtUser {
        val user = userRepository.getByUsername(userName)
        return JwtUser(user)
    }

    fun createUser(form: UserCreateRequest): Long {
        verifyPasswords(form.password1, form.password2)
        verifyEmail(form.email)
        val salt = BCrypt.gensalt(8)
        val password = BCrypt.hashpw(form.password1, salt)
        val secret = BCrypt.hashpw(form.secretCode, salt)
        val userId = userRepository.createUser(
                username = form.username,
                password = password,
                role = Role.USER,
                secret = secret
        )
        userInfoRepository.createUserInfo(
                userId = userId,
                firstName = form.firstName,
                lastName = form.secondName,
                email = form.email
        )
        balanceRepository.createBalance(userId)
        return userId
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

    fun updateUserInfo(
            userId: Long,
            form: UserInfoRequest
    ) {
        verifyEmail(form.email)
        userInfoRepository.updateUserInfo(
                userId = userId,
                firstName = form.firstName,
                lastName = form.secondName,
                email = form.email
        )
    }

    fun getUserProfile(userId: Long) : UserProfile {
        val user = userRepository.getById(userId)
        val userInfo = userInfoRepository.findById(userId)
        val balance = balanceRepository.getBalance(userId)
        return UserProfile(
                userId = user.id,
                username = user.username,
                registerTime = user.registerTime,
                lastSeen = user.lastSeen,
                role = user.role,
                firstName = userInfo.firstName,
                lastName = userInfo.lastName,
                email = userInfo.email,
                balance = balance.balance
        )

    }

    fun getUserBalance(userId: Long) : UserBalance {
       return balanceRepository.getBalance(userId)
    }

    fun getUserTransfers(userId: Long) : List<Receipt> {
        val userOperations = operationRepository.findUserOperations(userId, OpType.TRANSFER)
        if(userOperations.isEmpty()) return emptyList()
        val idOpMap = userOperations.associate { operation ->
            if(operation.receiverId == userId) {
                operation to operation.senderId
            } else {
                operation to operation.receiverId
            }
        }
        val receiversInfo = userInfoRepository.findByIds(idOpMap.values.toList()).associateBy { it.userId }
        return idOpMap.map { (op, id) -> Receipt(op, receiversInfo[id]!!) }
    }

    fun getUserDeposits(userId: Long) : List<Operation> {
        return operationRepository.findUserOperations(userId, OpType.DEPOSIT)
    }

    fun getUserWithdrawals(userId: Long) : List<Operation> {
        return operationRepository.findUserOperations(userId, OpType.WITHDRAWAL)
    }

    private fun verifyPasswords(p1: String, p2: String) {
        if (p1 != p2) {
            throw VerificationException("Passwords aren't equal")
        }
    }

    private fun verifyEmail(email : String){
        val regex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        if(!regex.matches(email)){
            throw VerificationException("Email is invalid")
        }
    }

}