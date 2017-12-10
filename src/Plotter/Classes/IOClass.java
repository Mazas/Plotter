package Plotter.Classes;

import com.sun.javafx.tk.Toolkit;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IOClass {

    public void saveSource(ArrayList<XYChart.Series<Number, Number>> lines,String xAxis,String yAxis, File file){
        String writable ="$xAxisName{"+xAxis+"}\n$yAxisName{"+yAxis+"}\n";
        for(XYChart.Series<Number, Number> line:lines) {
            writable += "$"+line.getName() + "{\n\t";
            String tmp = ",\n\t";
            for (XYChart.Data<Number, Number> l : line.getData()) {
                writable+=l.getXValue() + " ";
                tmp += l.getYValue() + " ";
            }
            writable+=tmp + "\n}";
        }
        try(FileWriter fw = new FileWriter(file)){
            fw.write(writable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readSource(File file){
        List<String> in;
        String read = "";
        try {
            in = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (String s: in){
                read+=s+"\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return read;
    }

    public boolean saveAsPng(LineChart lineChart, File file) {
        double height = lineChart.getMinHeight(),width = lineChart.getMinWidth();
        lineChart.setMinSize(Toolkit.getToolkit().getScreenConfigurationAccessor().getWidth(Toolkit.getToolkit().getPrimaryScreen()),
                Toolkit.getToolkit().getScreenConfigurationAccessor().getHeight(Toolkit.getToolkit().getPrimaryScreen()));
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            lineChart.setMinSize(width, height);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            lineChart.setMinSize(width, height);
            return false;
        }
    }
}
