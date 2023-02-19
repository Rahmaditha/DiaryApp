package com.cookiss.diaryapp.util

object Exceptions {
    class GoogleAccountNotFoundException(
        override val message: String? = "Google Account Not Found"
    ) : Exception()

    class InvalidUserException(message: String) : Exception(message)
}