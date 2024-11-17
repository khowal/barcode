class AppStrings {
    companion object {
        // api url end points and base url
        var API_BASEURL = "https://cbtc.nta.ac.in/api/"
        const val API_LOGIN = "login"
        const val API_BARCODE_INFO = "barcode"
        const val API_SAVE_IMAGE = "saveimage"


        // api parameters
        const val PARAM_EMAIL = "email"
        const val PARAM_PASSWORD = "password"
        const val  PARAM_ROLL_NUMBER = "rollnumber"
        const val  PARAM_PHOTO = "photo"
        const val  PARAM_NAME = "name"

        // error msgs
        var INVALID_EMAIL = "Invalid email id found"
        var INVALID_PASSWORD = "Invalid password found"
        var SOMETHING_WENT_WRONG = "Something went wrong, Please try again later!"
        var NO_INTERNET = "No internet connection"
        var LOADING = "Loading..."

        //
        var INTENT_BARCODE = "intent_barcode"


    }
}