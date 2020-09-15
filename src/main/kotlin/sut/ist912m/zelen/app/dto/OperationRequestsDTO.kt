package sut.ist912m.zelen.app.dto

import sut.ist912m.zelen.app.entity.OpState
import sut.ist912m.zelen.app.entity.OpType
import sut.ist912m.zelen.app.entity.Operation
import sut.ist912m.zelen.app.entity.UserInfo
import java.time.Instant


data class IORequest (
        val amount : Double
)

data class TransferRequest (
        val receiverId : Long,
        val amount: Double
)

data class TransferResponse (
        val operation: Operation,
        val receiverInfo: UserInfo
)

data class ConfirmRequest (
        val operationId : Long,
        val confirm : Boolean
)


