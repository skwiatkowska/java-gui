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

import java.io.*;

public class EnterOrComputeDataTypesWindow {

    private static String output;
    private static String[] row = null;
    private static String computedTypes;


    private static void tryToComputeTypesFromRow(String[] row) {
        String[] computedTypesArray = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            if (row[i].contains("."))
                computedTypesArray[i] = "Double";
            else if (row[i].contains("-"))
                computedTypesArray[i] = "DateTime";
            else if (row[i].matches("[0-9]+"))
                computedTypesArray[i] = "Double";
            else
                computedTypesArray[i] = "String";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : computedTypesArray) {
            stringBuilder.append(s);
            stringBuilder.append(" ");
        }
        computedTypes = stringBuilder.toString();
    }


    private static void getRowFromFileAndComputeTypes(File file, String separator) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file.getAbsolutePath()));

            for (int i = 0; i < 2; i++) {
                String line = br.readLine();
                row = line.split(separator);
            }
            tryToComputeTypesFromRow(row);
        } catch (FileNotFoundException var19) {
            System.out.println("Wrong file path or there is not such file.");
        } catch (IOException var20) {
            var20.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException var18) {
                    var18.printStackTrace();
                }
            }

        }
    }


    public static String show(String title, String text, File file) {
        getRowFromFileAndComputeTypes(file, ",");
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
        label.setFont(new Font("System", 15));
        Label emptyLabel = new Label();
        emptyLabel.setPrefWidth(100);
        TextField textField = new TextField();
        textField.setPrefWidth(50);
        textField.setOnAction(e -> {
            stage.close();
        });


        Button okButton = new Button("     OK     ");
        okButton.setOnAction(e -> {
            output = textField.getText();
            stage.close();
        });

        Button cancelButoon = new Button(" Cancel ");
        cancelButoon.setOnAction(e -> {
            output = "null";
            stage.close();
        });

        Button computeButton = new Button("Compute");
        computeButton.setOnAction(e -> {
            textField.setText(computedTypes);
        });

        Button clearButton = new Button("  Clear  ");
        clearButton.setOnAction(e -> {
            textField.setText("");
        });

        okButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        cancelButoon.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        computeButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");
        clearButton.setStyle("-fx-background-color: #3C3C3C; -fx-text-fill: #C4C4C4");

        hb2.getChildren().addAll(computeButton, clearButton);
        hb2.setSpacing(30);
        hb1.getChildren().addAll(emptyLabel, hb2);
        topVBox.getChildren().addAll(label, hb1);
        bottonHBox.getChildren().addAll(okButton, cancelButoon);
        bottonHBox.setSpacing(30);
        bottonHBox.setPadding(new Insets(10, -10, 20, 90));
        hb2.setPadding(new Insets(3, 30, 3, -40));
        topVBox.setPadding(new Insets(10, 0, 5, 30));
        label.setPadding(new Insets(0, 0, 0, 30));
        textField.setMaxWidth(200);
        root.setTop(topVBox);
        root.setCenter(textField);
        root.setBottom(bottonHBox);

        stage.setScene(new Scene(root, 350, 150));
        stage.showAndWait();

        return output;
    }

}
