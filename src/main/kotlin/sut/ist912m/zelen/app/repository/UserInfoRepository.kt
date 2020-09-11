package sut.ist912m.zelen.app.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.entity.User
import sut.ist912m.zelen.app.entity.UserBalance
import sut.ist912m.zelen.app.entity.UserInfo
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
    private val queryInsertUI = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("USER_INFO")
    private val querySelectById = "SELECT ${userInfoFieldsArr.joinToString(", ") { it }} FROM USERS WHERE ID=?"


    fun createUserInfo(
            userId: Long,
            firstName: String,
            lastName: String,
            birthDate: LocalDate,
            email: String,
            city: String,
            country: String
    ) {
        val params = mapOf(
                "user_id" to userId,
                "first_name" to firstName,
                "last_name" to lastName,
                "birth_date" to Date(birthDate.toEpochDay()),
                "email" to email,
                "addres_country" to country,
                "addres_city" to city
        )
        queryInsertUI.execute(params)
    }

    fun findById(userId: Long): UserInfo {
        return jdbcTemplate.queryForObject(querySelectById, userId) { rs: ResultSet, rowNum: Int ->
            mapRow(rs, rowNum)
        }
    }


    private fun mapRow(rs: ResultSet, rowNum: Int): UserInfo {
        return UserInfo(
                userId = rs.getLong("user_id"),
                firstName = rs.getString("first_name"),
                lastName = rs.getString("last_name"),
                email = rs.getString("email"),
                birthDate = rs.getDate("birth_date").toLocalDate(),
                city = kotlin.runCatching { rs.getString("addres_city") }.getOrNull(),
                country = kotlin.runCatching { rs.getString("addres_country") }.getOrNull()
        )
    }

    companion object {
        private val userInfoFieldsArr = arrayOf<String>(
                "user_id",
                "first_name",
                "last_name",
                "birth_date",
                "email",
                "addres_country",
                "addres_city"
        )
    }

}