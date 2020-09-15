package sut.ist912m.zelen.app.controller

import net.minidev.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import sut.ist912m.zelen.app.dto.UserCreateRequest
import sut.ist912m.zelen.app.dto.UserResetPasswordRequest
import sut.ist912m.zelen.app.jwt.JwtRequest
import sut.ist912m.zelen.app.jwt.JwtResponse
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.service.UserService
import sut.ist912m.zelen.app.utils.generateResponse
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(
        path = ["/api/v1/auth"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
)
class AuthController(
        private val jwtUtils: JwtTokenUtils,
        private val userService: UserService,
        private val authManager: AuthenticationManager
) {

    @RequestMapping(value = ["/token"], method = [RequestMethod.POST])
    fun generateAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*> {
        val (username, password) = authenticationRequest
        authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        val userDetails = userService.loadUserByUsername(authenticationRequest.username)
        userService.updateLastSeen(userDetails.user.id)
        val token: String = jwtUtils.generateToken(userDetails)
        return ResponseEntity.ok(JwtResponse(token))
    }

    @RequestMapping(value = ["/refresh"], method = [RequestMethod.GET])
    fun refreshToken(request: HttpServletRequest): ResponseEntity<*> {
        val token = request.getHeader("Authorization").substring(7)
        return if (jwtUtils.isExpired(token)) {
            val response = generateResponse("message" to "Token is expired")
            ResponseEntity.badRequest().body(response)
        } else {
            val username = jwtUtils.getUsername(token)
            val user = userService.loadUserByUsername(username)
            userService.updateLastSeen(user.user.id)
            val newToken = jwtUtils.generateToken(user)
            ResponseEntity.ok(JwtResponse(newToken))
        }
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun createUser(@RequestBody form: UserCreateRequest): ResponseEntity<*> {
        val userId = userService.createUser(form)
        val jsonResponse = generateResponse(
                "id" to userId,
                "username" to form.username)
        return ResponseEntity.ok(jsonResponse)
    }

    @RequestMapping(value = ["/reset-password"], method = [RequestMethod.POST])
    fun resetPassword(@RequestBody form: UserResetPasswordRequest): ResponseEntity<*> {
        userService.resetPassword(form)
        val response = generateResponse(
                "message" to "Update password for User [${form.username}] -> successful")
        return ResponseEntity.ok(response)
    }



}