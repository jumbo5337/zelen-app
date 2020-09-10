package sut.ist912m.zelen.app.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.entity.User
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
    private val querySelectById = "SELECT ${userFieldsArr.joinToString(", ") { it }} FROM USERS WHERE ID=?"
    private val querySelectByUsername = "SELECT ${userFieldsArr.joinToString(", ") { it }} FROM USERS WHERE USERNAME=?"

    fun createUser(username: String, password: String, role: Role): Long {
        val params = mapOf(
                "USERNAME" to username,
                "PASSWORD" to password,
                "LAST_SEEN" to Instant.now().epochSecond,
                "REG_DATE" to Instant.now().epochSecond,
                "USER_ROLE" to role.id
        )
        return queryInsertUser.executeAndReturnKey(params).toLong()
    }

    fun updatePassword(id: Long, password: String): Boolean {
        return jdbcTemplate.update(queryUpdatePassword, password, id) != 0
    }

    fun updateLastSeen(id: Long): Boolean {
        return jdbcTemplate.update(queryUpdateLastSeen, Instant.now().epochSecond, id) != 0
    }

    fun findById(id: Long): User? {
        return jdbcTemplate.queryForObject(querySelectById, id) { rs: ResultSet, rowNum: Int -> mapRow(rs, rowNum) }
    }

    fun findByUsername(username: String): User? {
        return jdbcTemplate.queryForObject(querySelectByUsername, username) { rs: ResultSet, rowNum: Int -> mapRow(rs, rowNum) }
    }

    fun mapRow(rs: ResultSet, rowNum: Int): User? {
        return User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                role = Role.byId(rs.getInt("user_role")),
                lastSeen = Instant.ofEpochMilli(rs.getLong("last_seen")),
                registerTime = Instant.ofEpochMilli(rs.getLong("reg_date"))
        )
    }

    companion object {
        private val userFieldsArr = arrayOf<String>(
                "username",
                "password",
                "reg_date",
                "id",
                "user_role",
                "last_seen"
        )
    }

}
