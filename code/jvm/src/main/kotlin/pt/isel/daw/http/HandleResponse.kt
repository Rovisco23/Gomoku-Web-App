package pt.isel.daw.http

import org.springframework.http.ResponseEntity
import pt.isel.daw.http.utils.Failure
import pt.isel.daw.http.utils.Success
import pt.isel.daw.http.model.Errors
import pt.isel.daw.http.utils.Result

inline fun <reified T> handleResponse(
    res: Result<Errors,T>,
    makeResponse: (T) -> ResponseEntity<*>
): ResponseEntity<*> {
    return when (res) {
        is Success -> {
            makeResponse(res.value)
        }
        is Failure -> {
            Errors.response(res.value)
        }
    }
}