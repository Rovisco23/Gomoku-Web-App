package pt.isel.daw.http.model

import pt.isel.daw.domainmodel.game.*
import java.util.*

data class GamePlayInputModel(val userId : Int, val row: Int, val col: Char)

data class GameStartInputModel(val userId: Int, val rule: String, val boardSize: Int)

data class GetGameModel(
    val id: UUID,
    val board: BoardExternalValues,
    val state: String,
    val rule: String,
    val playerB: Int,
    val blackName: String,
    val win_rate_b: Double,
    val playerW: Int,
    val whiteName: String,
    val win_rate_w: Double,
    val winner: Int?
) {
    fun toGame() = Game(
        id,
        board.toBoard(),
        GameState.fromString(state),
        OpeningRule.fromString(rule),
        playerB,
        playerW,
        winner
    )
}

data class BoardExternalValues(val cells: String, val size: Int, val turn: Char) {
    fun toBoard() = Board(cells.toCells(size), size, turn)
}

fun String.toCells(size: Int): Array<Array<CellState>> {
    val boardState = Array(size) { Array(size) { CellState.EMPTY } }
    for (row in 0 until size)
        for (col in 0 until size)
            boardState[row][col] = CellState.fromChar(this[row * size + col])
    return boardState
}

data class GameExternalValues(
    val id: String,
    val board: BoardExternalValues,
    val state: String,
    val playerW: Int,
    val playerB: Int,
    val winner: Int?
)
