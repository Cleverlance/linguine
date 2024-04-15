package util

object Logger {
    fun info(message: Any) {
        println("INFO: $message")
    }

    fun warn(message: Any) {
        println("WARNING: $message")
    }

    fun error(message: Any) {
        println("ERROR: $message")
    }
}