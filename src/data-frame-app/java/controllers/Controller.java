package controllers;

import dataframe.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.EnterDataHeaderWindow;
import view.EnterOrComputeDataTypesWindow;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;


public class Controller {

    private final ArrayList<ArrayList<Value>> dataArray = new ArrayList<>();
    public TableView<ObservableList<Value>> tableviewLeft;
    public TableView<ObservableList<Value>> tableViewMiddle;
    public TextField groupByColNamesTextField;
    public TextField groupByColNamesTextField2;
    public Button chartsButton;
    public Button maxButton;
    public Button minButton;
    public Button stdButton;
    public Button varButton;
    public Button meanButton;
    public Button hideGroupedButton;
    public Button lineChartButton;
    public Button confirmGroupByButton;
    public ComboBox<String> xAxisCombo1;
    public ComboBox<String> yAxisCombo;
    public ComboBox<String> functionCombo;
    public AnchorPane chartPane;
    public AnchorPane statPane;
    public Label selectColNamesLabel;
    public Label delimitLabel;
    public Label selectColNamesLabel1;
    public Label chooseAxisLabel;
    public Label pathLabel;
    public Button loadFileButton;
    public Button enterTypesButton;
    public Button initializeDFButton;
    public Button statisticsButton;
    public Button resetButton;
    public ComboBox<String> xAxisCombo2;
    public TextField yAxesColNamesTextField;
    public Button scatterChartButton;
    public AnchorPane chartTypesPane;
    public Button boxChartButton;
    private File inputFile;
    private DataFrame model;
    private DataFrame groupedModel;
    private String colTypes;
    private String colNamesToGroupBy;


    private void setPathLabelText(String text) {
        pathLabel.setText(text);
    }


