package com.sudokuhomeedition

class SudokuGame(val difficulty: Difficulty) : SudokuGameLogic {

    enum class Difficulty(val clues: Int) {
        EASY(40),
        MEDIUM(30),
        HARD(20)
    }

    private val board = Array(9) { IntArray(9) }
    private val solution = Array(9) { IntArray(9) }
    private val fixed = Array(9) { BooleanArray(9) }

    init {
        generatePuzzle()
    }

    private fun generatePuzzle() {
        generateFullBoard()
        copyBoard(board, solution)
        removeCells(difficulty.clues)
    }

    private fun generateFullBoard() {
        solve(board)
    }

    private fun copyBoard(from: Array<IntArray>, to: Array<IntArray>) {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                to[i][j] = from[i][j]
            }
        }
    }

    private fun solve(grid: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (grid[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num
                            if (solve(grid)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until 9) {
            if (grid[row][i] == num || grid[i][col] == num) return false
        }
        val boxRow = row / 3 * 3
        val boxCol = col / 3 * 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (grid[boxRow + i][boxCol + j] == num) return false
            }
        }
        return true
    }

    private fun removeCells(cluesToLeave: Int) {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                fixed[i][j] = true
            }
        }

        val cells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                cells.add(Pair(i, j))
            }
        }
        cells.shuffle()

        var removed = 0
        while (cells.isNotEmpty() && (81 - removed) > cluesToLeave) {
            val (row, col) = cells.removeAt(0)
            board[row][col] = 0
            fixed[row][col] = false
            removed++
        }
    }

    override fun getCell(row: Int, col: Int) = board[row][col]

    override fun setCell(row: Int, col: Int, value: Int) {
        if (!fixed[row][col]) {
            board[row][col] = value
        }
    }

    override fun isCellEditable(row: Int, col: Int): Boolean {
        return !fixed[row][col]
    }

    override fun isCellCorrect(row: Int, col: Int): Boolean {
        val value = board[row][col]
        return value != 0 && value == solution[row][col]
    }

    override fun isSolved(): Boolean {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                if (!isCellCorrect(i, j)) return false
            }
        }
        return true
    }
    override fun getRegionId(row: Int, col: Int): Int {
        return (row / 3) * 3 + (col / 3)
    }

}
