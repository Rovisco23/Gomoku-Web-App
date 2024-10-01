package pt.isel.daw.domainmodel.game

import pt.isel.daw.domainmodel.game.CellState.*
import pt.isel.daw.http.model.BoardExternalValues

data class Board(private val cells: Array<Array<CellState>>, private val size: Int, private val turn: Char) {

    fun get(row: Int, col: Int) = cells[row][col]
    fun mutate(currTurn: CellState, nextTurn: Char, row: Int, col: Int): Board {
        val newBoardCells = Array(size) { r -> Array(size) { c -> cells[r][c] } }
        newBoardCells[row][col] = currTurn
        return Board(newBoardCells, size, nextTurn)
    }

    fun getSize() = size
    fun getCurrTurn() = turn
    fun checkIfEmpty(row: Int, col: Int) = get(row, col) == EMPTY

    companion object {

        private val possibleSizes = listOf(15,19)

        fun create(size: Int) =
            Board(Array(size) { Array(size) { EMPTY } }, size, 'B')

        fun fromString(s: String): Board {
            val boardValues = s.split('/')
            val size = boardValues[1].toInt()
            require(boardValues[0].length == size * size)
            val boardState = Array(size) { Array(size) { EMPTY } }
            for (row in 0 until size)
                for (col in 0 until size)
                    boardState[row][col] = CellState.fromChar(s[row * size + col])
            return Board(
                boardState,
                size,
                boardValues[2].first()
            )
        }

        fun checkSize(boardSize: Int) = possibleSizes.contains(boardSize)
    }

    override fun toString(): String =
        cells.flatMap { row -> row.map { it.char } }.joinToString("") + "/$size/$turn"

    fun getNumberOfCells (): Int {
        var totalCells = 0
        cells.forEach { it.forEach { cell-> if (cell != EMPTY) totalCells++ } }
        return totalCells
    }

    fun checkIfWin(row: Int, col: Int): Boolean {
        val adjacentCells = arrayOf(
            Pair(-1, -1),
            Pair(-1, 0),
            Pair(-1, 1),
            Pair(0, -1),
            Pair(0, 1),
            Pair(1, -1),
            Pair(1, 0),
            Pair(1, 1)
        )
        adjacentCells.forEach {
            if (checkCellsInLine(row, col, it.first, it.second, 0, false) == 4) return true
        }
        return false
    }

    fun checkIfDraw() = getNumberOfCells() + 1 == size * size

    private fun checkCellsInLine(row: Int, col: Int, rowInc: Int, colInc: Int, currCount: Int, onRepeat: Boolean): Int {
        var currRow = row + rowInc
        var currCol = col + colInc
        var count = currCount
        while (
            currRow in 0 until size && currCol in 0 until size
            && get(currRow, currCol) == CellState.fromChar(turn)
        ) {
            count++
            currRow += rowInc
            currCol += colInc
        }
        return if (count <= 4 && !onRepeat) checkCellsInLine(row, col, -rowInc, -colInc, count, true)
        else count
    }

    private fun cellsToString() = cells.flatMap { row -> row.map { it.char } }.joinToString("")

    fun toExternal() = BoardExternalValues(
        cells = cellsToString(),
        size = size,
        turn = turn
    )
}
