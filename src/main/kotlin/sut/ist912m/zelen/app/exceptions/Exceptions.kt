package sut.ist912m.zelen.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
class EntityAlreadyExistsException(msg : String) : RuntimeException(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EntityNotFoundException(msg : String) : RuntimeException(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class VerificationException(msg : String) : RuntimeException(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class TokenExpiredException(msg : String) : RuntimeException(msg)