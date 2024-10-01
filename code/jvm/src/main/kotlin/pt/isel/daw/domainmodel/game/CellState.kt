package pt.isel.daw.domainmodel.game

enum class CellState(val char: Char) {
    EMPTY('-'),
    PLAYER_B('B'),
    PLAYER_W('W');

    override fun toString() = if (this == PLAYER_B) "Black Player" else if (this == PLAYER_W) "White Player" else "Empty Cell"

    fun getNextTurn() = if (this == PLAYER_B) 'W' else 'B'

    companion object {
        fun fromChar(c: Char) = when (c) {
            '-' -> EMPTY
            'B' -> PLAYER_B
            'W' -> PLAYER_W
            else -> throw IllegalArgumentException("Invalid value for Board.State")
        }
    }
}