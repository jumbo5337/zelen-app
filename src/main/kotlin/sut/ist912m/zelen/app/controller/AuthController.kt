package sut.ist912m.zelen.app.controller

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


@RestController("api/v1/")
class AuthController(
        private val jwtUtils: JwtTokenUtils,
        private val userDetailsService: JwtUserDetailsService,
        private val authManager: AuthenticationManager
) {

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    @Throws(Exception::class)
    fun generateAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*> {
        val (username, password) = authenticationRequest
        authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        val userDetails: UserDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.username)
        val token: String = jwtUtils.generateToken(userDetails)
        return ResponseEntity.ok(JwtResponse(token))
    }

}