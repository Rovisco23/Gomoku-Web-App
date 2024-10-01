package pt.isel.daw.domainmodel.game

import pt.isel.daw.domainmodel.game.CellState.PLAYER_B
import pt.isel.daw.domainmodel.game.CellState.PLAYER_W
import pt.isel.daw.domainmodel.game.GameState.FINISHED
import pt.isel.daw.domainmodel.game.GameState.ON_GOING
import pt.isel.daw.http.model.GameExternalValues
import pt.isel.daw.repository.GamesRepository
import java.util.*

data class Game(
    val id: UUID,
    val board: Board,
    val state: GameState,
    val openingRule: OpeningRule,
    val playerB: Int,
    val playerW: Int,
    val winner: Int? = null
) {
    fun isFinished() = state == FINISHED

    fun checkWrongTurn(userId: Int) = board.getCurrTurn() == 'B' && playerW == userId ||
            board.getCurrTurn() == 'W' && playerB == userId

    private fun updateGame(gameRep: GamesRepository, currTurn: CellState, nextTurn: Char, row: Int, col: Int): Game {
        val updatedGame = copy(board = this.board.mutate(currTurn, nextTurn, row, col))
        gameRep.update(updatedGame)
        return updatedGame
    }

    fun toExternal() = GameExternalValues(
        id = id.toString(),
        board = board.toExternal(),
        state = state.toString(),
        playerB = playerB,
        playerW = playerW,
        winner = winner
    )

    private fun winGame(gameRep: GamesRepository, currTurn: CellState, winner: Int, row: Int, col: Int): Game {
        val updatedGame = copy(
            board = this.board.mutate(currTurn, currTurn.char, row, col),
            state = FINISHED,
            winner = winner
        )
        gameRep.win(updatedGame, winner)
        return updatedGame
    }

    private fun drawGame(gameRep: GamesRepository, currTurn: CellState, nextTurn: Char, row: Int, col: Int): Game {
        val updatedGame = copy(board = this.board.mutate(currTurn, nextTurn, row, col), state = FINISHED)
        gameRep.draw(updatedGame)
        return updatedGame
    }

    fun giveUp(gameRep: GamesRepository, winner: Int): Game {
        val updatedGame = copy(state= FINISHED, winner = winner)
        gameRep.win(updatedGame, winner)
        return updatedGame
    }

    fun checkMove(gameRep: GamesRepository, row: Int, col: Char, turn: CellState): Pair<Char, Boolean> {
        if (turn == PLAYER_W) return Pair(turn.getNextTurn(), true)
        val gameMoveCount = board.getNumberOfCells()
        val size = board.getSize()
        return when (gameMoveCount) {
            0 -> if (checkCenter(size, row, col)) Pair(turn.getNextTurn(), true) else Pair(turn.getNextTurn(), false)
            else -> {
                if (openingRule == OpeningRule.LONG_PRO)
                     if(size == 19 && !(row in 7..13 && col in 'g'..'m') ||
                             size == 15 && !(row in 5..11 && col in 'e'..'k')){
                         gameRep.updateGameState(id, ON_GOING)
                         Pair(turn.getNextTurn(), true)
                     }
                     else Pair(turn.getNextTurn(), false)
                else
                    if(size == 19 && !(row in 8..12 && col in 'h'..'l') ||
                            size == 15 && !(row in 6..10 && col in 'f'..'j')){
                        gameRep.updateGameState(id, ON_GOING)
                        Pair(turn.getNextTurn(), true)
                    }
                    else Pair(turn.getNextTurn(), false)
            }
        }
    }

    private fun checkCenter(size: Int, row: Int, col: Char) =
            size == 15 && row == 8 && col == 'h' || size == 19 && row == 10 && col == 'j'

    fun processTurn(
        gameRep: GamesRepository,
        row: Int,
        col: Int,
        userId: Int,
        currTurn: CellState,
        nextTurn: Char
    ): Game {
        return if (board.checkIfWin(row, col)) winGame(gameRep, currTurn, userId, row, col)
        else if (board.checkIfDraw()) drawGame(gameRep, currTurn, nextTurn, row, col)
        else updateGame(gameRep, currTurn, nextTurn, row, col)
    }

    fun isPlayableTurn(userId: Int) =
        board.getCurrTurn() == 'B' && playerB == userId || board.getCurrTurn() == 'W' && playerW == userId

    fun getTurnCredentials(userId: Int): CellState = if (playerB == userId) PLAYER_B else PLAYER_W

}


