package sut.ist912m.zelen.app.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import sut.ist912m.zelen.app.entity.OpState
import sut.ist912m.zelen.app.entity.OpType
import sut.ist912m.zelen.app.entity.Operation
import java.sql.ResultSet
import java.time.Instant
import javax.sql.DataSource

@Repository
class OperationRepository(
        dataSource: DataSource
) {
    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcTemplateNamed = NamedParameterJdbcTemplate(dataSource)

    private val insertQuery = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("USER_OPERATIONS")
            .usingGeneratedKeyColumns("id")

    private val updateQuery = "UPDATE USER_OPERATIONS SET op_state=?, updated=? WHERE  id=?"
    private val selectQuery = "SELECT * FROM USER_OPERATIONS WHERE id=?"
    private val findAllUserOperations = "SELECT DISTINCT * FROM USER_OPERATIONS " +
            "WHERE (sender_id = :id OR receiver_id =:id) AND op_type=:type"

    fun create(
            senderId: Long,
            receiverId: Long,
            type: OpType,
            income: Double,
            outcome: Double,
            fee: Double,
            state: OpState
    ): Long {
        val params = mapOf(
                "SENDER_ID" to senderId,
                "RECEIVER_ID" to receiverId,
                "OP_TYPE" to type.id,
                "INCOME" to income,
                "OUTCOME" to outcome,
                "FEE" to fee,
                "OP_STATE" to state.id,
                "CREATED" to Instant.now().toEpochMilli(),
                "UPDATED" to Instant.now().toEpochMilli()
        )
        return insertQuery.executeAndReturnKey(params).toLong()
    }

    fun update(opId: Long, state: OpState) {
        jdbcTemplate.update(updateQuery, state.id, Instant.now().epochSecond, opId)
    }

    fun findById(opId: Long): Operation? {
        return kotlin.runCatching {
            jdbcTemplate.queryForObject(selectQuery, opId) { rs: ResultSet, rowNum: Int ->
                mapRow(rs, rowNum)
            }
        }.getOrNull()
    }

    fun findUserOperations(userId: Long, type: OpType): List<Operation> {
        val params = MapSqlParameterSource()
        params.addValue("id", userId)
        params.addValue("type", type.id)
        return jdbcTemplateNamed.query(findAllUserOperations, params) { rs: ResultSet, rowNum: Int ->
            mapRow(rs, rowNum)
        }
    }

    private fun mapRow(rs: ResultSet, rowNum: Int): Operation? {
        return kotlin.runCatching {
            Operation(
                    id = rs.getLong("id"),
                    senderId = rs.getLong("sender_id"),
                    receiverId = rs.getLong("receiver_id"),
                    opState = OpState.byId(rs.getByte("OP_STATE").toInt()),
                    opType = OpType.byId(rs.getByte("OP_TYPE").toInt()),
                    income = rs.getDouble("income"),
                    outcome = rs.getDouble("outcome"),
                    fee = rs.getDouble("fee"),
                    created = Instant.ofEpochMilli(rs.getLong("created")),
                    updated = Instant.ofEpochMilli(rs.getLong("updated"))
            )
        }.getOrNull()
    }


}