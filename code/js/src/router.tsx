import {createBrowserRouter, RouterProvider} from "react-router-dom";
import React from "react";
import {createRoot} from "react-dom/client";
import {Home} from "./views/home";
import {Login} from "./views/user/login";
import {Create} from "./views/game/create";
import {Lobby} from "./views/game/lobby";
import {Rankings} from "./views/user/rankings";
import {SignIn} from "./views/user/signin";
import {About} from "./views/about";
import {Profile} from "./views/user/profile";
import {Game} from "./views/game/game";
import {Logout} from "./views/user/logout";
import {Layout} from "./utils/NavBar";

const router = createBrowserRouter([
    {
        "path": "/",
        "children": [
            {
                "path": "/",
                "element": <Home/>
            },
            {
                "path": "/login",
                "element": <Login/>
            },
            {
                "path": "/create",
                "element": <Create/>
            },
            {
                "path": "/game/:id",
                "element": <Game/>
            },
            {
                "path": "/lobby",
                "element": <Lobby/>
            },
            {
                "path": "/rankings",
                "element": <Rankings/>
            },
            {
                "path": "/signin",
                "element": <SignIn/>
            },
            {
                "path": "/about",
                "element": <About/>
            },
            {
                "path": "/profile",
                "element": <Profile/>
            },
            {
                "path": "/logout",
                "element": <Logout/>
            }
        ]
    }
])

function App(){
    return (
        <Layout>
            <RouterProvider router={router}/>
        </Layout>
    )
}

export function main() {
    const root = createRoot(document.getElementById("container"))
    root.render(<App/>)
}