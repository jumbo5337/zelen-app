package sut.ist912m.zelen.app.entity

import java.time.Instant

data class Operation (
        val id : Long,
        val senderId : Long,
        val receiverId : Long,
        val opState: OpState,
        val opType: OpType,
        val income : Double,
        val outcome : Double,
        val fee : Double,
        val created : Instant,
        val updated : Instant
)

data class AdminOperation (
        val id : Long,
        val userId : Long,
        val adminId : Long,
        val opType : AdminOpType,
        val created : Instant,
        val reason : String
)