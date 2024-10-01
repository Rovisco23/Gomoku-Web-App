import {NavBar, BottomBar} from "../utils/NavBar";
import React, {useContext} from "react";
import {contextLogged} from "../utils/cookieHandlers";

export function Home() {
    const user = useContext(contextLogged).loggedInState
    console.log("USER: ", user)
    return (
        <div>
            <NavBar/>
            <h1>Welcome to Gomoku.Kom</h1>
            <BottomBar/>
        </div>
    )
}