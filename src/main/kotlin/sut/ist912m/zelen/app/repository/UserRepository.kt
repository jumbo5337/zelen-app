package sut.ist912m.zelen.app.repository

import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.entity.User
import sut.ist912m.zelen.app.exceptions.EntityAlreadyExistsException
import sut.ist912m.zelen.app.exceptions.EntityNotFoundException
import java.sql.ResultSet
import java.time.Instant
import javax.sql.DataSource

@Repository
class UserRepository(
        dataSource: DataSource
) {

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val queryInsertUser = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("USERS")
            .usingGeneratedKeyColumns("id")
    private val queryUpdatePassword = "UPDATE USERS SET PASSWORD=? WHERE ID=?"
    private val queryUpdateLastSeen = "UPDATE USERS SET LAST_SEEN=? WHERE ID=?"
    private val queryUpdateSecret = "UPDATE USERS SET SECRET_CODE=? WHERE ID=?"
    private val querySelectById = "SELECT ${userFieldsArr.joinToString(", ") { it }} FROM USERS WHERE ID=?"
    private val querySelectByUsername = "SELECT ${userFieldsArr.joinToString(", ") { it }} FROM USERS WHERE USERNAME=?"

    fun createUser(
            username: String,
            password: String,
            role: Role,
            secret: String
    ): Long {
        val params = mapOf(
                "USERNAME" to username,
                "PASSWORD" to password,
                "LAST_SEEN" to Instant.now().epochSecond,
                "REG_DATE" to Instant.now().epochSecond,
                "SECRET_CODE" to secret,
                "USER_ROLE" to role.id
        )
        return try {
            queryInsertUser.executeAndReturnKey(params).toLong()
        } catch (exc: DuplicateKeyException) {
            throw EntityAlreadyExistsException("User with name [${username}] already exists")
        }
    }

    fun updatePassword(id: Long, password: String) {
        jdbcTemplate.update(queryUpdatePassword, password, id) != 0
    }

    fun updateLastSeen(id: Long) {
        jdbcTemplate.update(queryUpdateLastSeen, Instant.now().epochSecond, id)
    }

    fun updateSecretCode(id: Long, secret: String) {
        jdbcTemplate.update(queryUpdateSecret, secret, id)
    }

    fun getById(id: Long): User {
        return findById(id)
                ?: throw EntityNotFoundException("User with id [$id] doesn't exists")
    }

    fun findById(id: Long): User? {
        return jdbcTemplate.queryForObject(querySelectById, id) { rs: ResultSet, rowNum: Int -> mapRow(rs, rowNum) }
    }

    fun getByUsername(username: String): User {
        return findByUsername(username)
                ?: throw EntityNotFoundException("User with username [$username] doesn't exists")
    }

    fun findByUsername(username: String): User? {
        return jdbcTemplate.queryForObject(querySelectByUsername, username) { rs: ResultSet, rowNum: Int -> mapRow(rs, rowNum) }
    }

    private fun mapRow(rs: ResultSet, rowNum: Int): User? {
        return User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                role = Role.byId(rs.getInt("user_role")),
                secret = rs.getString("secret_code"),
                lastSeen = Instant.ofEpochMilli(rs.getLong("last_seen")),
                registerTime = Instant.ofEpochMilli(rs.getLong("reg_date"))
        )
    }

    companion object {
        private val userFieldsArr = arrayOf(
                "username",
                "password",
                "reg_date",
                "id",
                "user_role",
                "last_seen",
                "secret_code"
        )
    }

}
