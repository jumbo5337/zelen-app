package sut.ist912m.zelen.app.exceptions

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sut.ist912m.zelen.app.utils.generateResponse

@ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(value = [Throwable::class])
    fun handle(e: Throwable): ResponseEntity<*> {
        val response = generateResponse("message" to e.localizedMessage)
        return ResponseEntity.badRequest().body(response)
    }



}