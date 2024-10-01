import {useFetch} from "../utils/useFetch";
import {NavBar, BottomBar} from "../utils/NavBar";
import {CreatorInfo} from "./utils";
import React from "react";

export function About() {
    const rsp = useFetch("/api/about", "GET").response
    return (
        <div>
            <NavBar/>
            {!rsp ?
                <div>
                    <h1>About us:</h1>
                    Loading
                </div>
                :
                <div>
                    <h1>About us:</h1>
                    <p>Creators:</p>
                    <CreatorInfo authors={rsp.properties.authors}/>
                    <strong>Version: {rsp.properties.version}</strong>
                </div>
            }
            <BottomBar/>
        </div>
    )
}