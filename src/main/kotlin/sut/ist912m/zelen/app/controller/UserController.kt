package sut.ist912m.zelen.app.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sut.ist912m.zelen.app.dto.*
import sut.ist912m.zelen.app.entity.OpType
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.service.UserService
import sut.ist912m.zelen.app.utils.generateResponse

@RestController
@RequestMapping(
        path = ["/api/v1/user"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
        val userService: UserService,
        val jwtTokenUtils: JwtTokenUtils
) {

    @RequestMapping(value = ["/change-password"], method = [RequestMethod.POST])
    fun updatePassword(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: UserChangePasswordRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val username = jwtTokenUtils.getUsername(jwt)
        userService.changePassword(userId, form)
        val response = generateResponse(
                "message" to "Update password for User [${username}] password -> successful")
        return ResponseEntity.ok(response)
    }

    @RequestMapping(value = ["/profile"], method = [RequestMethod.GET])
    fun getMyProfile(@RequestHeader("Authorization") jwt: String) : ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val profile = userService.getUserProfile(userId)
        return ResponseEntity.ok(profile)
    }

    @RequestMapping(value = ["/balance"], method = [RequestMethod.GET])
    fun getMyBalance(@RequestHeader("Authorization") jwt: String) : ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val profile = userService.getUserBalance(userId)
        return ResponseEntity.ok(profile)
    }

    @RequestMapping(value = ["/update-secret"], method = [RequestMethod.POST])
    fun updateSecret(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: UserChangeSecretRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val username = jwtTokenUtils.getUsername(jwt)
        userService.userChangeSecret(userId, form)
        val jsonResponse = generateResponse(
                "message" to "Update secret for User [$username] -> successful")
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/update-info"], method = [RequestMethod.POST])
    fun updateUserInfo(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: UserInfoRequest
    ) : ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        userService.updateUserInfo(userId, form)
        val jsonResponse = generateResponse(
                "message" to "Update info for User [$userId] -> successful")
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/operations"], method = [RequestMethod.POST])
    fun findUserOperations(
            @RequestHeader("Authorization") jwt: String,
            @RequestBody form: OperationsRequest
    ) : ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        val responseList : List<Any> = when (OpType.byId(form.opType)){
            OpType.WITHDRAWAL -> userService.getUserWithdrawals(userId)
            OpType.DEPOSIT -> userService.getUserDeposits(userId)
            OpType.TRANSFER -> userService.getUserTransfers(userId)

        }
        return ResponseEntity.ok(responseList)
    }








}