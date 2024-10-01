import React, {ChangeEvent, useContext, useState} from "react";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {useFetch} from "../../utils/useFetch";
import {contextLogged} from "../../utils/cookieHandlers";
import {Navigate} from "react-router-dom";
import {contextLobby} from "../utils";


export function Create() {
    const [opening, setOpening] = useState<string>("Free Style")
    const [boardSize, setBoardSize] = useState<number>(15)
    const [submiting, setSubmit] = useState(false)

    const lobby = useContext(contextLobby)
    lobby.rule = opening
    lobby.size = boardSize

    const handleOpeningChange = (event: ChangeEvent<HTMLSelectElement>) => {
        setOpening(event.target.value);
        lobby.rule = event.target.value
    };

    const handleBoardSizeChange = (event: ChangeEvent<HTMLSelectElement>) => {
        setBoardSize(Number(event.target.value));
        lobby.size = Number(event.target.value)
    };

    const handleSubmit = () => {
        setSubmit(true)
    }

    function StartGame() {
        const user = useContext(contextLogged).loggedInState.id
        const fetch = useFetch(
            "/api/games",
            "POST",
            {},
            JSON.stringify({
                userId: user,
                rule: opening,
                boardSize: boardSize
            })
        )
        const rsp = fetch.response
        const error = fetch.error

        if (!rsp && !error) {
            return (
                <div>
                    <h1>Game</h1>
                    <p>Loading</p>
                </div>
            )
        }

        if (error) {
            alert(error)
            return <Navigate to="/login" replace={true}/>
        }

        if (rsp) {
            if (rsp.properties === "Lobby Created") {
                return <Navigate to={rsp.links[0].href} replace={true}/>
            } else {
                return <Navigate to={"/game/" + rsp.properties} replace={true}/>
            }
        }
    }

    return (
        <div>
            <div>
                <NavBar/>
                {!submiting ?
                    <div>
                        <h1>Choose the rules you wanna use</h1>
                        <p>Openings:
                            <select name="opening" id="opening" value={lobby.rule} onChange={handleOpeningChange}>
                                <option value="Free Style">Free Style</option>
                                <option value="Pro">Pro</option>
                                <option value="Long Pro">Long Pro</option>
                            </select>
                        </p>
                        <p>Board size:
                            <select name="board-size" id="board-size" value={lobby.size}
                                    onChange={handleBoardSizeChange}>
                                <option value={15}>15</option>
                                <option value={19}>19</option>
                            </select>
                        </p>
                        <p>
                            <button onClick={handleSubmit}>Submit</button>
                        </p>
                    </div>
                    :
                    <StartGame/>
                }
                <BottomBar/>
            </div>
        </div>
    )
}
