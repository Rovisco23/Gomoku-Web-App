package pt.isel.daw.domainmodel.game

enum class OpeningRule {
    PRO, LONG_PRO, FREE_STYLE;

    override fun toString(): String {
        return when {
            this == PRO -> "Pro"
            this == LONG_PRO -> "Long Pro"
            this == FREE_STYLE -> "Free Style"
            else -> throw IllegalArgumentException("Invalid value for OpeningRule")
        }
    }

    companion object {

        private val possibleRules = listOf(PRO, LONG_PRO, FREE_STYLE)

        fun fromString(s: String) = when (s) {
            "Pro" -> PRO
            "Long Pro" -> LONG_PRO
            "Free Style" -> FREE_STYLE
            else -> throw IllegalArgumentException("Invalid value for OpeningRule")
        }

        fun checkRule(openingRule: OpeningRule) = possibleRules.any { it.toString() == openingRule.toString() }
    }
}
