package sut.ist912m.zelen.app.service

import org.springframework.stereotype.Service
import sut.ist912m.zelen.app.dto.TransferRequest
import sut.ist912m.zelen.app.entity.OpState
import sut.ist912m.zelen.app.entity.OpType
import sut.ist912m.zelen.app.entity.Operation
import sut.ist912m.zelen.app.entity.UserBalance
import sut.ist912m.zelen.app.exceptions.BadOperationException
import sut.ist912m.zelen.app.repository.BalanceRepository
import sut.ist912m.zelen.app.repository.OperationRepository
import sut.ist912m.zelen.app.repository.UserRepository

@Service
class OperationService(
        val balanceRepository: BalanceRepository,
        val operationRepository: OperationRepository
) {

    private val feePercent = 0.015

    fun deposit(userId: Long, amount: Double): Operation {
        balanceRepository.updateBalance(userId, amount)
        val operationId = operationRepository.create(
                senderId = userId,
                receiverId = userId,
                type = OpType.DEPOSIT,
                income = amount,
                outcome = 0.0,
                fee = 0.0,
                state = OpState.COMPLETED
        )
        return operationRepository.findById(operationId)!!
    }

    fun withdrawal(userId: Long, amount: Double): Operation {
        val (_, currentBalance) = balanceRepository.getBalance(userId)
        if (currentBalance < amount.calcFee()){
            throw BadOperationException("Balance is too low for this operation")
        }
        balanceRepository.updateBalance(userId, amount)
        val operationId =  operationRepository.create(
                senderId = userId,
                receiverId = userId,
                type = OpType.WITHDRAWAL,
                income = -amount.calcFee(),
                outcome = amount,
                fee = amount.fee(),
                state = OpState.COMPLETED
        )
        return operationRepository.findById(operationId)!!
    }

    fun createTransfer(userId: Long, form : TransferRequest) : Operation {
        val (_, currentBalance) = balanceRepository.getBalance(userId)
        val (receiver, amount) = form
        val receiverBalance = balanceRepository.findBalance(receiver) ?:
                throw BadOperationException("User with id [${receiver}] doesn't exists")
        if (currentBalance < amount.calcFee()){
            throw BadOperationException("Balance is too low for this operation")
        }
        val operationId =  operationRepository.create(
                senderId = userId,
                receiverId = userId,
                type = OpType.WITHDRAWAL,
                income = -amount.calcFee(),
                outcome = amount,
                fee = amount.fee(),
                state = OpState.CREATED
        )
        return operationRepository.findById(operationId)!!
    }

    // TODO write confirm transfer


    private fun Double.calcFee() : Double = this + this * feePercent
    private fun Double.fee() : Double = this * feePercent

}