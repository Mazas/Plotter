package Plotter.Classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.chart.*;

import java.util.ArrayList;
import java.util.InputMismatchException;

public class Controller {
    private ArrayList<XYChart.Series<String, Number>> lines = new ArrayList<>();
    private String name = "";
    private IOClass io = new IOClass();

    @FXML
    private LineChart<String, Number> graph;
    @FXML
    private TextField nameInput, xLabel, yLabel;
    @FXML
    private TextArea xPoints, yPoints, lineList;

    public void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void export(ActionEvent actionEvent) {
        io.saveAsPng(graph,"image.png");
    }

    public void newLineButton(ActionEvent actionEvent) {
        String[] xData = xPoints.getText().split(" ");
        String[] yData = yPoints.getText().split(" ");
        XYChart.Series<String, Number> line = new XYChart.Series<>();
        try {
            if (xData.length != yData.length) {
                throw new InputMismatchException("Vectors must be the same length.");
            } else if (xData.length < 1 || xData[0].equalsIgnoreCase("") || yData[0].equalsIgnoreCase("")) {
                throw new InputMismatchException("Vectors are empty.");
            }
            for (int i = 0; i < xData.length; i++) {
                line.getData().add(new XYChart.Data<>(xData[i], Integer.parseInt(yData[i])));
            }
            line.setName(name);
            lines.add(line);
        } catch (InputMismatchException ie) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(ie.getMessage());
            alert.showAndWait();
        }catch (NumberFormatException nfe) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Use numbers.");
            alert.setHeaderText(null);
            nfe.printStackTrace();
            lines.clear();
            alert.showAndWait();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText(e.getMessage());
            alert.setHeaderText(null);
            e.printStackTrace();
            lines.clear();
            alert.showAndWait();
        }
        name = "";
        nameInput.setText("");
        xPoints.setText("");
        yPoints.setText("");
        lineList.appendText(line.getName()+"{\n\t");
        String tmp = "\n\t";
        for(XYChart.Data<String,Number> l:line.getData()) {
            lineList.appendText(l.getXValue() + " ");
            tmp += l.getYValue() + " ";
        }
        lineList.appendText(tmp+"\n}\n");
    }

    public void plot(ActionEvent actionEvent) {
        graph.getData().clear();
        for (XYChart.Series<String, Number> l : lines) {
            graph.getData().add(l);
        }
    }

    public void xAxisLabelChanged(KeyEvent keyEvent) {
        graph.getXAxis().setLabel(xLabel.getText());
    }

    public void yAxisLabelChanged(KeyEvent keyEvent) {
        graph.getYAxis().setLabel(yLabel.getText());
    }

    public void nameChanged(KeyEvent keyEvent) {
        name = nameInput.getText();
    }

    public void open(ActionEvent actionEvent) {
        lineList.setText(io.readSource("../Source.txt"));
        // TODO
    }

    public void saveSource(ActionEvent actionEvent) {
        // TODO change path
         io.saveSource(lines,"../Source.txt");
    }
}
