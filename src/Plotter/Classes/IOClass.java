package Plotter.Classes;

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

    public void saveSource(ArrayList<XYChart.Series<String, Number>> lines, String path){
        File file = new File(path);
        String writable ="";
        for(XYChart.Series<String, Number> line:lines) {
            writable += line.getName() + "{\n\t";
            String tmp = "\n\t";
            for (XYChart.Data<String, Number> l : line.getData()) {
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

    public String readSource(String path){
        List<String> in;
        String read = "";
        try {
            in = Files.readAllLines(Paths.get(path));
            for (String s: in){
                read+=s+"\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return read;
    }









    public boolean saveAsPng(LineChart lineChart, String path) {
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
