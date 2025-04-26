package com.sudokuhomeedition

class JigsawSudokuGame(val difficulty: SudokuGame.Difficulty) : SudokuGameLogic {

    private val board = Array(9) { IntArray(9) }
    private val solution = Array(9) { IntArray(9) }
    private val fixed = Array(9) { BooleanArray(9) }

    // ✅ Фиксна, тестирана regionMap
    val regionMap = generateJigsawRegionMap()


    init {
        validateRegionMap()
        generateFullBoard()
        copyBoard(board, solution)
        removeCells(difficulty.clues)
    }

    private fun generateJigsawRegionMap(): Array<IntArray> {
        val map = Array(9) { IntArray(9) { -1 } } // -1 значи непополнета
        val directions = listOf(Pair(0,1), Pair(1,0), Pair(0,-1), Pair(-1,0)) // десно, долу, лево, горе

        var currentRegion = 0

        while (currentRegion < 9) {
            val cells = mutableListOf<Pair<Int, Int>>()
            val freeCells = mutableListOf<Pair<Int, Int>>()

            // најди прва слободна точка
            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    if (map[i][j] == -1) freeCells.add(i to j)
                }
            }

            freeCells.shuffle()
            val start = freeCells.first()
            map[start.first][start.second] = currentRegion
            cells.add(start)

            while (cells.size < 9) {
                val expandable = cells.filter { (r, c) ->
                    directions.any { (dr, dc) ->
                        val nr = r + dr
                        val nc = c + dc
                        nr in 0..8 && nc in 0..8 && map[nr][nc] == -1
                    }
                }
                if (expandable.isEmpty()) {
                    // нема простор - невалидна мапа, ресет
                    return generateJigsawRegionMap()
                }
                val (r, c) = expandable.random()
                val (dr, dc) = directions.shuffled().firstOrNull { (dr, dc) ->
                    val nr = r + dr
                    val nc = c + dc
                    nr in 0..8 && nc in 0..8 && map[nr][nc] == -1
                } ?: continue
                val nr = r + dr
                val nc = c + dc
                map[nr][nc] = currentRegion
                cells.add(nr to nc)
            }

            currentRegion++
        }

        return map
    }


    private fun validateRegionMap() {
        val counts = IntArray(9)
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                val id = regionMap[i][j]
                if (id in 0..8) counts[id]++
            }
        }
        counts.forEachIndexed { index, count ->
            if (count != 9) {
                throw IllegalStateException("Region $index has $count cells!")
            }
        }
    }
    // ✅ Генерира табла користејќи класична логика (за сигурност)
    private fun generateFullBoard() {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 9) for (j in 0 until 9) empty.add(i to j)
        solveClassic(board, empty, 0)
    }

    // ✅ Класично решавање (3x3 box логика)
    private fun solveClassic(grid: Array<IntArray>, empty: List<Pair<Int, Int>>, index: Int): Boolean {
        if (index == empty.size) return true
        val (row, col) = empty[index]
        for (num in (1..9).shuffled()) {
            if (isValidClassic(grid, row, col, num)) {
                grid[row][col] = num
                if (solveClassic(grid, empty, index + 1)) return true
                grid[row][col] = 0
            }
        }
        return false
    }

    private fun isValidClassic(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until 9) {
            if (grid[row][i] == num || grid[i][col] == num) return false
        }
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (i in 0 until 3) for (j in 0 until 3) {
            if (grid[boxRow + i][boxCol + j] == num) return false
        }
        return true
    }

    // ✅ Проверка со Jigsaw логика (користи regionMap)
    private fun isValidJigsaw(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until 9) {
            if (grid[row][i] == num && i != col) return false
            if (grid[i][col] == num && i != row) return false
        }
        val region = regionMap[row][col]
        for (i in 0 until 9) for (j in 0 until 9) {
            if ((i != row || j != col) && regionMap[i][j] == region && grid[i][j] == num) return false
        }
        return true
    }

    private fun removeCells(cluesToLeave: Int) {
        for (i in 0 until 9) for (j in 0 until 9) fixed[i][j] = true

        val cells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 9) for (j in 0 until 9) cells.add(i to j)
        cells.shuffle()

        var removed = 0
        while (cells.isNotEmpty() && (81 - removed) > cluesToLeave) {
            val (r, c) = cells.removeAt(0)
            board[r][c] = 0
            fixed[r][c] = false
            removed++
        }
    }

    private fun copyBoard(from: Array<IntArray>, to: Array<IntArray>) {
        for (i in 0 until 9) for (j in 0 until 9) to[i][j] = from[i][j]
    }

    override fun getCell(row: Int, col: Int): Int = board[row][col]

    override fun setCell(row: Int, col: Int, value: Int) {
        if (!fixed[row][col]) {
            board[row][col] = value
        }
    }

    override fun isCellEditable(row: Int, col: Int): Boolean = !fixed[row][col]

    override fun isCellCorrect(row: Int, col: Int): Boolean {
        val value = board[row][col]
        return value != 0 && value == solution[row][col]
    }

    override fun isSolved(): Boolean {
        for (i in 0 until 9) for (j in 0 until 9) {
            if (!isCellCorrect(i, j)) return false
        }
        return true
    }

    // ✅ За визуелна проверка или боја
    fun isMoveValid(row: Int, col: Int, number: Int): Boolean {
        return isValidJigsaw(board, row, col, number)

    }

    override fun getRegionId(row: Int, col: Int): Int {
        return regionMap[row][col]
    }

}
