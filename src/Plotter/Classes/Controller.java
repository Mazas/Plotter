package Plotter.Classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.chart.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class Controller {
    private IOClass io = new IOClass();
    private boolean showSymbols=true;

    @FXML private CheckMenuItem drawSymbolsButton, setBoundsButton;
    @FXML private LineChart<Number, Number> graph;
    @FXML private TextField nameInput, xLabel, yLabel;
    @FXML private TextArea xPoints, yPoints, lineList;
    @FXML private NumberAxis xAxis, yAxis;

    public void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void export(ActionEvent actionEvent) {
        File file = getFile(2);
        if (file!=null) {
            io.saveAsPng(graph, file.getAbsolutePath());
        }
    }

    public void newLineButton(ActionEvent actionEvent) {
        String[] xData = xPoints.getText().split(" ");
        String[] yData = yPoints.getText().split(" ");
        if (xData.length != yData.length) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Vectors must be the same length.");
            alert.showAndWait();
        } else if (xData.length < 1 || xData[0].equalsIgnoreCase("") || yData[0].equalsIgnoreCase("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Vectors are empty.");
            alert.showAndWait();
        }
        lineList.appendText("$"+nameInput.getText()+"{\n\t"+xPoints.getText()+",\n\t"+yPoints.getText()+"\n}\n");
        nameInput.setText("");
        xPoints.setText("");
        yPoints.setText("");
    }

    public void plot(ActionEvent actionEvent) {
        graph.getData().clear();
        for (XYChart.Series<Number, Number> l : inputToList()) {
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
    }

    public void open(ActionEvent actionEvent) {
        File file = getFile(0);
        if (file!=null&&file.exists()){
            lineList.setText(io.readSource(file.getAbsolutePath()));
        }
    }

    public void saveSource(ActionEvent actionEvent) {
        File file = getFile(1);
        if (file!=null) {
            io.saveSource(inputToList(), graph.getXAxis().getLabel(), graph.getYAxis().getLabel(), file.getAbsolutePath());
        }
    }

    private ArrayList<XYChart.Series<Number, Number>> inputToList(){
        ArrayList<XYChart.Series<Number, Number>> lines = new ArrayList<>();
        String[] input = lineList.getText().trim().split("\\$");
        try {
            if (input.length>1) {
                for (String anInput : input) {
                    if (anInput.contains("xAxisName{")) {
                        graph.getXAxis().setLabel(anInput.substring(10, anInput.indexOf('}')));
                        xLabel.setText(anInput.substring(10, anInput.indexOf('}')));
                    } else if (anInput.contains("yAxisName{")) {
                        graph.getYAxis().setLabel(anInput.substring(10, anInput.indexOf('}')));
                        yLabel.setText(anInput.substring(10, anInput.indexOf('}')));
                    } else if (anInput.contains("{") && anInput.contains("}")) {
                        XYChart.Series<Number, Number> line = new XYChart.Series<>();
                        line.setName(anInput.substring(0, anInput.indexOf('{')));
                        String[] dataX = anInput.substring(anInput.indexOf('{') + 1, anInput.indexOf(',')).trim().split(" ");
                        String[] dataY = anInput.substring(anInput.indexOf(',') + 1, anInput.indexOf('}')).trim().split(" ");

                        for (int a = 0; a < dataX.length; a++) {
                            XYChart.Data<Number,Number> data = new XYChart.Data<>(Double.parseDouble(dataX[a].trim()), Double.parseDouble(dataY[a].trim()));
                            data.setNode(new NodeLabel(Double.parseDouble(dataY[a].trim())));
                            line.getData().add(data);
                        }
                        lines.add(line);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * @param open 0 if opening file, 1 if saving file, 2 if saving image
     * */
    private File getFile(int open){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = null;
        switch (open) {
            case 0:
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),new FileChooser.ExtensionFilter("Source Files", "*.txt"));
                fc.setTitle("Open");
                file = fc.showOpenDialog(new Stage());
                break;
            case 1:
                fc.setTitle("Save");
                fc.setInitialFileName("source.txt");
                file=fc.showSaveDialog(new Stage());
                break;
            case 2:
                fc.setTitle("Export");
                fc.setInitialFileName("image.png");
                file=fc.showSaveDialog(new Stage());
                break;
        }
        return file;
    }

    public void drawSymbols(ActionEvent actionEvent) {
        //System.out.println(drawSymbolsButton.isSelected());
        if (!graph.getData().isEmpty()) {
            showSymbols = drawSymbolsButton.isSelected();
        }else {
            drawSymbolsButton.setSelected(showSymbols);
        }
        graph.setCreateSymbols(showSymbols);
    }

    public void setBounds(ActionEvent actionEvent) {
        if (!setBoundsButton.isSelected()||graph.getData().size()<1){
            xAxis.setAutoRanging(true);
            setBoundsButton.setSelected(false);
            return;
        }
        int smallest =graph.getData().get(0).getData().get(0).getXValue().intValue();
        int largest = graph.getData().get(0).getData().get(0).getXValue().intValue();
        for (XYChart.Series<Number,Number> line:graph.getData()){
                for (XYChart.Data<Number,Number> point:line.getData()){
                    if (point.getXValue().intValue()<smallest){
                        smallest =point.getXValue().intValue();
                    }if (point.getXValue().intValue()>largest){
                        largest =point.getXValue().intValue();
                    }
                }
        }
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(smallest);
        xAxis.setUpperBound(largest);
    }

        public void clearGraph(ActionEvent actionEvent){
        graph.getData().clear();
    }
}
