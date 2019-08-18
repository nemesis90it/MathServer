//package com.nemesis.mathcore.expressionsolver.ui;
//
//import com.nemesis.mathcore.expressionsolver.ExpressionSolver;
//import com.nemesis.mathcore.utils.MathUtils;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.lang.reflect.TypeVariable;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
//
///**
// * Created by Seby on 18/02/2017.
// */
//
//public class AppScene {
//
//    private static final int SMALL_SCENE_WIDTH = 600;
//    private static final int SMALL_SCENE_HEIGHT = 800;
//
//    private static final Insets BUTTON_PADDING = new Insets(10, 10, 10, 10);
//    private static final int ROWS = 5;
//    private static final int COLUMNS = 5;
//    private static final int BUTTON_SIZE = SMALL_SCENE_WIDTH / COLUMNS - 20;
//
//    static Stage stage;
//    private static TextField inputBox = new TextField();
//    private static TextField outputBox = new TextField();
//
//
//    static {
//        inputBox.setPadding(new Insets(5, 5, 5, 5));
//        outputBox.setEditable(false);
//        outputBox.setPadding(new Insets(5, 5, 5, 5));
//    }
//
//    public static void start() {
//
//        BorderPane root = new BorderPane();
//        Scene scene = new Scene(root, SMALL_SCENE_WIDTH, SMALL_SCENE_HEIGHT);
//
//        GridPane topGrid = buildTopGrid();
//        root.setTop(topGrid);
//
//        GridPane functionsGrid = buildFunctionsGrid();
//        root.setCenter(functionsGrid);
//
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    private static GridPane buildTopGrid() {
//
//        GridPane topGrid = new GridPane();
//        topGrid.setVgap(5);
//        topGrid.setHgap(10);
//        topGrid.setPadding(new Insets(10, 10, 10, 10));
//        ColumnConstraints topColumn1 = new ColumnConstraints((SMALL_SCENE_WIDTH / 6) - 20);
//        ColumnConstraints topColumn2 = new ColumnConstraints((SMALL_SCENE_WIDTH / 6) * 5 - 20);
//        topGrid.getColumnConstraints().addAll(topColumn1, topColumn2);
//
//
//        topGrid.add(new Label(" Input"), 0, 0);
//        topGrid.add(inputBox, 1, 0);
//        topGrid.add(new Label(" Result"), 0, 1);
//        topGrid.add(outputBox, 1, 1);
//
//        return topGrid;
//    }
//
//    private static GridPane buildFunctionsGrid() {
//
//        GridPane functionsGrid = new GridPane();
//        functionsGrid.setVgap(5);
//        functionsGrid.setHgap(10);
//        functionsGrid.setPadding(new Insets(5, 5, 5, 5));
//
//        List<ColumnConstraints> columnConstraints = new ArrayList<>();
//
//        for (int i = 0; i < COLUMNS; i++) {
//            columnConstraints.add(new ColumnConstraints(BUTTON_SIZE));
//        }
//
//        functionsGrid.getColumnConstraints().addAll(columnConstraints);
//
//        VBox[][] buttonMatrix = getButtonMatrix();
//        for (int r = 0; r < ROWS; r++) {
//            for (int c = 0; c < COLUMNS; c++) {
//                VBox vBox = buttonMatrix[r][c];
//                if (vBox != null) {
//                    functionsGrid.add(vBox, r, c);
//                }
//            }
//        }
//        return functionsGrid;
//    }
//
//
//    private static VBox[][] getButtonMatrix() {
//
//        VBox[][] buttonMatrix = new VBox[ROWS][COLUMNS];
//
//        EventHandler<ActionEvent> clear = event -> {
//            inputBox.clear();
//            outputBox.clear();
//        };
//
//        EventHandler<ActionEvent> evaluate = event -> {
//            outputBox.setText(String.valueOf(ExpressionSolver.evaluate(inputBox.getText())));
//        };
//
//        buttonMatrix[0][0] = buildVBoxButton("CLEAR", clear);
//        buttonMatrix[1][0] = buildVBoxButton("=", evaluate);
//
//        buttonMatrix[2][0] = buildVBoxButton("x!", getActionEventHandler(MathUtils::factorial));
//        buttonMatrix[3][0] = buildVBoxButton("e^x", getActionEventHandler(MathUtils::exponential));
//        buttonMatrix[4][0] = buildVBoxButton("ln(x)", getActionEventHandler(MathUtils::ln));
//        buttonMatrix[0][1] = buildVBoxButton("sin(x)", getActionEventHandler(MathUtils::sin));
//        buttonMatrix[1][1] = buildVBoxButton("cos(x)", getActionEventHandler(MathUtils::cos));
//        buttonMatrix[2][1] = buildVBoxButton("tan(x)", getActionEventHandler(MathUtils::tan));
//
//        return buttonMatrix;
//    }
//
//
//    private static EventHandler<ActionEvent> getActionEventHandler(Function<BigDecimal, BigDecimal> function) {
//        return event -> outputBox.setText(String.valueOf(function.apply(new BigDecimal(inputBox.getText()))));
//    }
//
//
//    private static VBox buildVBoxButton(String label, EventHandler<ActionEvent> eventHandler) {
//
//        Button button = new Button(label);
//        button.setMinWidth(BUTTON_SIZE);
//        button.setPadding(BUTTON_PADDING);
//        button.setOnAction(eventHandler);
//
//        VBox buttonBox = new VBox(5);
//        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
//        buttonBox.getChildren().add(button);
//        buttonBox.setPadding(new Insets(10, 10, 10, 10));
//        return buttonBox;
//    }
//
//}
