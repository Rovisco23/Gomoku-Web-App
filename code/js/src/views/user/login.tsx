import React, {useContext, useState} from "react";
import {useFetch} from "../../utils/useFetch";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {Navigate} from "react-router-dom";
import {contextLogged} from "../../utils/cookieHandlers";

export function Login(): React.ReactElement {
    const logged = useContext(contextLogged)
    const [inputs, setInputs] = useState({username: "", password: ""})
    const [submiting, setSubmiting] = useState(false)
    const [error] = useState('')

    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setSubmiting(true)
    }

    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({...inputs, [name]: ev.currentTarget.value})
    }

    function Authenticate(inputs: { username: string, password: string }) {
        const fetch = useFetch(
            "/api/login",
            "POST",
            {},
            JSON.stringify({
                username: inputs.username,
                password: inputs.password
            })
        )
        const rsp = fetch.response
        const error = fetch.error

        if (!rsp && !error) {
            return (
                <div>
                    <h1>Login</h1>
                    <p>Loading</p>
                </div>
            )
        }

        if (error) {
            alert(error)
            setSubmiting(false)
        }

        if (rsp) {
            const props = rsp.properties
            logged.loggedInState = {state: true, auth: true, token: props.token, id: props.userId}
            return <Navigate to={rsp.links[0].href} replace={true}/>
        }
    }

    return (
        <div>
            <NavBar/>
            {!submiting ?
                <form onSubmit={handleSubmit}>
                    <fieldset disabled={submiting}>
                        <h1>Login</h1>
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
                            <button type="submit">Login</button>
                        </p>
                    </fieldset>
                    {error}
                </form>
                :
                <Authenticate username={inputs.username} password={inputs.password}/>
            }
            <BottomBar/>
        </div>
    )
}