package sut.ist912m.zelen.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import sut.ist912m.zelen.app.repository.UserRepository

@SpringBootApplication
class ZelenAppApplication

fun main(args: Array<String>) {
    runApplication<ZelenAppApplication>(*args)
}
