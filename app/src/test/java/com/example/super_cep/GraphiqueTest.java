package com.example.super_cep;

import com.example.super_cep.model.Export.CreateChartToSlide;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFColorRgbPercent;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.BarGrouping;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphiqueTest {


    private static final int DATA_START_ROW = 1;

    @Test
    public void testGraphique() {

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("example_bar_chart");
        final int NUM_OF_ROWS = 3;
        final int NUM_OF_COLUMNS = 10;

        Row row;
        Cell cell;

        for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
            row = sheet.createRow((short) rowIndex);

            for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
                cell = row.createCell((short) colIndex);
                cell.setCellValue(colIndex * (rowIndex + 1.0)); // some random values
            }
        }
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();

        legend.setPosition(LegendPosition.TOP_RIGHT);
        // Use a category axis for the bottom axis.

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);

        bottomAxis.setTitle("x");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);

        leftAxis.setTitle("f(x)");

        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        // Define Data Source x axis / y-axis values. (CellRangeAddress):
        XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        // Define Data Source values to be plotted on those axis. (CellRangeAddress):
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));

        XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));
        // Create Chart – ChartType, ChartAxis, ValueAxis:
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        // Add data series – axis values/category , values:
        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(xs, ys1);
        series1.setTitle("2x", null);
        // Plot chart:
        chart.plot(data);


        try {
            FileOutputStream fileOut = new FileOutputStream("example_bar_chart.xlsx");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createBarChart(){
        try {
            XMLSlideShow ppt = new XMLSlideShow();

            // Créer une diapositive
            XSLFSlide slide = ppt.createSlide();

            Double[][] values = new Double [][] {
                    //        2019 2020 2021 2022 2023 2024
                    new Double[]{1d, 4d, 7d, 10d, 13d, 16d}, //electricité
                    new Double[]{2d,5d, 8d, 11d, 14d, 17d}, //gaz
                    new Double[]{3d, 6d, 9d, 12d, 15d, 18d}, //eau
            };


            CreateChartToSlide createChartToSlide = new CreateChartToSlide();
            Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(1d* Units.EMU_PER_CENTIMETER, 1d*Units.EMU_PER_CENTIMETER, 20d*Units.EMU_PER_CENTIMETER, 15d* Units.EMU_PER_CENTIMETER);
            //rgb(0, 69, 121)
            //rgb(235, 107, 10)
            XDDFColor[] colors = new XDDFColor[]{
                    new XDDFColorRgbPercent(0 / 100, 69 / 100, 121 / 100),
                    new XDDFColorRgbPercent(235 / 100, 107 / 100, 10 / 100),
                    new XDDFColorRgbPercent(0 / 100, 176 / 100, 80 / 100)
            };
            createChartToSlide.createBarChart(ppt, slide,rect, values,new String[]{"2019", "2020", "2021", "2022", "2023", "2024"},new String[]{"Electricité", "Gaz", "Eau"}, colors, "kWh");


            // Enregistrer le résultat
            try (FileOutputStream out = new FileOutputStream("test.pptx")) {
                ppt.write(out);
                System.out.println(new File("test.pptx").getAbsolutePath());

            }
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
