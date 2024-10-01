import React from "react";

export const navBarStyle: React.CSSProperties = {
    fontFamily: 'sans-serif',
    listStyleType: 'none',
    margin: 0,
    padding: 0,
    overflow: 'hidden',
    backgroundColor: '#3e73b6'
}

export const bottomBarStyle: React.CSSProperties = {
    fontFamily: 'sans-serif',
    position: 'fixed',
    bottom: 0,
    width: '100%',
    textAlign: 'center',
    lineHeight: '2.5',
    color: "white",
    height: '40px',
    listStyleType: 'none',
    margin: 0,
    padding: 0,
    overflow: 'hidden',
    backgroundColor: '#3e73b6'
}

export const inactiveBlockStyle: React.CSSProperties = {
    display: 'block',
    color: 'white',
    textAlign: 'center',
    padding: '14px 16px',
    textDecoration: 'none'
}

export const activeBlockStyle: React.CSSProperties = {
    display: 'block',
    color: 'white',
    textAlign: 'center',
    padding: '14px 16px',
    textDecoration: 'none',
    pointerEvents: 'none',
    backgroundColor: '#345d99'
}
