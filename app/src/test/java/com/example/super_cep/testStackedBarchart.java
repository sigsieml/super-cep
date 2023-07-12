package com.example.super_cep;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
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
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDLblPos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testStackedBarchart {
    @Test
    public void createStackedBarchartTest(){
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("barchart");
            final int NUM_OF_ROWS = 3;
            final int NUM_OF_COLUMNS = 10;

            // Create a row and put some cells in it. Rows are 0 based.
            Row row;
            Cell cell;
            for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
                row = sheet.createRow((short) rowIndex);
                for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
                    cell = row.createCell((short) colIndex);
                    cell.setCellValue(colIndex * (rowIndex + 1.0));
                }
            }

            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("x = 2x and x = 3x");
            chart.setTitleOverlay(false);
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            // Use a category axis for the bottom axis.
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("x");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("f(x)");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
            XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));
            XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));

            XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            XDDFChartData.Series series1 = data.addSeries(xs, ys1);
            series1.setTitle("2x", null);
            XDDFChartData.Series series2 = data.addSeries(xs, ys2);
            series2.setTitle("3x", null);
            chart.plot(data);

            // in order to transform a bar chart into a column chart, you just need to change the bar direction
            XDDFBarChartData bar = (XDDFBarChartData) data;
            bar.setBarDirection(BarDirection.COL);
            bar.setBarGrouping(BarGrouping.STACKED);
            // correcting the overlap so bars really are stacked and not side by side
            chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte)100);


            solidFillSeries(data, 0, PresetColor.CHARTREUSE);
            solidFillSeries(data, 1, PresetColor.TURQUOISE);

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("ooxml-bar-chart.xlsx")) {
                wb.write(fileOut);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void solidFillSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFChartData.Series series = data.getSeries(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        series.setShapeProperties(properties);
    }
    @Test
    public void testStackedLabelisedBarChart(){
        try (XMLSlideShow slideShow = new XMLSlideShow()) {

            XSLFSlide slide = slideShow.createSlide();

            // create the data
            String[] categories = new String[]{"KW1", "KW2", "KW3", "KW4", "KW5", "KW6"};
            int numOfPoints = categories.length;

            Double[][] values = new Double [][] {
                    new Double[]{10d, 0d, 20d, 5d, 30d, 10d},
                    new Double[]{15d, 35d, 25d, 15d, 10d, 8d},
                    new Double[]{5d, 15d, 0d, 25d, 15d, 0d},
                    new Double[]{10d, 5d, 30d, 30d, 20d, 12d}
            };
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
            slide.addChart(chart, new java.awt.geom.Rectangle2D.Double(1d* Units.EMU_PER_CENTIMETER, 1d*Units.EMU_PER_CENTIMETER, 20d*Units.EMU_PER_CENTIMETER, 15d*Units.EMU_PER_CENTIMETER));

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
                series.setTitle("Series"+(s+1), chart.setSheetTitle("Series"+(s+1), s+1));
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

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("CreatePowerPointStackedBarChartXDDFChart.pptx")) {
                slideShow.write(fileOut);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
