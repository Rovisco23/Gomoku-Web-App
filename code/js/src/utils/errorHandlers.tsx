import React from 'react';
import {Navigate} from 'react-router-dom'

enum SignInError {
    USERNAME_ALREADY_USED = "That username is already in use.",
    EMAIL_ALREADY_USED = "That email is already in use.",
    INVALID_PASSWORD = "Invalid Password."
}

export function SignInErrorHandle(props) {
    if (SignInError.USERNAME_ALREADY_USED === props.message ||
        SignInError.EMAIL_ALREADY_USED === props.message ||
        SignInError.INVALID_PASSWORD === props.message) {
        alert(props.message)
        return <div>
            <Navigate to={"/signin"}/>
        </div>
    }
}

export function LoginErrorHandle(props) {

    return (
        <div>
            <p>{props.message}</p>
            <Navigate to={"/login"}/>
        </div>
    )
}