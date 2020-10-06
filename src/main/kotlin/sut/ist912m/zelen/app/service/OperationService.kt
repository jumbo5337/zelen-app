package sut.ist912m.zelen.app.service

import org.springframework.stereotype.Service
import sut.ist912m.zelen.app.dto.TransferRequest
import sut.ist912m.zelen.app.dto.Receipt
import sut.ist912m.zelen.app.entity.OpState
import sut.ist912m.zelen.app.entity.OpType
import sut.ist912m.zelen.app.entity.Operation
import sut.ist912m.zelen.app.exceptions.BadOperationException
import sut.ist912m.zelen.app.repository.BalanceRepository
import sut.ist912m.zelen.app.repository.OperationRepository
import sut.ist912m.zelen.app.repository.UserInfoRepository
import kotlin.math.abs

@Service
class OperationService(
       private val balanceRepository: BalanceRepository,
       private val operationRepository: OperationRepository,
       private val userInfoRepository: UserInfoRepository
) {

    private val feePercent = 0.015

    fun deposit(userId: Long, amount: Double): Operation {
        if (amount < 0) {
            throw BadOperationException("Enter a valid amount")
        }
        val (_, currentBalance) = balanceRepository.getBalance(userId)
        balanceRepository.updateBalance(userId, currentBalance + amount)
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
        if (currentBalance < amount.calcFee()) {
            throw BadOperationException("Balance is too low for this operation")
        }
        if (amount < 0) {
            throw BadOperationException("Enter a valid amount")
        }
        balanceRepository.updateBalance(userId, currentBalance - amount.calcFee())
        val operationId = operationRepository.create(
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

    fun createTransfer(userId: Long, form: TransferRequest): Receipt {
        val (receiver, amount) = form
        if (receiver == userId) {
           throw BadOperationException("Self-transfers are prohibited")
        }
        if (form.amount < 0) {
            throw BadOperationException("Enter a valid amount")
        }
        val (_, currentBalance) = balanceRepository.getBalance(userId)
        val receiverBalance = balanceRepository.findBalance(receiver)
                ?: throw BadOperationException("User with id [${receiver}] doesn't exists")
        if (currentBalance < amount.calcFee()) {
            throw BadOperationException("Balance is too low for this operation")
        }
        val operationId = operationRepository.create(
                senderId = userId,
                receiverId = receiver,
                type = OpType.TRANSFER,
                income = -amount.calcFee(),
                outcome = amount,
                fee = amount.fee(),
                state = OpState.CREATED
        )
        val operation = operationRepository.findById(operationId)!!
        val userInfo = userInfoRepository.findById(form.receiverId)
        return Receipt(operation, userInfo)
    }

    fun confirmTransfer(userId: Long, opId: Long): Receipt {
        val (_, currentBalance) = balanceRepository.getBalance(userId)
        val operation = operationRepository.findById(opId)
                ?: throw BadOperationException("Operation with id [$opId] doesn't exist")
        if(operation.senderId != userId){
            throw BadOperationException("Wrong userId for confirmation")
        } else if (operation.opType != OpType.TRANSFER) {
            throw BadOperationException("Wrong method for transfer")
        } else if (currentBalance < abs(operation.income)) {
            throw BadOperationException("Balance is too low for this operation")
        } else if (operation.opState != OpState.CREATED) {
            throw BadOperationException("Operation is already [${operation.opState}]")
        }
        val (_, receiverBalance) = balanceRepository.getBalance(operation.receiverId)
        val senderBalance = currentBalance.plus(operation.income)
        val receiverNB = receiverBalance.plus(operation.outcome)
        balanceRepository.updateBalance(userId, senderBalance)
        balanceRepository.updateBalance(operation.receiverId,receiverNB)
        operationRepository.update(opId, OpState.COMPLETED)
        val finalOp = operationRepository.findById(opId)!!
        val receiverInfo = userInfoRepository.findById(finalOp.receiverId)
        return Receipt(finalOp, receiverInfo)
    }

    fun cancelTransfer(userId: Long, opId: Long): Receipt {
        operationRepository.findById(opId)
                ?: throw BadOperationException("Operation with id [$opId] doesn't exist")
        operationRepository.update(opId, OpState.CANCELED)
        val finalOp = operationRepository.findById(opId)!!
        val receiverInfo = userInfoRepository.findById(finalOp.receiverId)
        return Receipt(finalOp, receiverInfo)
    }


    private fun Double.calcFee(): Double = this + this * feePercent
    private fun Double.fee(): Double = this * feePercent

}