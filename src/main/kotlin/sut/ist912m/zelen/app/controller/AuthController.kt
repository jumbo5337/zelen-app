package sut.ist912m.zelen.app.controller

import net.minidev.json.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import sut.ist912m.zelen.app.dto.JwtRequest
import sut.ist912m.zelen.app.dto.JwtResponse
import sut.ist912m.zelen.app.jwt.JwtTokenUtils
import sut.ist912m.zelen.app.jwt.JwtUserDetailsService
import javax.servlet.http.HttpServletRequest


@RestController("api/v1/uaa")
class AuthController(
        private val jwtUtils: JwtTokenUtils,
        private val userDetailsService: JwtUserDetailsService,
        private val authManager: AuthenticationManager
) {

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun generateAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*> {
        val (username, password) = authenticationRequest
        authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        val userDetails: UserDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.username)
        val token: String = jwtUtils.generateToken(userDetails)
        return ResponseEntity.ok(JwtResponse(token))
    }

    @RequestMapping(value = ["/refresh"], method = [RequestMethod.POST])
    fun refreshToken(request: HttpServletRequest): ResponseEntity<*> {
        val token = request.getHeader("Authorization").substring(7)
        return if (jwtUtils.isExpired(token)) {
            val response = JSONObject()
            response.appendField("message", "Token is expired")
            ResponseEntity.badRequest().body(response)
        } else {
            val username = jwtUtils.getUsername(token)
            val user = userDetailsService.loadUserByUsername(username)
            val newToken = jwtUtils.generateToken(user)
            ResponseEntity.ok(JwtResponse(newToken))
        }
    }



}