
export type BoardValues = {
    cells: Array<Array<string>>,
    size: number,
    turn: string,
    numberOfOccupiedCells: number,
}

export type GameValues = {
    id: string | undefined,
    board: BoardValues | undefined,
    state: string | undefined,
    playerW: number | undefined,
    playerB: number| undefined,
    whiteName: string | undefined,
    blackName: string | undefined,
    win_rate_w: number | undefined,
    win_rate_b: number | undefined,
    winner: string | undefined,
}

export type LobbyValues = {
    rule: string,
    size: number
}