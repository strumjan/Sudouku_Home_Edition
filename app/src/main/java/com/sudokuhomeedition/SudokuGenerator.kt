package com.sudokuhomeedition

class SudokuGenerator {
    fun generateSolvedBoard(): Array<IntArray> {
        val board = Array(9) { IntArray(9) }
        solveBoard(board)
        return board
    }

    fun removeCells(board: Array<IntArray>, level: Int): Array<IntArray> {
        val removed = Array(9) { board[it].clone() }
        val clues = when (level) {
            0 -> 40
            1 -> 30
            else -> 24
        }

        val totalCells = 81
        var toRemove = totalCells - clues

        while (toRemove > 0) {
            val row = (0..8).random()
            val col = (0..8).random()
            if (removed[row][col] != 0) {
                removed[row][col] = 0
                toRemove--
            }
        }

        return removed
    }

    private fun solveBoard(board: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solveBoard(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0..8) {
            if (board[row][i] == num || board[i][col] == num) return false
        }
        val boxRow = row / 3 * 3
        val boxCol = col / 3 * 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[boxRow + i][boxCol + j] == num) return false
            }
        }
        return true
    }
}