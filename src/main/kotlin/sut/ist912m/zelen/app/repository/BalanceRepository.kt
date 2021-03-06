package sut.ist912m.zelen.app.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import sut.ist912m.zelen.app.entity.UserBalance
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class BalanceRepository(
        dataSource: DataSource
) {

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val queryInsertAccount = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("USER_BALANCE")
    private val queryGetAccount = "SELECT * FROM USER_BALANCE WHERE user_id=?"
    private val updateUserBalance = "UPDATE USER_BALANCE SET balance=? WHERE user_id=?"

    fun createBalance(userId:Long){
        val params = mapOf(
                "user_id" to userId,
                "balance" to 50
        )
        queryInsertAccount.execute(params)
    }

    fun updateBalance(userId: Long, amount: Double){
        jdbcTemplate.update(updateUserBalance, amount, userId)
    }

    fun getBalance(userId:Long) : UserBalance{
      return jdbcTemplate.queryForObject(queryGetAccount, userId) { rs: ResultSet, rowNum: Int ->
           UserBalance(
                   userId = rs.getLong("user_id"),
                   balance = rs.getDouble("balance")
           )
       }
    }

    fun findBalance(userId:Long) : UserBalance?{
        return kotlin.runCatching {
            jdbcTemplate.queryForObject(queryGetAccount, userId) { rs: ResultSet, rowNum: Int ->
                UserBalance(
                        userId = rs.getLong("user_id"),
                        balance = rs.getDouble("balance")
                )
            }
        }.getOrNull()
    }






}