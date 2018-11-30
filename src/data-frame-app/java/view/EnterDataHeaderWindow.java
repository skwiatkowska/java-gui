package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EnterDataHeaderWindow {

    private static String output;
    private static String[] row = null;
    private static String computedTypes;


    public static String show(String title, String text, int numberOfCols) {
        output = "null";
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);


        BorderPane root = new BorderPane();
        VBox topVBox = new VBox();
        HBox hb1 = new HBox();
        HBox bottonHBox = new HBox();
        HBox hb2 = new HBox();

        Label label = new Label(text);
        label.setFont(new Font("System", 13));
        Label emptyLabel = new Label();
        emptyLabel.setPrefWidth(100);
        TextField textField = new TextField();
        textField.setVisible(false);
        textField.setPrefWidth(250);
        textField.setOnAction(e -> {
            stage.close();
        });


        Button okButton = new Button("     OK     ");
        okButton.setVisible(false);
        okButton.setOnAction(e -> {
            output = textField.getText();
            stage.close();
        });

        Button cancelButoon = new Button(" Cancel  ");
        cancelButoon.setVisible(false);
        cancelButoon.setOnAction(e -> {
            output = "null";
            stage.close();
        });

        Button yesButton = new Button("    Yes    ");
        yesButton.setOnAction(e -> {
            output = "headerDefinedInTheFile";
            stage.close();
        });

        Button noButton = new Button("    No     ");
        noButton.setOnAction(e -> {
            textField.setVisible(true);
            okButton.setVisible(true);
            cancelButoon.setVisible(true);
            textField.setPromptText("Enter " + numberOfCols + " columns names separated by a space");
        });

        okButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        cancelButoon.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        yesButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        noButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");

        hb2.getChildren().addAll(yesButton, noButton);
        hb2.setSpacing(30);
        hb1.getChildren().addAll(emptyLabel, hb2);
        topVBox.getChildren().addAll(label, hb1);
        bottonHBox.getChildren().addAll(okButton, cancelButoon);
        bottonHBox.setSpacing(30);
        bottonHBox.setPadding(new Insets(10, -10, 20, 90));
        hb2.setPadding(new Insets(3, 30, 3, -20));
        topVBox.setPadding(new Insets(10, 0, 5, 10));
        textField.setMaxWidth(300);
        root.setTop(topVBox);
        root.setCenter(textField);
        root.setBottom(bottonHBox);

        stage.setScene(new Scene(root, 350, 150));
        stage.showAndWait();

        return output;
    }

}
