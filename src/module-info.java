class HistorySnapshot {
    constructor(matrix, rPos, cPos) {
        this.matrix = matrix.map(row => [...row]);
        this.rPos = rPos;
        this.cPos = cPos;
    }
}

class GridModel {
    constructor() {
        this.cells = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
        this.zeroR = 2;
        this.zeroC = 2;
    }

    defaultState() {
        this.cells = [[1, 2, 3], [4, 5, 6], [7, 8, 0]];
        this.zeroR = 2;
        this.zeroC = 2;
    }

    scrambleGrid() {
        let sequence = [1, 2, 3, 4, 5, 6, 7, 8, 0];
        do {
            sequence.sort(() => Math.random() - 0.5);
        } while (!this.validateInversions(sequence));

        for (let r = 0; r < 3; r++) {
            for (let c = 0; c < 3; c++) {
                this.cells[r][c] = sequence[r * 3 + c];
                if (this.cells[r][c] === 0) {
                    this.zeroR = r;
                    this.zeroC = c;
                }
            }
        }
    }

    validateInversions(arr) {
        let total = 0;
        let items = arr.filter(v => v !== 0);
        for (let i = 0; i < items.length; i++) {
            for (let j = i + 1; j < items.length; j++) {
                if (items[i] > items[j]) total++;
            }
        }
        return total % 2 === 0;
    }

    swapTile(r, c) {
        if (Math.abs(r - this.zeroR) + Math.abs(c - this.zeroC) !== 1) return false;
        this.cells[this.zeroR][this.zeroC] = this.cells[r][c];
        this.cells[r][c] = 0;
        this.zeroR = r;
        this.zeroC = c;
        return true;
    }

    isCompleted() {
        let goal = [1, 2, 3, 4, 5, 6, 7, 8, 0];
        let idx = 0;
        for (let r = 0; r < 3; r++) {
            for (let c = 0; c < 3; c++) {
                if (this.cells[r][c] !== goal[idx++]) return false;
            }
        }
        return true;
    }
}

class GameManager {
    constructor() {
        this.board = new GridModel();
        this.moveCount = 0;
        this.allowedMoves = 20;
        this.historyLog = [];
        this.gameOver = false;
        this.outcomeMessage = "";
    }

    setMaxMoves(max) {
        this.allowedMoves = max;
    }

    play() {
        this.board.scrambleGrid();
        this.moveCount = 0;
        this.historyLog = [];
        this.gameOver = false;
        this.outcomeMessage = "";
    }

    reset() {
        this.board.defaultState();
        this.moveCount = 0;
        this.historyLog = [];
        this.gameOver = false;
        this.outcomeMessage = "";
    }

    makeMove(r, c) {
        if (this.gameOver) return;
        let snap = new HistorySnapshot(this.board.cells, this.board.zeroR, this.board.zeroC);
        if (this.board.swapTile(r, c)) {
            this.historyLog.push(snap);
            this.moveCount++;
            this.evaluateGame();
        }
    }

    undoMove() {
        if (this.gameOver || this.historyLog.length === 0) return;
        let previous = this.historyLog.pop();
        this.board.cells = previous.matrix.map(row => [...row]);
        this.board.zeroR = previous.rPos;
        this.board.zeroC = previous.cPos;
        this.moveCount--;
    }

    evaluateGame() {
        if (this.board.isCompleted() && this.moveCount < this.allowedMoves) {
            this.outcomeMessage = "Congrats, WINNER!!!";
            this.gameOver = true;
        } else if (this.moveCount >= this.allowedMoves) {
            this.outcomeMessage = "You FAILED!";
            this.gameOver = true;
        }
    }
}
