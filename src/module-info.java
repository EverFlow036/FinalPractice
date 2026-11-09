import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SliderModel {
    int[][] grid = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    int r0 = 2, c0 = 2;

    void reset() {
        int[][] def = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        for(int i=0; i<3; i++) System.arraycopy(def[i], 0, grid[i], 0, 3);
        r0 = 2; c0 = 2;
    }

    boolean shift(int r, int c) {
        if(Math.abs(r - r0) + Math.abs(c - c0) == 1) {
            grid[r0][c0] = grid[r][c];
            grid[r][c] = 0;
            r0 = r;
            c0 = c;
            return true;
        }
        return false;
    }

    boolean checkWin() {
        int t = 1;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(i == 2 && j == 2) return grid[i][j] == 0;
                if(grid[i][j] != t++) return false;
            }
        }
        return true;
    }
}

public class NumberSliderAppV2 extends Application {
    private SliderModel model = new SliderModel();
    private int stepCount = 0;
    private int maxAllowed = 20;
    private boolean finished = false;
    private List<int[][]> stack = new ArrayList<>();
    private Button[][] buttons = new Button[3][3];
    private Label lblStatus = new Label("Ready");
    private Label lblMoves = new Label("Moves: 0 / 20");
    private TextField txtMax = new TextField("20");

    @Override
    public void start(Stage stage) {
        stage.setTitle("Slider Game V2");
        HBox topBox = new HBox(8);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        Button btnPlay = new Button("Play");
        Button btnReset = new Button("Reset");
        Button btnUndo = new Button("Undo");
        Button btnSet = new Button("Set");
        txtMax.setPrefWidth(45);
        topBox.getChildren().addAll(btnPlay, btnReset, btnUndo, txtMax, btnSet);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(4);
        gridPane.setVgap(4);
        for(int r=0; r<3; r++) {
            for(int c=0; c<3; c++) {
                Button btn = new Button();
                btn.setPrefSize(70, 70);
                final int row = r;
                final int col = c;
                btn.setOnAction(e -> clickTile(row, col));
                buttons[r][c] = btn;
                gridPane.add(btn, c, r);
            }
        }

        VBox bottomBox = new VBox(5);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(lblStatus, lblMoves);

        btnPlay.setOnAction(e -> {
            List<Integer> list = new ArrayList<>();
            for(int i=0; i<=8; i++) list.add(i);
            do {
                Collections.shuffle(list);
            } while(!validShuffle(list) || isOrdered(list));
            int idx = 0;
            for(int i=0; i<3; i++) {
                for(int j=0; j<3; j++) {
                    model.grid[i][j] = list.get(idx++);
                    if(model.grid[i][j] == 0) { model.r0 = i; model.c0 = j; }
                }
            }
            stepCount = 0;
            stack.clear();
            finished = false;
            lblStatus.setText("Playing");
            updateView();
        });

        btnReset.setOnAction(e -> {
            model.reset();
            stepCount = 0;
            stack.clear();
            finished = false;
            lblStatus.setText("Reset");
            updateView();
        });

        btnUndo.setOnAction(e -> {
            if(finished || stack.isEmpty()) return;
            int[][] prev = stack.remove(stack.size() - 1);
            for(int i=0; i<3; i++) System.arraycopy(prev[i], 0, model.grid[i], 0, 3);
            for(int i=0; i<3; i++) {
                for(int j=0; j<3; j++) {
                    if(model.grid[i][j] == 0) { model.r0 = i; model.c0 = j; }
                }
            }
            stepCount--;
            lblStatus.setText("Undone");
            updateView();
        });

        btnSet.setOnAction(e -> {
            try {
                int v = Integer.parseInt(txtMax.getText().trim());
                if(v > 0) { maxAllowed = v; updateView(); }
            } catch(Exception ignored) {}
        });

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(gridPane);
        root.setBottom(bottomBox);
        updateView();
        stage.setScene(new Scene(root, 320, 390));
        stage.show();
    }

    private void clickTile(int r, int c) {
        if(finished) return;
        int[][] snap = new int[3][3];
        for(int i=0; i<3; i++) System.arraycopy(model.grid[i], 0, snap[i], 0, 3);
        if(model.shift(r, c)) {
            stack.add(snap);
            stepCount++;
            if(model.checkWin() && stepCount < maxAllowed) {
                lblStatus.setText("Congrats, WINNER!!!");
                finished = true;
            } else if(stepCount >= maxAllowed) {
                lblStatus.setText("You FAILED!");
                finished = true;
            }
            updateView();
        }
    }

    private boolean validShuffle(List<Integer> list) {
        int inv = 0;
        List<Integer> sub = new ArrayList<>(list);
        sub.remove(Integer.valueOf(0));
        for(int i=0; i<sub.size(); i++) {
            for(int j=i+1; j<sub.size(); j++) {
                if(sub.get(i) > sub.get(j)) inv++;
            }
        }
        return inv % 2 == 0;
    }

    private boolean isOrdered(List<Integer> list) {
        return list.equals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 0));
    }

    private void updateView() {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(model.grid[i][j] == 0) buttons[i][j].setText("");
                else buttons[i][j].setText(String.valueOf(model.grid[i][j]));
            }
        }
        lblMoves.setText("Moves: " + stepCount + " / " + maxAllowed);
    }
}
            this.gameOver = true;
        }
    }
}
