package pt.isel.daw.domainmodel.game

enum class GameState {
    STARTING,
    ON_GOING,
    FINISHED;

    override fun toString(): String {
        return when {
            this == STARTING -> "Starting"
            this == ON_GOING -> "On Going"
            this == FINISHED -> "Finished"
            else -> throw IllegalArgumentException("Invalid value for GameState")
        }
    }
    companion object {
        fun fromString(s: String) = when (s.lowercase()) {
            "starting" -> STARTING
            "on going" -> ON_GOING
            "finished" -> FINISHED
            else -> throw IllegalArgumentException("Invalid value for GameState")
        }
    }
}