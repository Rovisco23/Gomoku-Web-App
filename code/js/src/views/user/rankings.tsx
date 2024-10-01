import React, {useState} from "react";
import {useFetch} from "../../utils/useFetch";
import {NavBar, BottomBar} from "../../utils/NavBar";
import {RankingCard} from "../utils";

export function Rankings() {
    const [playerName, setPlayerName] = useState("");
    const [playerLimit, setPlayerLimit] = useState("");
    const [reload, setReload] = useState(false)
    const [rsp, setRsp] = useState(null)

    const handleNameChange = (event) => {
        setPlayerName(event.target.value);
    };

    const handleLimitChange = (event) => {
        setPlayerLimit(event.target.value);
    }

    const handleReload = () => {
        setReload(true)
    }

    function Fetch() {
        const newRsp = useFetch("api/rankings", "GET", {}, null).response
        if (!newRsp) {
            return (
                <div>
                    Loading
                </div>
            )
        }
        setReload(false)
        setRsp(newRsp)
    }

    const filteredRankings = !rsp ? [] : rsp.properties.filter(
        (ranking) => {
            if (playerName.trim() === '') {
                return true;
            } else {
                return ranking.username.toLowerCase().includes(playerName.toLowerCase());
            }
        }
    ).slice(0, playerLimit === '' ? 10 : parseInt(playerLimit));

    return (
        <div>
            <NavBar/>
            <h1>Rankings:</h1>
            <p>Search for a Player:
                <input type="text" placeholder="Enter player name" value={playerName} onChange={handleNameChange}/>
                Player Limit:
                <input type="text" placeholder="Enter player limit" value={playerLimit} onChange={handleLimitChange}/>
            </p>
            <button onClick={handleReload}>Reload Rankings</button>
            <div>
                {!reload ?
                    <div>
                        {!rsp ?
                            <div>
                                <Fetch/>
                            </div>
                            :
                            <div>
                                {filteredRankings.length === 0 ? (
                                    <p>No results found for {playerName}</p>
                                ) : (
                                    filteredRankings.map((ranking, index) => (
                                        <RankingCard
                                            key={index}
                                            username={ranking.username}
                                            played={ranking.games_played}
                                            won={ranking.games_won}
                                            drawn={ranking.games_drawn}
                                            winRate={ranking.win_rate}
                                        />
                                    ))
                                )}
                            </div>
                        }
                    </div>
                    :
                    <div>
                        <Fetch/>
                    </div>
                }</div>
            <BottomBar/>
        </div>
    )
}