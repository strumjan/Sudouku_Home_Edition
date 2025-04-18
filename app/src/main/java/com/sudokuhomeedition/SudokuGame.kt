package com.sudokuhomeedition

class SudokuGame(difficulty: Difficulty) {

    enum class Difficulty(val clues: Int) {
        EASY(40),
        MEDIUM(30),
        HARD(20)
    }

    val board = Array(9) { IntArray(9) }
    private val solution = Array(9) { IntArray(9) }
    private val fixed = Array(9) { BooleanArray(9) }

    init {
        generateFullBoard()
        copyBoard(board, solution)
        removeCells(difficulty.clues)
    }

    private fun copyBoard(from: Array<IntArray>, to: Array<IntArray>) {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                to[i][j] = from[i][j]
            }
        }
    }

    // Дали полето е фиксно (не може да се менува)
    fun isCellEditable(row: Int, col: Int): Boolean {
        return !fixed[row][col]
    }

    fun setCell(row: Int, col: Int, value: Int) {
        if (!fixed[row][col]) {
            board[row][col] = value
        }
    }

    fun getCell(row: Int, col: Int): Int {
        return board[row][col]
    }

    fun isSolved(): Boolean {
        for (i in 0 until 9) {
            val row = mutableSetOf<Int>()
            val col = mutableSetOf<Int>()
            val box = mutableSetOf<Int>()
            for (j in 0 until 9) {
                val rVal = board[i][j]
                val cVal = board[j][i]
                val bVal = board[(i / 3) * 3 + j / 3][(i % 3) * 3 + j % 3]

                if (rVal == 0 || !row.add(rVal)) return false
                if (cVal == 0 || !col.add(cVal)) return false
                if (bVal == 0 || !box.add(bVal)) return false
            }
        }
        return true
    }

    fun isMoveValid(row: Int, col: Int, number: Int): Boolean {
        // Проверка во редот и колоната
        for (i in 0 until 9) {
            if (board[row][i] == number && i != col) return false
            if (board[i][col] == number && i != row) return false
        }

        // Проверка во 3x3 кутијата
        val boxRowStart = row / 3 * 3
        val boxColStart = col / 3 * 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val r = boxRowStart + i
                val c = boxColStart + j
                if (board[r][c] == number && (r != row || c != col)) return false
            }
        }

        return true
    }

    fun isCellCorrect(row: Int, col: Int): Boolean {
        //return board[row][col] == solution[row][col]
        val userValue = board[row][col]
        val correctValue = solution[row][col]
        return userValue != 0 && userValue == correctValue
    }


    private fun generateFullBoard() {
        solve(board)
    }

    private fun removeCells(cluesToLeave: Int) {
        // Означи ги сите како фиксни
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
            val backup = board[row][col]
            board[row][col] = 0

            if (!hasUniqueSolution()) {
                board[row][col] = backup
            } else {
                fixed[row][col] = false
                removed++
            }
        }
    }

    // Генератор + решавач
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
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (grid[boxRow + i][boxCol + j] == num) return false
            }
        }
        return true
    }

    private fun hasUniqueSolution(): Boolean {
        var count = 0

        fun countSolutions(grid: Array<IntArray>): Boolean {
            for (row in 0 until 9) {
                for (col in 0 until 9) {
                    if (grid[row][col] == 0) {
                        for (num in 1..9) {
                            if (isValid(grid, row, col, num)) {
                                grid[row][col] = num
                                if (countSolutions(grid)) return true
                                grid[row][col] = 0
                            }
                        }
                        return false
                    }
                }
            }
            count++
            return count > 1
        }

        val tempBoard = Array(9) { board[it].clone() }
        countSolutions(tempBoard)
        return count == 1
    }
}