    public void handleLoadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load csv file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("csv files", "*.csv"));
        inputFile = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        if (inputFile != null) {
            unlockButton(loadFileButton);
            unlockButton(enterTypesButton);
            setPathLabelText("Loaded: " + inputFile.getName());
        }
    }


    public void handleEnterTypesButtonClick() {
        String output = EnterOrComputeDataTypesWindow.show("Enter columns types", "Columns types separated by a space:", inputFile);
        if (output != null && !output.equals("null")) {
            colTypes = output;
            unlockButton(initializeDFButton);
        }
    }


    public void handleInitializeDFButtonClick() {
        String[] coltypesArray = colTypes.split("\\s");
        ArrayList<Class<? extends Value>> types = new ArrayList<>();

        try {
            for (int i = 0; i < coltypesArray.length; i++) {
                if (coltypesArray[i].equals("String"))
                    types.add(new StringValue().getClass());
                else if (coltypesArray[i].equals("Integer"))
                    types.add(new IntegerValue().getClass());
                else if (coltypesArray[i].equals("Double"))
                    types.add(new DoubleValue().getClass());
                else if (coltypesArray[i].equals("Float"))
                    types.add(new FloatValue().getClass());
                else if (coltypesArray[i].equals("DateTime"))
                    types.add(new DateTimeValue().getClass());
                else throw new IllegalArgumentException("Cannot recognize this type: " + coltypesArray[i]);
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Given wrong type.", e.getMessage());
        }
        String output = EnterDataHeaderWindow.show("Set columns names", "Does the first row of the file contain column headers?", coltypesArray.length);

        try {
            if (output != null && output.equals("headerDefinedInTheFile")) {
                model = new DataFrame(inputFile.getAbsolutePath(), types);
            }
            else if (output != null && !output.equals("null")) {
                String[] colNamesArray = output.split("\\s");
                if (colNamesArray.length != coltypesArray.length)
                    throw new InvalidParameterException("The number of column names does not equal the number of types");
                ArrayList<String> header = new ArrayList<>();
                for (String s : colNamesArray)
                    header.add(s);
                model = new DataFrame(inputFile.getAbsolutePath(), types, header);
            }


            StringBuilder stringBuilder = new StringBuilder();

            for (String s : model.names) {
                stringBuilder.append(s);
                stringBuilder.append(" ");
            }
            String colnamesToDisplay = stringBuilder.toString();

            tableviewLeft.setVisible(true);

            displayDataFrame(model, tableviewLeft);

            showAlert(Alert.AlertType.INFORMATION, "Data Frame has been initialized successfully",
                    "Column names: " + colnamesToDisplay + "\nColumn types: " + colTypes);

            setPathLabelText("Loaded: " + inputFile.getName() + ". Columns names: " + colnamesToDisplay + ", columns types: " + colTypes);

        } catch (InconsistentColumnTypeException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong data, cannot create DataFrame", e.getMessage());
            unlockButton(loadFileButton);
        } catch (ArrayIndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong data in the selected file", e.getMessage());
        } catch (InvalidParameterException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong size", e.getMessage());
        }

        functionCombo.getItems().removeAll(functionCombo.getItems());
        functionCombo.getItems().addAll("max", "min", "std", "var", "mean");

        lockButton(loadFileButton);
        lockButton(enterTypesButton);

        unlockButton(resetButton);
        if (model != null) {
            unlockButton(statisticsButton);
            unlockButton(chartsButton);
        }
    }


    public void handleStatisticsButton() {
        groupByColNamesTextField.setText("");
        if (statPane.isVisible())
            statPane.setVisible(false);
        else
            statPane.setVisible(true);

        if (!chartPane.isVisible() && !statPane.isVisible() && tableViewMiddle.isVisible())
            tableViewMiddle.setVisible(false);
    }


    private void createTableView(TableView<ObservableList<Value>> tableView, ArrayList<ArrayList<Value>> dataArray, DataFrame df) {
        ObservableList<ObservableList<Value>> data = FXCollections.observableArrayList();

        for (ArrayList<Value> a : dataArray) {
            data.add(FXCollections.observableArrayList(a));
        }
        tableView.setItems(data);

        for (int i = 0; i < df.names.size(); i++) {
            final int curCol = i;
            final TableColumn<ObservableList<Value>, Value> column = new TableColumn<>(
                    df.names.get(curCol)
            );
            column.setCellValueFactory(
                    param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol))
            );
            tableView.getColumns().add(column);
        }
    }


    private void displayDataFrame(DataFrame df, TableView<ObservableList<Value>> tableView) {
        if (tableView.getColumns().size() != 0)
            tableView.getColumns().clear();

        dataArray.clear();

        ArrayList<Value> row = new ArrayList<>();
        for (int j = 0; j < Math.min(df.data.get(0).size(), 1000); j++) {
            for (int i = 0; i < df.names.size(); i++) {
                row.add(df.data.get(i).get(j));
            }
            dataArray.add(new ArrayList<>(row));
            row.clear();
        }

        createTableView(tableView, dataArray, df);
    }


    private void showAlert(Alert.AlertType alertType, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        if (alertType == Alert.AlertType.ERROR)
            alert.setTitle("Error");
        else if (alertType == Alert.AlertType.INFORMATION)
            alert.setTitle("Information");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    public void handleGroupByMaxButtonClick() {
        try {
            tableViewMiddle.setVisible(true);
            colNamesToGroupBy = groupByColNamesTextField.getText();
            String[] colNamesToGroupBySplit = colNamesToGroupBy.split("\\s");
            groupedModel = model.groupBy(colNamesToGroupBySplit).max();
            displayDataFrame(groupedModel, tableViewMiddle);
            tableViewMiddle.setVisible(true);
            hideGroupedButton.setVisible(true);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "");
        }
    }


    public void handleGroupByMinButtonClick() {
        try {
            tableViewMiddle.setVisible(true);
            colNamesToGroupBy = groupByColNamesTextField.getText();
            String[] colNamesToGroupBySplit = colNamesToGroupBy.split("\\s");
            groupedModel = model.groupBy(colNamesToGroupBySplit).min();
            displayDataFrame(groupedModel, tableViewMiddle);
            tableViewMiddle.setVisible(true);
            hideGroupedButton.setVisible(true);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "");
        }
    }


    public void handleGroupByStdButtonClick() {
        try {
            tableViewMiddle.setVisible(true);
            colNamesToGroupBy = groupByColNamesTextField.getText();
            String[] colNamesToGroupBySplit = colNamesToGroupBy.split("\\s");
            groupedModel = model.groupBy(colNamesToGroupBySplit).std();
            displayDataFrame(groupedModel, tableViewMiddle);
            tableViewMiddle.setVisible(true);
            hideGroupedButton.setVisible(true);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "");
        }
    }


    public void handleGroupByVarButtonClick() {
        try {
            tableViewMiddle.setVisible(true);
            colNamesToGroupBy = groupByColNamesTextField.getText();
            String[] colNamesToGroupBySplit = colNamesToGroupBy.split("\\s");
            groupedModel = model.groupBy(colNamesToGroupBySplit).var();
            displayDataFrame(groupedModel, tableViewMiddle);
            tableViewMiddle.setVisible(true);
            hideGroupedButton.setVisible(true);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "");
        }
    }


    public void handleGroupByMeanButtonClick() {
        try {
            tableViewMiddle.setVisible(true);
            colNamesToGroupBy = groupByColNamesTextField.getText();
            String[] colNamesToGroupBySplit = colNamesToGroupBy.split("\\s");
            groupedModel = model.groupBy(colNamesToGroupBySplit).mean();
            displayDataFrame(groupedModel, tableViewMiddle);
            tableViewMiddle.setVisible(true);
            hideGroupedButton.setVisible(true);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "");
        }
    }


    public void handleChartsButtonClick() {
        groupByColNamesTextField2.setText("");
        yAxesColNamesTextField.setText("");
        if (chartPane.isVisible()) {
            chartPane.setVisible(false);
            delimitLabel.setVisible(false);
        }
        else {
            chartPane.setVisible(true);
            chartTypesPane.setVisible(false);
            if (statPane.isVisible())
                delimitLabel.setVisible(true);
        }
        if (!chartPane.isVisible() && !statPane.isVisible() && tableViewMiddle.isVisible())
            tableViewMiddle.setVisible(false);

    }


    public void handleHideButtonClick() {
        tableViewMiddle.setVisible(false);
        hideGroupedButton.setVisible(false);
    }


    private void unlockButton(Button b) {
        b.setDisable(false);
    }


    private void lockButton(Button b) {
        b.setDisable(true);
    }


    public void handleConfirmInChartSectionButtonClick() {
        try {
            String[] colNamesSplit = groupByColNamesTextField2.getText().split("\\s");
            String functionName = functionCombo.getValue();
            switch (functionName) {
                case "max":
                    groupedModel = model.groupBy(colNamesSplit).max();
                    break;
                case "min":
                    groupedModel = model.groupBy(colNamesSplit).min();
                    break;
                case "std":
                    groupedModel = model.groupBy(colNamesSplit).std();
                    break;
                case "var":
                    groupedModel = model.groupBy(colNamesSplit).var();
                    break;
                case "mean":
                    groupedModel = model.groupBy(colNamesSplit).mean();
                    break;
            }

            xAxisCombo1.getItems().removeAll(xAxisCombo1.getItems());
            yAxisCombo.getItems().removeAll(yAxisCombo.getItems());
            for (String s : groupedModel.names) {
                xAxisCombo1.getItems().add(s);
                xAxisCombo2.getItems().add(s);
                yAxisCombo.getItems().add(s);
            }

            chartTypesPane.setVisible(true);

        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Statistic function must be chosen.", e.getMessage());

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong column name or no value was given", e.getMessage());
        }

    }


    public void handleLineChartButtonClick() {
        String xAxisCol = xAxisCombo1.getValue();
        String yAxisCol = yAxisCombo.getValue();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisCol);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisCol);
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("'" + xAxisCol + "' vs. '" + yAxisCol + "' chart");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("data");
        try {
            if (xAxisCol.equals(yAxisCol))
                throw new IllegalArgumentException("Cannot do the '" + xAxisCol + "' vs. '" + yAxisCol + "' chart.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Change axes. ", e.getMessage());
        }
        try {
            for (int i = 0; i < groupedModel.data.get(0).size(); i++) {
                series.getData().add(new XYChart.Data<>(groupedModel.get(xAxisCol).get(i).toString(), Double.parseDouble(groupedModel.get(yAxisCol).get(i).toString())));
            }
            Stage chartStage = new Stage();
            Scene chartScene = new Scene(lineChart, 550, 400);
            lineChart.getData().add(series);
            chartStage.setTitle("LINE CHART");
            chartStage.setScene(chartScene);
            chartStage.show();
        } catch (IndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Both axes must be chosen.", "Choose X and Y axes.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Y-axis must be numeric", "'" + yAxisCol + "' cannot be converted to a numeric type.");
        }

    }


    public void handleScatterOrBarChartButtonClick(ActionEvent actionEvent) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        XYChart<String, Number> chart = null;
        if (((Button) actionEvent.getSource()).getText().equals("SCATTER CHART")) {
            chart = new ScatterChart<String, Number>(xAxis, yAxis);
        }
        else if (((Button) actionEvent.getSource()).getText().equals("BAR CHART")) {
            chart = new BarChart<String, Number>(xAxis, yAxis);
        }
        xAxis.setLabel(xAxisCombo1.getValue());
        yAxis.setLabel("Returns to date");
        chart.setTitle("Data Overview");

        try {
            String[] yAxesCols = yAxesColNamesTextField.getText().split("\\s");

            for (int i = 0; i < yAxesCols.length; i++) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(yAxesCols[i]);

                for (int j = 0; j < groupedModel.data.get(0).size(); j++) {
                    series.getData().add(new XYChart.Data<>(groupedModel.get(xAxisCombo2.getValue()).get(j).toString(), Double.parseDouble(groupedModel.get(yAxesCols[i]).get(j).toString())));
                }

                chart.getData().add(series);
            }
            Stage chartStage = new Stage();
            Scene chartScene = new Scene(chart, 550, 400);
            chartStage.setTitle(((Button) actionEvent.getSource()).getText());
            chartStage.setScene(chartScene);
            chartStage.show();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong Y-axis chosen", "Change Y-axes columns");
        } catch (IndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Wrong column name", "Cannot recognize given name/names");
        }

    }


    public void handleResetButtonClick() {
        model = null;
        groupedModel = null;
        unlockButton(loadFileButton);
        lockButton(initializeDFButton);
        lockButton(statisticsButton);
        lockButton(chartsButton);
        statPane.setVisible(false);
        chartPane.setVisible(false);
        tableviewLeft.setVisible(false);
        tableViewMiddle.setVisible(false);
        delimitLabel.setVisible(false);
        chartTypesPane.setVisible(false);
        groupByColNamesTextField.setText("");
        groupByColNamesTextField2.setText("");
        yAxesColNamesTextField.setText("");
        setPathLabelText("File not loaded.");
    }
}
