package pt.isel.daw.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
