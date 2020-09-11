package sut.ist912m.zelen.app.controller

import net.minidev.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sut.ist912m.zelen.app.dto.UserChangePasswordRequest
import sut.ist912m.zelen.app.dto.UserChangeSecretRequest
import sut.ist912m.zelen.app.dto.UserCreateRequest
import sut.ist912m.zelen.app.dto.UserResetPasswordRequest
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.service.UserService

@RestController
@RequestMapping(
        path = ["/api/v1/users"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
        val userService: UserService,
        val jwtTokenUtils: JwtTokenUtils
) {

    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createUser(@RequestBody form: UserCreateRequest): ResponseEntity<*> {
        val userId = userService.createUser(form)
        val jsonResponse = JSONObject()
        jsonResponse.appendField("id", userId)
        jsonResponse.appendField("username", form.username)
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/change-password"], method = [RequestMethod.POST])
    fun updatePassword(
            @RequestHeader("Authorization") jwt : String,
            @RequestBody form: UserChangePasswordRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        userService.changePassword(userId, form)
        val jsonResponse = JSONObject()
        jsonResponse.appendField("message", "Update User [$userId] password successful")
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/reset-password"], method = [RequestMethod.POST])
    fun resetPassword(@RequestBody form: UserResetPasswordRequest): ResponseEntity<*> {
        userService.resetPassword(form)
        val jsonResponse = JSONObject()
        jsonResponse.appendField("message", "Update User [${form.username}] password successful")
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/update-secret"], method = [RequestMethod.POST])
    fun updateSecret(
            @RequestHeader("Authorization") jwt : String,
            @RequestBody form: UserChangeSecretRequest
    ): ResponseEntity<*> {
        val userId = jwtTokenUtils.getUserId(jwt)
        userService.userChangeSecret(userId, form)
        val jsonResponse = JSONObject()
        jsonResponse.appendField("message", "Update User [$userId] secret successful")
        return ResponseEntity.ok(jsonResponse)
    }



}