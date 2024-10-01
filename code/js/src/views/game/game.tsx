import React, {useContext, useEffect, useState} from "react";
import {NavBar} from "../../utils/NavBar";
import {useFetch} from "../../utils/useFetch";
import {contextLogged} from "../../utils/cookieHandlers";
import {useParams} from "react-router-dom";
import {makeBoard} from "../utils";
import {Button, Container, Row} from "react-bootstrap";
import {GameValues} from "../../utils/types";
import black_stone from '../../utils/images/black_stone.png';
import white_stone from '../../utils/images/white_stone.png';

const button_style: React.CSSProperties = {
    background: 'linear-gradient(to bottom, transparent 47%, #000 47%, #000 53%,  transparent 53%), linear-gradient(to right, transparent 47%, #000 47%, #000 53%, transparent 53%)',
    backgroundColor: '#B87333',
    maxWidth: `50px`,
    minWidth: `50px`,
    height: `50px`,
    margin: '0',
    padding: '0',
    border: 'none'
}

export function Game() {
    const user = useContext(contextLogged).loggedInState.id
    const gameId = useParams().id
    const [game, setGame] = useState(undefined)
    const rsp = useFetch("/api/games/" + gameId, "GET", {}, null).response

    useEffect(() => {
        const period = 2000;
        if (game && game.state !== "Finished") {
            const tid = setInterval(async () => {
                const fetched = await fetch(`/api/games/${gameId}`, {
                    method: "GET",
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                const rsp2 = await fetched.json()
                if (rsp2.properties) {
                    const props = rsp2.properties
                    const newBoard = makeBoard(props.board.cells, props.board.size, props.board.turn)
                    if (newBoard.numberOfOccupiedCells > game.board.numberOfOccupiedCells || props.state == "Finished") {
                        const newGame: GameValues = {
                            ...game,
                            state: props.state,
                            board: newBoard,
                            winner: props.winner === game.playerB ? game.blackName : props.winner === game.playerW ? game.whiteName : null
                        }
                        setGame(newGame)
                    }
                }
            }, period);
            return () => clearInterval(tid);
        }
    }, [game]);

    async function play(row: number, col: number) {
        console.log(`Clicked on cell (${row}, ${col})`);
        try {
            const u: string = user
            const char: string = 'a';
            const newChar: string = String.fromCharCode(char.charCodeAt(0) + col);
            const fetched = await fetch(
                "/api/games/" + gameId,
                {
                    method: "POST",
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        "userId": u,
                        "row": row + 1,
                        "col": newChar
                    })
                }
            )
            if (!fetched.ok) {
                const error = await fetched.text()
                if (error === "This game already ended.") {
                    if (game.winner) {
                        alert(`Game ended. ${game.winner} Won!`)
                        return
                    } else {
                        alert("Game ended. Draw!")
                        return
                    }
                }
                alert(error)
            } else {
                const rsp = await fetched.json()
                console.log("Response OK:", rsp)
                const props = rsp.properties
                const newBoard = makeBoard(props.board.cells, props.board.size, props.board.turn)
                console.log("NEWBOARD: ", newBoard)
                const newGame: GameValues = {
                    ...game,
                    state: props.state,
                    board: newBoard,
                    winner: props.winner === game.playerB ? game.blackName : props.winner === game.playerW ? game.whiteName : undefined
                }
                console.log(newGame)
                setGame(newGame)
            }
        } catch (error) {
            console.log("Error:", error)
        }
    }

    function WaitGame() {
        return (
            <div>
                {!rsp ?
                    <div>
                        <h1>Gomoku</h1>
                        <p>Loading</p>
                    </div>
                    :
                    <StartGame/>
                }
            </div>
        );
    }

    function StartGame() {
        const props = rsp.properties
        const game: GameValues = {
            id: props.id,
            board: makeBoard(props.board.cells, props.board.size, props.board.turn),
            state: props.state,
            playerW: props.playerW,
            playerB: props.playerB,
            whiteName: props.whiteName,
            blackName: props.blackName,
            win_rate_w: props.win_rate_w.toFixed(2),
            win_rate_b: props.win_rate_b.toFixed(2),
            winner: props.winner === props.playerB ? props.blackName : props.winner === props.playerW ? props.whiteName : undefined
        }
        setGame(game)
        return (<div></div>)
    }

    async function giveUp() {
        if (game.state == "Finished"){
            if (game.winner) {
                alert("Game is already Finished. " + game.winner + " won!")
            } else {
                alert("Game is already Finished. Draw!")
            }
            return
        }
        try {
            const fetched = await fetch("/api/giveup/" + gameId, {
                method: "POST",
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            if (!fetched.ok) {
                const error = await fetched.text()
                alert(error)
            } else {
                const rsp = await fetched.json()
                const props = rsp.properties
                const newGame: GameValues = {
                    ...game,
                    state: props.state,
                    winner: props.winner === game.playerB ? game.blackName : game.whiteName
                }
                setGame(newGame)
                alert("You gave up!")
            }
        } catch (error) {
            console.log("Error:", error)
        }
    }

    function GameStatus() {
        const turn = game.board.turn === "B" ? game.blackName : game.whiteName
        if (!game.winner && game.state !== "Finished") {
            return <p style={{display: 'block', margin: '0', padding: '0'}}>Turn: {turn}</p>
        } else if (!game.winner && game.state === "Finished") {
            return <p style={{display: 'block', margin: '0', padding: '0'}}>Draw!</p>
        } else {
            return <p style={{display: 'block', margin: '0', padding: '0'}}>Winner: {game.winner}</p>
        }

    }

    function GamePlay() {
        return (
            <div>
                <h1>Gomoku</h1>
                <button onClick={giveUp}>Give Up</button>
                <GetOpponent/>
                <DrawBoard/>
                <GetPlayer/>
            </div>
        )
    }

    function DrawBoard() {
        const cellSize = 100 / game.board.size
        console.log("Game Stuff Vewy CUUL:", game)
        console.log("First Cell in board:", game.board.cells[0][0])
        return (
            <center>
                <GameStatus/>
                <Container style={{backgroundColor: 'white', padding: '0'}}>
                    {
                        Array.from(Array(game.board.size).keys()).map((row: number) => (
                            <Row style={{margin: '0', padding: '0', maxWidth: `${cellSize}`, lineHeight: '0'}}>
                                {
                                    game && game.board && Array.from(Array(game.board.size).keys()).map((col: number) => (
                                        <Button
                                            key={col}
                                            style={button_style}
                                            onClick={() => play(row, col)}
                                        >
                                            {game.board.cells[row][col] === "B" && (
                                                <img src={black_stone} style={{width: '100%', height: '100%'}}/>
                                            )}
                                            {game.board.cells[row][col] === "W" && (
                                                <img src={white_stone} style={{width: '100%', height: '100%'}}/>
                                            )}
                                        </Button>
                                    ))
                                }
                            </Row>
                        ))
                    }
                </Container>
            </center>
        )
    }

    function GetOpponent() {
        const user = useContext(contextLogged).loggedInState.id
        if (user == game.playerB) {
            return (
                <p style={{textAlign: "right"}}>White<p
                    style={{display: 'block', margin: '0', padding: '0'}}>{game.whiteName} - {game.win_rate_w}%</p></p>
            )
        } else {
            return (
                <p style={{textAlign: "right"}}>Black<p
                    style={{display: 'block', margin: '0', padding: '0'}}>{game.blackName} - {game.win_rate_b}%</p></p>
            )
        }
    }

    function GetPlayer() {
        const user = useContext(contextLogged).loggedInState.id
        if (user == game.playerB) {
            return (
                <p style={{textAlign: "left"}}>Black<p
                    style={{display: 'block', margin: '0', padding: '0'}}>{game.blackName} - {game.win_rate_b}%</p></p>
            )
        } else {
            return (
                <p style={{textAlign: "left"}}>White<p
                    style={{display: 'block', margin: '0', padding: '0'}}>{game.whiteName} - {game.win_rate_w}%</p></p>
            )
        }
    }

    return (
        <div>
            <NavBar/>
            {!game ?
                <WaitGame/>
                :
                <GamePlay/>
            }
        </div>
    )
}