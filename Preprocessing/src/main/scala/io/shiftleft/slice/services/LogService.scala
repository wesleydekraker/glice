package io.shiftleft.slice.services

class LogService {
    def info(message: String): Unit = {
        println(message)
    }

    def error(message: String, e: Exception): Unit = {
        error(message)
        error(e.getClass.getName)
        error(e.getMessage)

        for (stackTrace <- e.getStackTrace) {
            error(stackTrace.toString)
        }
    }

    def error(messages: String*): Unit = {
        for (message <- messages) {
            println(message)
        }
    }
}
