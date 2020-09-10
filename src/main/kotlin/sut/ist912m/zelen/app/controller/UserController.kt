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

@RestController
@RequestMapping(
        path = ["/api/v1/users"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
        val userRepository: UserRepository
) {

    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun refreshToken(@RequestBody form: UserCreateRequest): ResponseEntity<*> {
        return if (form.password2 != form.password1) {
            val response = JSONObject()
            response.appendField("message", "Passwords aren't equals")
            ResponseEntity.badRequest().body(response)
        } else {
            val salt = BCrypt.gensalt(8)
            val password = BCrypt.hashpw(form.password1, salt)
            userRepository.createUser(
                    username = form.username,
                    password = password,
                    role = Role.USER
            )
            val response = JSONObject()
            response.appendField("message", "User with username [${form.username}] created")
            ResponseEntity.ok(response)
        }
    }




}