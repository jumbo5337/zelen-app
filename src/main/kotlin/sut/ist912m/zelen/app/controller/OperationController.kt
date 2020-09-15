package sut.ist912m.zelen.app.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sut.ist912m.zelen.app.dto.ConfirmRequest
import sut.ist912m.zelen.app.dto.IORequest
import sut.ist912m.zelen.app.dto.TransferRequest
import sut.ist912m.zelen.app.dto.UserChangePasswordRequest
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.service.OperationService

@RestController
@RequestMapping(
        path = ["/api/v1/ops"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class OperationController(
        val jwtTokenUtils: JwtTokenUtils,
        val operationService: OperationService
) {

    @RequestMapping(value = ["/deposit"], method = [RequestMethod.POST])
    fun deposit(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: IORequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val deposit = operationService.deposit(userId, form.amount)
        return ResponseEntity.ok(deposit)
    }

    @RequestMapping(value = ["/withdrawal"], method = [RequestMethod.POST])
    fun withdrawal(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: IORequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val withdrawal = operationService.withdrawal(userId, form.amount)
        return ResponseEntity.ok(withdrawal)
    }

    @RequestMapping(value = ["/transfer"], method = [RequestMethod.POST])
    fun createTransfer(@RequestHeader("Authorization") jwt: String,
                       @RequestBody form: TransferRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val receipt = operationService.createTransfer(userId, form)
        return ResponseEntity.ok(receipt)
    }

    @RequestMapping(value = ["/transfer"], method = [RequestMethod.PUT])
    fun confirm(@RequestHeader("Authorization") jwt: String,
                @RequestBody form: ConfirmRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val receipt = when (form.confirm) {
            true -> operationService.confirmTransfer(userId, form.operationId)
            false -> operationService.cancelTransfer(userId, form.operationId)
        }
        return ResponseEntity.ok(receipt)
    }


}