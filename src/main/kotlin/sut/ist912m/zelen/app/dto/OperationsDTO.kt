package sut.ist912m.zelen.app.dto

import java.time.Instant

data class Operation (
        val id : String,
        val firstUserId : String,
        val secondUserId : String,
        val opState: OpState,
        val opType: OpType,
        val income : Double,
        val outcome : Double,
        val fee : Double,
        val created : Instant,
        val Updated : Instant
)

data class AdminOperation (
        val id : Long,
        val userId : Long,
        val adminId : Long,
        val opType : AdminOpType,
        val created : Instant,
        val reason : String
)