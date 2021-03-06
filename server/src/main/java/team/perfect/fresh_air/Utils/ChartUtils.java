package team.perfect.fresh_air.Utils;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ChartUtils {
    public static byte[] lineChart(List<Integer> pm100Data, List<Integer> pm25Data, List<Integer> hourXAxis) {
        // Create Chart
        XYChart chart = new XYChart(50 * 16, 50 * 9);

        chart.setXAxisTitle("Hour");
        chart.setYAxisTitle("ug/m3");

        Color background = new Color(6, 36, 59);
        Color white = new Color(255, 255, 255, 255);
        Color white_transparent = new Color(255, 255, 255, 150);

        chart.getStyler().setChartBackgroundColor(background);
        chart.getStyler().setPlotBackgroundColor(background);
        chart.getStyler().setLegendBackgroundColor(background);
        chart.getStyler().setToolTipBackgroundColor(background);
        chart.getStyler().setChartTitleBoxBackgroundColor(background);
        chart.getStyler().setChartFontColor(white);
        chart.getStyler().setErrorBarsColor(white);
        chart.getStyler().setPlotBorderColor(white);
        chart.getStyler().setLegendBorderColor(background);
        chart.getStyler().setAxisTickMarksColor(white);
        chart.getStyler().setPlotGridLinesColor(white_transparent);
        chart.getStyler().setToolTipBorderColor(white);
        chart.getStyler().setAxisTickLabelsColor(white);
        chart.getStyler().setToolTipHighlightColor(white);
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
        chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
        chart.getStyler().setAxisTicksLineVisible(false);
        chart.getStyler().setAxisTicksVisible(true);
        chart.getStyler().setAxisTicksMarksVisible(false);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setAxisTitlesVisible(true);

        XYSeries series1 = chart.addSeries("PM10", hourXAxis, pm100Data);
        series1.setLineColor(white);
        series1.setMarkerColor(white);
        series1.setFillColor(white);
        series1.setMarker(SeriesMarkers.CIRCLE);
        series1.setLineStyle(SeriesLines.SOLID);

        XYSeries series2 = chart.addSeries("PM2.5", hourXAxis, pm25Data);
        series2.setLineColor(white_transparent);
        series2.setMarkerColor(white_transparent);
        series2.setFillColor(white_transparent);
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