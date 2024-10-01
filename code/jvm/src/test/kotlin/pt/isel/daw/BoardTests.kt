package pt.isel.daw

import org.junit.jupiter.api.Test
import pt.isel.daw.domainmodel.game.Board

class BoardTests {
    private val board = Board.fromString(
        "-------------------------------------------------------------------------------------------------------" +
                "-------BBBBB------------BB-BB---------------------------------------------------------------------" +
                "------------------------/15/B")

    @Test
    fun checkIfEmptyTest() {
        assert(board.checkIfEmpty(0, 0))
        assert(board.checkIfEmpty(6, 6))
    }

    @Test
    fun checkIfDrawTest() {
        val drawPlayBoard = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" +
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" +
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB-/15/W"
        val board = Board.fromString(drawPlayBoard)
        assert(board.checkIfDraw())
    }

    @Test
    fun checkIfWinTest() {
        assert(board.checkIfWin(8,9))
        assert(!board.checkIfWin(7,10))
    }
}