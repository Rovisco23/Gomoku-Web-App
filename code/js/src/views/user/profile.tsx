import React, {useContext} from "react";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {useFetch} from "../../utils/useFetch";
import {contextLogged} from "../../utils/cookieHandlers";
export function Profile() {
    const user = useContext(contextLogged).loggedInState.id
    const rsp = useFetch("/api/users/" + user, "GET").response

    return (
        <div>
            <NavBar/>
            <h1>Profile:</h1>
            {!rsp ? (
                <div>
                <p>Loading</p>
                </div>
            ) : (
                <div>
                    <p>Username: {rsp.properties.username}</p>
                    <p>Email: {rsp.properties.email}</p>
                </div>
            )}
            <BottomBar/>
        </div>
    )
}