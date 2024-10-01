import React from 'react'
import {Link, useLocation} from "react-router-dom"
import {contextLogged} from "./cookieHandlers"
import {navBarStyle, activeBlockStyle, inactiveBlockStyle, bottomBarStyle} from "./styleSheet"
import {AuthContainer} from "./AuthContainer";

export function Layout(props){
    return(
        <AuthContainer>
            {props.children}
        </AuthContainer>
    )
}

export function NavBar() {
    const logged_state = React.useContext(contextLogged).loggedInState.state
    const location = useLocation()
    const isLinkActive = (path) => {
        return location.pathname === path
    }
    return(
            <div>
                {!logged_state?
                    <>
                        <ul style={navBarStyle}>
                            <li style={{float: 'left'}}>
                                <Link to={"/"} className={isLinkActive("/") ? '' : 'active'} state={{source: location.pathname}} style={isLinkActive('/') ? activeBlockStyle : inactiveBlockStyle} replace={true}>Home</Link>
                            </li>
                            <li style={{float: 'right'}}>
                                <Link to={"/login"} className={isLinkActive("/login") ? '' : 'active'} style={isLinkActive('/login') ? activeBlockStyle : inactiveBlockStyle}>Login</Link>
                            </li>
                            <li style={{float: 'right'}}>
                                <Link to={"/signin"} className={isLinkActive("/signin") ? '' : 'active'} style={isLinkActive('/signin') ? activeBlockStyle : inactiveBlockStyle}>Sign In</Link>
                            </li>
                            <li style={{float: 'left'}}>
                                <Link to={"/rankings"} className={isLinkActive("/rankings") ? '' : 'active'} style={isLinkActive('/rankings') ? activeBlockStyle : inactiveBlockStyle}>Rankings</Link>
                            </li>
                            <li style={{float: 'left'}}>
                                <Link to={"/about"} className={isLinkActive("/about") ? '' : 'active'} style={isLinkActive('/about') ? activeBlockStyle : inactiveBlockStyle}>About us</Link>
                            </li>
                        </ul>
                    </>
                    :
                    <>
                        <ul style={navBarStyle}>
                            <li style={{float: 'left'}}>
                                <Link to={"/"} className={isLinkActive("/") ? '' : 'active'} style={isLinkActive('/') ? activeBlockStyle : inactiveBlockStyle}>Home</Link>
                            </li>
                            <li style={{float: 'left'}}>
                                <Link to={"/create"} className={isLinkActive("/create") ? '' : 'active'} style={isLinkActive('/create') ? activeBlockStyle : inactiveBlockStyle}>Start Game</Link>
                            </li>
                            <li style={{float: 'left'}}>
                                <Link to={"/rankings"} className={isLinkActive("/rankings") ? '' : 'active'} style={isLinkActive('/rankings') ? activeBlockStyle : inactiveBlockStyle}>Rankings</Link>
                            </li>
                            <li style={{float: 'left'}}>
                                <Link to={"/about"} className={isLinkActive("/about") ? '' : 'active'} style={isLinkActive('/about') ? activeBlockStyle : inactiveBlockStyle}>About us</Link>
                            </li>
                            <li style={{float: 'right'}}>
                                <Link to={"/logout"} className='active' style={inactiveBlockStyle}>Logout</Link>
                            </li>
                            <li style={{float: 'right'}}>
                                <Link to={"/profile"} className={isLinkActive("/profile") ? '' : 'active'} style={isLinkActive('/profile') ? activeBlockStyle : inactiveBlockStyle}>Profile</Link>
                            </li>
                        </ul>
                    </>
                }
            </div>
    )
}

export function BottomBar() {
    return(
        <div>
            <ul style={bottomBarStyle}>
                <li>
                    © GOMOKU.KOM
                </li>
            </ul>
        </div>
    )
}