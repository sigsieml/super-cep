package com.example.super_cep.model.Export;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFColorRgbPercent;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFNoFillProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.BarGrouping;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xslf.usermodel.*;
//import static org.apache.poi.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

import org.openxmlformats.schemas.drawingml.x2006.chart.*;

import java.util.ArrayList;
import java.util.List;

import java.awt.geom.Rectangle2D;

public class CreateChartToSlide {




    public void createBarChart(XMLSlideShow slideShow, XSLFSlide slide, Rectangle2D anchor, List<Anner> anners) throws PowerpointException {
        if(anners.size() == 0) throw new PowerpointException("Nombres d'années insuffisant pour créer le graphique");
        Double[][] values = new Double[3][anners.size()];
        for (int i = 0; i < anners.size(); i++) {
            values[0][i] = anners.get(i).elec;
            values[1][i] = anners.get(i).gaz;
            values[2][i] = anners.get(i).fioul;
        }

        String annersString[] = new String[anners.size()];
        for (int i = 0; i < anners.size(); i++) {
            annersString[i] = String.valueOf(anners.get(i).anner);
        }
        //rgb(0, 69, 121)
        //rgb(235, 107, 10)
        XDDFColor[] colors = new XDDFColor[]{
                XDDFColor.from(hex2Rgb("#004579")),
                XDDFColor.from(hex2Rgb("#eb6b0a")),
                XDDFColor.from(hex2Rgb("#f2c504")),
        };
        createBarChart(slideShow, slide, anchor,values, annersString, new String[]{"Electricité", "Gaz", "Fioul"}, colors);
    }
    public static byte[] hex2Rgb(String colorStr) {
        return new byte[]{
                (byte) Integer.valueOf(colorStr.substring(1, 3), 16).intValue(),
                (byte) Integer.valueOf(colorStr.substring(3, 5), 16).intValue(),
                (byte) Integer.valueOf(colorStr.substring(5, 7), 16).intValue()
        };
    }

    public void createBarChart(XMLSlideShow slideShow, XSLFSlide slide,Rectangle2D anchor,  Double[][] values,String[] categories,  String[] serie, XDDFColor[] colors) {
        // create the data
        int numOfPoints = categories.length;

        Double[] sums = new Double[numOfPoints];
        for (int i = 0; i < sums.length; i++) {
            double sum = 0;
            for (Double[] valueRow : values) {
                sum += valueRow[i];
            }
            sums[i] = sum;
        }

        // create the chart
        XSLFChart chart = slideShow.createChart();

        // add chart to slide
        slide.addChart(chart, anchor);

        // bar chart

        // create data sources
        String categoryDataRange = chart.formatRange(new CellRangeAddress(1, numOfPoints, 0, 0));

        XDDFDataSource<String> categoriesData = XDDFDataSourcesFactory.fromArray(categories, categoryDataRange, 0);

        List<XDDFNumericalDataSource<Double>> valuesData = new ArrayList<XDDFNumericalDataSource<Double>>();
        int c = 1;
        for (Double[] valueRow : values) {
            String valuesDataRange = chart.formatRange(new CellRangeAddress(1, numOfPoints, c, c));
            valuesData.add(XDDFDataSourcesFactory.fromArray(valueRow, valuesDataRange, c));
            c++;
        }

        // create axis
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        // Set AxisCrossBetween, so the left axis crosses the category axis between the categories.
        // Else first and last category is exactly on cross points and the bars are only half visible.
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        // create chart data
        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        ((XDDFBarChartData)data).setBarDirection(BarDirection.COL);
        // stacked bar chart
        ((XDDFBarChartData)data).setBarGrouping(BarGrouping.STACKED);
        ((XDDFBarChartData)data).setOverlap((byte)100);

        // create series
        if (valuesData.size() == 1) {
            // if only one series do not vary colors for each bar
            ((XDDFBarChartData)data).setVaryColors(false);
        } else {
            // if more than one series do vary colors of the series
            ((XDDFBarChartData)data).setVaryColors(true);
        }

        for (int s = 0; s < valuesData.size(); s++) {
            XDDFChartData.Series series = data.addSeries(categoriesData, valuesData.get(s));
            series.setTitle(serie[s], chart.setSheetTitle(serie[s], s+1));
            XDDFFillProperties fillProperties = new XDDFSolidFillProperties(colors[s]);
            series.setFillProperties(fillProperties);
        }

        // plot chart data
        chart.plot(data);

        // add data labels
        for (int s = 0 ; s < valuesData.size(); s++) {
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).addNewDLbls();
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls()
                    .addNewDLblPos().setVal(STDLblPos.CTR);

            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewNumFmt();
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().getNumFmt()
                    .setSourceLinked(false);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().getNumFmt()
                    .setFormatCode("0;-0;");

            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowVal().setVal(true);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowLegendKey().setVal(false);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowCatName().setVal(false);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowSerName().setVal(false);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowPercent().setVal(false);
            chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).getDLbls().addNewShowBubbleSize().setVal(false);
        }


        // line chart
        c = values.length + 1;
        // create data source
        String sumDataRange = chart.formatRange(new CellRangeAddress(1, numOfPoints, c, c));
        XDDFNumericalDataSource<Double> sumData = XDDFDataSourcesFactory.fromArray(sums, sumDataRange, c);

        // axis must be there but must not be visible
        bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setVisible(false);
        leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setVisible(false);

        // set correct cross axis
        bottomAxis.crossAxis(leftAxis);
        leftAxis.crossAxis(bottomAxis);

        data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(categoriesData, sumData);
        series.setTitle("sum", chart.setSheetTitle("sum", c));
        ((XDDFLineChartData.Series)series).setSmooth(false);
        ((XDDFLineChartData.Series)series).setMarkerStyle(MarkerStyle.NONE);
        // don't show the line
        XDDFShapeProperties shapeProperties = new XDDFShapeProperties();
        shapeProperties.setLineProperties(new XDDFLineProperties(new XDDFNoFillProperties()));
        series.setShapeProperties(shapeProperties);

        // plot chart data
        chart.plot(data);

        // correct the id and order, must not start 0 again because there are bar series already
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getIdx().setVal(c);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getOrder().setVal(c);

        // add data labels
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).addNewDLbls();
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls()
                .addNewDLblPos().setVal(STDLblPos.T);

        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewNumFmt();
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().getNumFmt()
                .setSourceLinked(false);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().getNumFmt()
                .setFormatCode("0;-0;");

        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowVal().setVal(true);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowLegendKey().setVal(false);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowCatName().setVal(false);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowSerName().setVal(false);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowPercent().setVal(false);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getDLbls().addNewShowBubbleSize().setVal(false);

    }

}