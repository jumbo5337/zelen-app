package sut.ist912m.zelen.app.jwt

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter(
        private val userDetailsService: JwtUserDetailsService,
        private val jwtTokenUtils: JwtTokenUtils
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filter: FilterChain
    ) {
        val token = request.getHeader("Authorization")
        try {
            if (!token.isNullOrEmpty()) {
                val jwt = if (token.startsWith("Bearer ")) {
                    token.substring(7)
                } else {
                    log.warn("JWT Token does not begin with Bearer String")
                    token
                }

                val username = jwtTokenUtils.getUsername(jwt)
                if (SecurityContextHolder.getContext().authentication == null) {
                    val jwtUser = userDetailsService.loadUserByUsername(username)
                    if (jwtTokenUtils.validate(jwt, jwtUser)) {
                        val upaToken = UsernamePasswordAuthenticationToken(
                                jwtUser, null, jwtUser.authorities
                        )
                        upaToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = upaToken
                    }
                }
            }
        } catch (exc: Exception) {
            log.error("Unable to load User Information from JWT. Authorization Failed")
        } finally {
            filter.doFilter(request, response)
        }

    }


}