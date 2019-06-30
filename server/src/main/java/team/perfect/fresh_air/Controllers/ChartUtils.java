package team.perfect.fresh_air.Controllers;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ChartUtils {
    public static byte[] lineChart(List<Integer> pm100Data, List<Integer> pm25Data, List<Integer> hourXAxis) {
        // Create Chart
        XYChart chart = new XYChart(50 * 16, 50 * 9);
        chart.getStyler().setChartBackgroundColor(new Color(255, 255, 255, 255));
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
        chart.getStyler().setAxisTicksVisible(true);
        chart.getStyler().setAxisTitlesVisible(true);

        XYSeries series1 = chart.addSeries("PM10", hourXAxis, pm100Data);
        series1.setLineColor(Color.BLUE);
        series1.setMarker(SeriesMarkers.CIRCLE);
        series1.setLineStyle(SeriesLines.SOLID);

        XYSeries series2 = chart.addSeries("PM2.5", hourXAxis, pm25Data);
        series2.setLineColor(Color.RED);
        series2.setMarker(SeriesMarkers.CIRCLE);
        series2.setLineStyle(SeriesLines.SOLID);

        byte[] img = null;
        try {
            img = BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
        } catch (IOException ioe) {

        }

        return img;
    }
}