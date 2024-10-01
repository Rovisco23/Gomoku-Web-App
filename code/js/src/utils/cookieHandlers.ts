import {createContext} from "react";

export const contextLogged = createContext({
    loggedInState: { state: false, auth: false, token: "", id: "" },
});