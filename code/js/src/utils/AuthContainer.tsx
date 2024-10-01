import React, {useEffect, useState} from "react";
import {contextLogged} from "./cookieHandlers"

export function AuthContainer({ children }: { children: React.ReactNode }) {
    const [authenticated, setAuthenticated] = useState({ state: false, auth: false, token: undefined, id: ""})
    useEffect(() => {
        fetchGetSession(
            (token: string, id: string) => {
                if (token) setAuthenticated({ state: true, auth: true , token: token, id: id})
                else setAuthenticated({ state: false, auth: false, token: undefined, id: "" })
            }
        )
        return () => { }
    }, [setAuthenticated])

    return (
        <contextLogged.Provider value={{loggedInState: authenticated}}>
            { children }
        </contextLogged.Provider>
    );
}

export async function fetchGetSession(onSuccess: (token: string, id: string) => void) {
    try {
        const resp = await fetch('/api/check', { credentials: 'include' })
        const response = await resp.json()
        const token = response.find(item => item.name === "token")
        const id = response.find(it => it.name === "id")
        onSuccess(token.value, id.value)
    } catch (error) {
        //throw new Error('Failed to fetch session')
    }
}
