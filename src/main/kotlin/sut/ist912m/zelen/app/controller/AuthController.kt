package sut.ist912m.zelen.app.controller

import net.minidev.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import sut.ist912m.zelen.app.jwt.JwtRequest
import sut.ist912m.zelen.app.jwt.JwtResponse
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.service.UserService
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
        val token: String = jwtUtils.generateToken(userDetails)
        return ResponseEntity.ok(JwtResponse(token))
    }

    @RequestMapping(value = ["/refresh"], method = [RequestMethod.GET])
    fun refreshToken(request: HttpServletRequest): ResponseEntity<*> {
        val token = request.getHeader("Authorization").substring(7)
        return if (jwtUtils.isExpired(token)) {
            val response = JSONObject()
            response.appendField("message", "Token is expired")
            ResponseEntity.badRequest().body(response)
        } else {
            val username = jwtUtils.getUsername(token)
            val user = userService.loadUserByUsername(username)
            val newToken = jwtUtils.generateToken(user)
            ResponseEntity.ok(JwtResponse(newToken))
        }
    }



}