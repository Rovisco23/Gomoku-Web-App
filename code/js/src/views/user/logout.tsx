import React from "react";
import {Navigate} from "react-router-dom";
import {useFetch} from "../../utils/useFetch";
import {BottomBar, NavBar} from "../../utils/NavBar";
import {contextLogged} from "../../utils/cookieHandlers";

export function Logout(): React.ReactElement {
    const logged = React.useContext(contextLogged)
    const fetch = useFetch(
        "/api/logout",
        "POST",
    )
    const rsp = fetch.response
    const error = fetch.error
    if (error) {
        alert(error)
        return <Navigate to="/" replace={true}/>
    }

    if (rsp) {
        logged.loggedInState = {state: false, auth: false, token: undefined, id: ""}
    }

    return (
        <div>
            <NavBar/>
            {!rsp ?
                <div>
                    <h1>Logout</h1>
                    <p>Loading</p>
                </div>
                :
                <Navigate to="/" replace={true}/>
            }
            <BottomBar/>
        </div>
    )
}
