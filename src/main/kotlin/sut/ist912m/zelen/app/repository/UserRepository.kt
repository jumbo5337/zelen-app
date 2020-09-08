package sut.ist912m.zelen.app.repository

import sut.ist912m.zelen.app.dto.Role
import sut.ist912m.zelen.app.dto.User

interface UserRepository {

    fun createUser(username: String, password: String, role: Role): Boolean

    fun updatePassword(id: Long, password: String) : Boolean

    fun updateLastSeen(id: Long) : Boolean

    fun findById(id: Long) : User?

    fun findByUsername(username: String) : User?



    
}