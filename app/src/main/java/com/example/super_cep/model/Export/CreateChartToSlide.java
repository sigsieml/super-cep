package com.example.super_cep.model.Export;

import com.example.super_cep.controller.Conso.Anner;
import com.example.super_cep.controller.Conso.Energie;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.SchemeColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFExtensionList;
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
import org.apache.poi.xddf.usermodel.chart.LayoutMode;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartExtensionList;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLayout;
import org.apache.poi.xddf.usermodel.chart.XDDFLegendEntry;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFManualLayout;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xslf.usermodel.*;
//import static org.apache.poi.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;

import java.util.ArrayList;
import java.util.List;

import java.awt.geom.Rectangle2D;

public class CreateChartToSlide {




    public void createBarChart(XMLSlideShow slideShow, XSLFSlide slide, Rectangle2D anchor, List<Anner> anners) throws PowerpointException {
        if(anners.size() == 0) throw new PowerpointException("Nombres d'années insuffisant pour créer le graphique");
        Double[][] values = new Double[Energie.values().length][anners.size()];
        for (int i = 0; i < anners.size(); i++) {
            for (int j = 0; j < Energie.values().length; j++) {
               values[j][i] = anners.get(i).getEnergie(Energie.values()[j]);
            }
        }

        String annersString[] = new String[anners.size()];
        for (int i = 0; i < anners.size(); i++) {
            annersString[i] = String.valueOf(anners.get(i).anner);
        }
        //rgb(0, 69, 121)
        //rgb(235, 107, 10)
        XDDFColor[] colors = new XDDFColor[Energie.values().length];
        for(Energie energie : Energie.values()){
            colors[energie.ordinal()] = energie.getXDDFColor();
        }
        createBarChart(slideShow, slide, anchor,values, annersString, Energie.ENERGIES, colors);
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

        // Legend
        XDDFChartLegend chartLegend = chart.getOrAddLegend();
        chartLegend.setPosition(LegendPosition.BOTTOM);
        XDDFChartExtensionList extLst = chartLegend.getExtensionList();

        XDDFLayout layout = new XDDFLayout();
        layout.setManualLayout(chartLegend.getOrAddManualLayout());
        layout.getManualLayout().setX(0.1485286896873747);
        layout.getManualLayout().setY(0.85653916327457535);
        layout.getManualLayout().setXMode(LayoutMode.EDGE);
        layout.getManualLayout().setYMode(LayoutMode.EDGE);
        layout.getManualLayout().setWidthRatio(0.70294235889146317);
        layout.getManualLayout().setHeightRatio(0.11230960238842484);
        chartLegend.setLayout(layout);

        XDDFTextBody legendTextBody = new XDDFTextBody(chartLegend);
        legendTextBody.getXmlObject().addNewBodyPr();
        legendTextBody.addNewParagraph().addDefaultRunProperties().setFontSize(5d);
        chartLegend.setTextBody(legendTextBody);

        // plot Area
        CTLayout layoutPlotArea = chart.getCTChart().getPlotArea().addNewLayout();
        layoutPlotArea.addNewManualLayout().addNewLayoutTarget().setVal(STLayoutTarget.INNER);
        layoutPlotArea.getManualLayout().addNewXMode().setVal(STLayoutMode.EDGE);
        layoutPlotArea.getManualLayout().addNewYMode().setVal(STLayoutMode.EDGE);
        layoutPlotArea.getManualLayout().addNewX().setVal(3.6564210103133585E-2);
        layoutPlotArea.getManualLayout().addNewY().setVal(5.7110596284499889E-2);
        layoutPlotArea.getManualLayout().addNewW().setVal(0.9268715797937328);
        layoutPlotArea.getManualLayout().addNewH().setVal(0.67455545103675962);
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
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        leftAxis.setVisible(false);
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
            CTDLbls ctdLbls = chart.getCTChart().getPlotArea().getBarChartArray(0).getSerArray(s).addNewDLbls();
            ctdLbls.addNewDLblPos().setVal(STDLblPos.CTR);
            CTNumFmt ctNumFmt = ctdLbls.addNewNumFmt();
            ctNumFmt.setSourceLinked(false);
            ctNumFmt.setFormatCode("0;-0;");
            ctdLbls.addNewShowVal().setVal(true);
            ctdLbls.addNewShowLegendKey().setVal(false);
            ctdLbls.addNewShowCatName().setVal(false);
            ctdLbls.addNewShowSerName().setVal(false);
            ctdLbls.addNewShowPercent().setVal(false);
            ctdLbls.addNewShowBubbleSize().setVal(false);

            // set color of text to white
            ctdLbls.addNewTxPr();
            ctdLbls.getTxPr().addNewBodyPr();
            ctdLbls.getTxPr().addNewP();
            ctdLbls.getTxPr().getPArray(0).addNewPPr();
            ctdLbls.getTxPr().getPArray(0).getPPr().addNewDefRPr();
            ctdLbls.getTxPr().getPArray(0).getPPr().getDefRPr().setB(true);
            ctdLbls.getTxPr().getPArray(0).getPPr().getDefRPr().addNewSolidFill();
            ctdLbls.getTxPr().getPArray(0).getPPr().getDefRPr().getSolidFill().addNewSrgbClr()
                    .setVal(new byte[]{(byte) 255, (byte) 255, (byte) 255});

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
        series.setTitle(" ", chart.setSheetTitle(" ", c));
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