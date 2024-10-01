import React, {useContext, useEffect, useState} from "react";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {contextLogged} from "../../utils/cookieHandlers";
import {Navigate} from "react-router-dom";
import {contextLobby} from "../utils";

export function Lobby() {
    console.log("Entering Lobby")
    const user = useContext(contextLogged).loggedInState.id
    const [gameId, setGameId] = useState("")
    const [found, setFound] = useState(false)
    const lobby = useContext(contextLobby)

    useEffect(() => {
        const period = 2000;
        const tid = setInterval(async () => {
            if (!found) {
                const fetched = await fetch("api/lobbies/" + user, {
                    method: "GET",
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                if (!fetched.ok) {
                    const error = await fetched.text()
                    alert(error)
                    return <Navigate to="/" replace={true}/>
                } else {
                    const rsp = await fetched.json()
                    if (rsp.properties !== "Lobby is not full") {
                        setGameId(rsp.properties)
                        setFound(true)
                        alert("Lobby Found!")
                    }
                }
            }
        }, period);
        return () => clearInterval(tid);
    });

    return (
        <div>
            <NavBar/>
            {!found ?
                <div>
                    <h1>Lobby</h1>
                    <p>Opening: {lobby.rule}</p>
                    <p>Board Size: {lobby.size}</p>
                </div>
                :
                <Navigate to={"/game/" + gameId} replace={true}/>
            }
            <BottomBar/>
        </div>
    )
}