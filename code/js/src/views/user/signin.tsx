import React, {useState} from "react";
import {useFetch} from "../../utils/useFetch";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {Navigate} from "react-router-dom";

export function SignIn(): React.ReactElement {

    const [inputs, setInputs] = useState({username: "", password: "", email: ""})
    const [submiting, setSubmiting] = useState(false)
    const [error] = useState('')

    function CreateAccount(inputs: { username: string, email: string, password: string }) {
        const body = JSON.stringify({
            name: inputs.username,
            email: inputs.email,
            password: inputs.password
        })
        const fetch = useFetch("/api/signin", "POST", {}, body)
        const rsp = fetch.response
        const error = fetch.error

        if (error) {
            alert(error)
        }

        if (rsp) {
            return <Navigate to={rsp.links[0].href} replace={true}/>
        }

        return (
            <div>
                <h1>Sign in</h1>
                <p>Loading</p>
            </div>
        )
    }

    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setSubmiting(true)
    }

    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({...inputs, [name]: ev.currentTarget.value})
        console.log("Input changes")
    }

    return (
        <div>
            <NavBar/>
            {!submiting ?
                <form onSubmit={handleSubmit}>
                    <fieldset disabled={submiting}>
                        <h1>Sign In</h1>
                        <p>
                            <label htmlFor="email">Email</label>
                            <input id="email" type="text" name="email" value={inputs.email} onChange={handleChange}/>
                        </p>
                        <p>
                            <label htmlFor="username">Username</label>
                            <input id="username" type="text" name="username" value={inputs.username}
                                   onChange={handleChange}/>
                        </p>
                        <p>
                            <label htmlFor="password">Password</label>
                            <input id="password" type="password" name="password" value={inputs.password}
                                   onChange={handleChange}/>
                        </p>
                        <p>
                            <button type="submit">Sign In</button>
                        </p>
                    </fieldset>
                    {error}
                </form>
                :
                <CreateAccount email={inputs.email} username={inputs.username} password={inputs.password}/>
            }
            <BottomBar/>
        </div>
    )
}