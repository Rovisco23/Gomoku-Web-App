package pt.isel.daw.domainmodel.game

data class InvalidMoves(
    val nextTurn: Char,
    val invalidMovesString: String
) {
    val invalidMoves: Pair<Char, List<Pair<Int, Char>>>
        get() {
            val list = invalidMovesString.split(",")
            val invalidMovesList = mutableListOf<Pair<Int, Char>>()
            for (i in list.indices) {
                val pos = list[i].split(":")
                invalidMovesList.add(Pair(pos[0].toInt(), pos[1].single()))
            }
            return Pair(
                nextTurn,
                invalidMovesList as List<Pair<Int, Char>>
            )
        }
}