package sut.ist912m.zelen.app.repository

import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.entity.User
import sut.ist912m.zelen.app.entity.UserBalance
import sut.ist912m.zelen.app.entity.UserInfo
import sut.ist912m.zelen.app.exceptions.EntityAlreadyExistsException
import java.sql.Date
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDate
import javax.sql.DataSource

@Repository
class UserInfoRepository(
        dataSource: DataSource
) {

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val namedJdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val queryInsertUI = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("USER_INFO")
    private val querySelectById = "SELECT ${userInfoFieldsArr.joinToString(", ") { it }} FROM USER_INFO WHERE user_id=?"
    private val querySelectByIds = "SELECT ${userInfoFieldsArr.joinToString(", ") { it }} FROM USER_INFO WHERE user_id IN (:ids)"
    private val queryUpdateUserInfo = "UPDATE USER_INFO SET first_name=?, last_name=?, email=? WHERE user_id=?"

    fun createUserInfo(
            userId: Long,
            firstName: String,
            lastName: String,
            email: String
    ) {
        val params = mapOf(
                "user_id" to userId,
                "first_name" to firstName,
                "last_name" to lastName,
                "email" to email
        )
        try {
            queryInsertUI.execute(params)
        } catch (exc: DuplicateKeyException) {
            throw EntityAlreadyExistsException("UserInfo for user with id [${userId}] already exists")
        }
    }

    fun updateUserInfo(
            userId: Long,
            firstName: String,
            lastName: String,
            email: String
    ) {
        jdbcTemplate.update(queryUpdateUserInfo, firstName, lastName, email, userId)
    }

    fun findById(userId: Long): UserInfo {
        return jdbcTemplate.queryForObject(querySelectById, userId) { rs: ResultSet, rowNum: Int ->
            mapRow(rs, rowNum)
        }
    }

    fun findByIds(ids: List<Long>): List<UserInfo> {
        val params = MapSqlParameterSource()
        params.addValue("ids", ids)
        return namedJdbcTemplate.query(querySelectByIds, params) { rs: ResultSet, rowNum: Int ->
            mapRow(rs, rowNum)
        }
    }


    private fun mapRow(rs: ResultSet, rowNum: Int): UserInfo {
        return UserInfo(
                userId = rs.getLong("user_id"),
                firstName = rs.getString("first_name"),
                lastName = rs.getString("last_name"),
                email = rs.getString("email")
        )

    }

    companion object {
        private val userInfoFieldsArr = arrayOf<String>(
                "user_id",
                "first_name",
                "last_name",
                "email"
        )
    }

}