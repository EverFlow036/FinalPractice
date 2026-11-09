package learn;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class learning extends Application {

    int[][] board = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };

    int[][] startBoard = new int[3][3];
    Button[][] tileButtons = new Button[3][3];

    int movesUsed = 0;
    int maxMoves = 20;
    boolean gameOver = false;

    Label movesLabel = new Label("Moves Used: 0 / 20");
    Label resultLabel = new Label("Start the game!");
    TextField maxField = new TextField("20");

    ArrayList<int[][]> history = new ArrayList<>();
    Random random = new Random();

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("NUMBER SLIDER GAME");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label maxLabel = new Label("Set Maximum Moves:");
        maxField.setPrefWidth(50);

        Button setButton = new Button("SET");
        HBox maxBox = new HBox(8, maxLabel, maxField, setButton);
        maxBox.setAlignment(Pos.CENTER);

        Button resetButton = new Button("RESET");
        Button playButton = new Button("PLAY");
        Button undoButton = new Button("UNDO");

        resetButton.setPrefWidth(90);
        playButton.setPrefWidth(90);
        undoButton.setPrefWidth(90);

        HBox buttonBox = new HBox(20, resetButton, playButton, undoButton);
        buttonBox.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Arrange the following numbers!");

        GridPane boardPane = new GridPane();
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setHgap(10);
        boardPane.setVgap(10);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button tile = new Button();
                tile.setPrefSize(90, 70);

                final int r = row;
                final int c = col;

                tile.setOnAction(e -> moveTile(r, c));

                tileButtons[row][col] = tile;
                boardPane.add(tile, col, row);
            }
        }

        resetButton.setOnAction(e -> resetGame());
        playButton.setOnAction(e -> playGame());
        undoButton.setOnAction(e -> undoMove());
        setButton.setOnAction(e -> setMaximumMoves());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
                titleLabel,
                maxBox,
                buttonBox,
                instructionLabel,
                boardPane,
                movesLabel,
                resultLabel
        );

        copyBoard(board, startBoard);
        updateBoard();

        Scene scene = new Scene(root, 420, 520);
        stage.setTitle("Number Slider Game");
        stage.setScene(scene);
        stage.show();
    }

    public void playGame() {
        setSolvedBoard();

        int lastValue = -1;

        for (int i = 0; i < 25; i++) {
            ArrayList<int[]> possibleMoves = new ArrayList<>();

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (canMove(row, col) && board[row][col] != lastValue) {
                        possibleMoves.add(new int[]{row, col});
                    }
                }
            }

            int[] chosen = possibleMoves.get(random.nextInt(possibleMoves.size()));
            int row = chosen[0];
            int col = chosen[1];

            lastValue = board[row][col];
            swapWithEmpty(row, col);
        }

        copyBoard(board, startBoard);
        history.clear();
        movesUsed = 0;
        gameOver = false;
        resultLabel.setText("Game started!");
        updateBoard();
    }

    public void resetGame() {
        copyBoard(startBoard, board);
        history.clear();
        movesUsed = 0;
        gameOver = false;
        resultLabel.setText("Game reset!");
        updateBoard();
    }

    public void undoMove() {
        if (history.isEmpty()) {
            return;
        }

        int[][] lastBoard = history.remove(history.size() - 1);
        copyBoard(lastBoard, board);

        if (movesUsed > 0) {
            movesUsed--;
        }

        gameOver = false;
        resultLabel.setText("Move undone!");
        updateBoard();
    }

    public void setMaximumMoves() {
        try {
            int value = Integer.parseInt(maxField.getText().trim());

            if (value > 0) {
                maxMoves = value;
                resultLabel.setText("Maximum moves set!");
                updateBoard();
            } else {
                resultLabel.setText("Enter a number above 0.");
            }
        } catch (Exception e) {
            resultLabel.setText("Invalid input.");
        }
    }

    public void moveTile(int row, int col) {
        if (gameOver) {
            return;
        }

        if (canMove(row, col)) {
            int[][] oldBoard = new int[3][3];
            copyBoard(board, oldBoard);
            history.add(oldBoard);

            swapWithEmpty(row, col);
            movesUsed++;

            if (isWinner() && movesUsed <= maxMoves) {
                resultLabel.setText("Winner!");
                gameOver = true;
            } else if (movesUsed >= maxMoves) {
                resultLabel.setText("You FAILED!");
                gameOver = true;
            } else {
                resultLabel.setText("Keep playing!");
            }

            updateBoard();
        }
    }

    public boolean canMove(int row, int col) {
        int emptyRow = 0;
        int emptyCol = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }

        int distance = Math.abs(row - emptyRow) + Math.abs(col - emptyCol);
        return distance == 1;
    }

    public void swapWithEmpty(int row, int col) {
        int emptyRow = 0;
        int emptyCol = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }

        board[emptyRow][emptyCol] = board[row][col];
        board[row][col] = 0;
    }

    public boolean isWinner() {
        int correct = 1;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 2 && col == 2) {
                    return board[row][col] == 0;
                }

                if (board[row][col] != correct) {
                    return false;
                }

                correct++;
            }
        }

        return true;
    }

    public void setSolvedBoard() {
        int num = 1;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 2 && col == 2) {
                    board[row][col] = 0;
                } else {
                    board[row][col] = num;
                    num++;
                }
            }
        }
    }

    public void copyBoard(int[][] source, int[][] destination) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                destination[row][col] = source[row][col];
            }
        }
    }

    public void updateBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    tileButtons[row][col].setText("");
                } else {
                    tileButtons[row][col].setText(String.valueOf(board[row][col]));
                }
            }
        }

        movesLabel.setText("Moves Used: " + movesUsed + " / " + maxMoves);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
