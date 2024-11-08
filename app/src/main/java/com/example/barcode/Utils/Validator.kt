object Validator {

    // Email validation using regex pattern
    fun isValidEmail(email: String?): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return !email.isNullOrEmpty() && Regex(emailPattern).matches(email)
    }

    // Password validation: minimum e characters
    fun isValidPassword(password: String?): Boolean {
        return !password.isNullOrEmpty() && password.length >= 6
    }
}
