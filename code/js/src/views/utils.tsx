import React, {createContext} from "react";
import {BoardValues, LobbyValues} from "../utils/types";

export const AuthorInfoCard = ({name, number}) => (
    <div style={{ margin: '20px' }}>
        <p>{number} - {name}</p>
    </div>
);

export const RankingCard = ({username, played, won, drawn, winRate}) => (
    <div style={{
        border: '1px solid #ccc',
        borderRadius: '5px',
        padding: '10px',
        marginBottom: '15px'
    }}>
        <h3>{username}</h3>
        <p>Games Played: {played}</p>
        <p>Games Won: {won}</p>
        <p>Games Drawn: {drawn}</p>
        <p>Win : {winRate}</p>
    </div>
);

export function CreatorInfo({authors}: { authors: string }) {
    const authInfo = authors.split(", ")
    const pairInfo = authInfo.map(pair => {
        const [number, name] = pair.split(" - ");
        return {number: parseInt(number), name};
    });
    return (
        <div>
            {pairInfo.map((author, index) => (
                <AuthorInfoCard
                    key={index}
                    name={author.name}
                    number={author.number}
                />
            ))}
        </div>
    )
}

export const contextLobby = createContext<LobbyValues>({
    rule: "Free Style",
    size: 15
})

export function makeBoard(s: string, size: number, turn: string): BoardValues {
    const cells: string[][] = []
    let occupied = 0
    let index = 0
    for (let i = 0; i < size; i++) {
        const row = [];
        for (let j = 0; j < size; j++) {
            if (s[index] !== '-') {
                occupied++
            }
            row.push(s[index]) // Fill each row with 'cells'
            index++
        }
        cells.push(row) // Push the row into the board array
    }
    return {
        cells: cells,
        size: size,
        turn: turn,
        numberOfOccupiedCells: occupied
    }
}