package sut.ist912m.zelen.app.controller

import net.minidev.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import sut.ist912m.zelen.app.entity.Role
import sut.ist912m.zelen.app.dto.UserCreateRequest
import sut.ist912m.zelen.app.repository.UserRepository
import sut.ist912m.zelen.app.service.UserService

@RestController
@RequestMapping(
        path = ["/api/v1/users"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
        val userService: UserService
) {

    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun refreshToken(@RequestBody form: UserCreateRequest): ResponseEntity<*> {
        val userId = userService.createUser(form)
        val jsonResponse = JSONObject()
        jsonResponse.appendField("id", userId)
        jsonResponse.appendField("username", form.username)
        return ResponseEntity.ok(jsonResponse)
    }

//    @RequestMapping(value = ["/change-password"], method = [RequestMethod.POST])
//    fun refreshToken(@RequestBody form: UserCreateRequest): ResponseEntity<*> {
//        val userId = userService.createUser(form)
//        val jsonResponse = JSONObject()
//        jsonResponse.appendField("id", userId)
//        jsonResponse.appendField("username", form.username)
//        return ResponseEntity.ok(jsonResponse)
//    }




}