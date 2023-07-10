package com.example.super_cep.model.Export;

import java.io.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
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
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.util.*;
import org.apache.poi.openxml4j.opc.*;
//import static org.apache.poi.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;
import static org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;
import org.apache.poi.ooxml.*;

import org.apache.xmlbeans.*;

import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.drawingml.x2006.main.*;
import org.openxmlformats.schemas.presentationml.x2006.main.*;

import javax.xml.namespace.QName;

import java.util.List;
import java.util.regex.Pattern;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;

public class CreateChartToSlide {





    public void createBarChart(XSLFSlide slide, List<BarChartData> dataList, String[] series) throws Exception {
        XSLFChartShape XSLFChartShape = createXSLFChart(slide);

        XSLFChartShape.setAnchor(new Rectangle(370,100,300,300));
        // sheet
        XSSFWorkbook workbook = XSLFChartShape.getMyXSLFChart().getXSLFXSSFWorkbook().getXSSFWorkbook();
        XSSFSheet sheet = workbook.getSheetAt(0);
        // determine the type of the category axis from it's first category value (value in A2 in this case)

        // Écrire les données dans la feuille
        int rowNum = 0;
        for (BarChartData data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getCategory());
            for (int i = 0; i < data.getValues().length; i++) {
                row.createCell(i+1).setCellValue(data.getValues()[i]);
            }
        }
        int nbColumns = dataList.get(0).getValues().length;


        CTChartSpace chartSpace = XSLFChartShape.getMyXSLFChart().getChartSpace();
        CTChart cTChart = chartSpace.addNewChart();
        CTPlotArea cTPlotArea = cTChart.addNewPlotArea();
        CTBarChart cTBarChart = cTPlotArea.addNewBarChart();
        cTBarChart.addNewVaryColors().setVal(true);
        cTBarChart.addNewBarDir().setVal(STBarDir.COL);

        for (int r = 1; r <= nbColumns; r++) {
            CTBarSer cTBarSer = cTBarChart.addNewSer();
            CTStrRef cTStrRef = cTBarSer.addNewTx().addNewStrRef();
            cTStrRef.setF("Sheet0!$A$" + (r+1));
            cTStrRef.addNewStrCache().addNewPtCount().setVal(1);
            CTStrVal cTStrVal = cTStrRef.getStrCache().addNewPt();
            cTStrVal.setIdx(0);
            cTStrVal.setV(series[r-1]);
            cTBarSer.addNewIdx().setVal(r-1);

            CTAxDataSource cttAxDataSource = cTBarSer.addNewCat();
            cTStrRef = cttAxDataSource.addNewStrRef();
            cTStrRef.setF("Sheet0!$A$1:$A$" + dataList.size());
            cTStrRef.addNewStrCache().addNewPtCount().setVal(dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                cTStrVal = cTStrRef.getStrCache().addNewPt();
                cTStrVal.setIdx(i);
                cTStrVal.setV(String.valueOf(dataList.get(i).getCategory()));
            }

            CTNumDataSource ctNumDataSource = cTBarSer.addNewVal();
            CTNumRef cTNumRef = ctNumDataSource.addNewNumRef();
            cTNumRef.setF("Sheet0!$" + ((char) (66 + r-1)) + "$2:$" + ((char) (66 + r-1)) + "$" + (dataList.size() + 1));
            cTNumRef.addNewNumCache().addNewPtCount().setVal(dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                CTNumVal cTNumVal = cTNumRef.getNumCache().addNewPt();
                cTNumVal.setIdx(i);
                cTNumVal.setV("" + dataList.get(i).getValues()[r-1]);
            }
        }

        //telling the BarChart that it has axes and giving them Ids
        cTBarChart.addNewAxId().setVal(123456);
        cTBarChart.addNewAxId().setVal(123457);

        //cat axis
        CTCatAx cTCatAx = cTPlotArea.addNewCatAx();
        cTCatAx.addNewAxId().setVal(123456); //id of the cat axis
        CTScaling cTScaling = cTCatAx.addNewScaling();
        cTScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        cTCatAx.addNewDelete().setVal(false);
        cTCatAx.addNewAxPos().setVal(STAxPos.B);
        cTCatAx.addNewCrossAx().setVal(123457); //id of the val axis
        cTCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        //val axis left axis with the values
        CTValAx cTValAx = cTPlotArea.addNewValAx();
        cTValAx.addNewAxId().setVal(123457); //id of the val axis
        CTScaling cTScaling2 = cTValAx.addNewScaling();
        cTScaling2.addNewOrientation().setVal(STOrientation.MIN_MAX);
        cTValAx.addNewDelete().setVal(true);    // display the val axis or not
        cTValAx.addNewAxPos().setVal(STAxPos.L);
        cTValAx.addNewCrossAx().setVal(123456); //id of the cat axis
        cTValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        //legend
        CTLegend cTLegend = cTChart.addNewLegend();
        cTLegend.addNewLegendPos().setVal(STLegendPos.B);
        cTLegend.addNewOverlay().setVal(false);


        cTBarChart.addNewGrouping().setVal(STBarGrouping.STACKED);
        cTPlotArea.getBarChartArray(0).addNewOverlap().setVal((byte) 100);


    }

     //a method for creating the chart XML document /ppt/charts/chart*.xml in the *.pptx ZIP archive
    //and creating a XSLFChartShape as slide shape
    public XSLFChartShape createXSLFChart(XSLFSlide slide) throws Exception {

        OPCPackage oPCPackage = slide.getSlideShow().getPackage();
        int chartCount = oPCPackage.getPartsByName(Pattern.compile("/ppt/charts/chart.*")).size() + 1;
        PackagePartName partName = PackagingURIHelper.createPartName("/ppt/charts/chart" + chartCount + ".xml");
        PackagePart part = oPCPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.drawingml.chart+xml");

        MyXSLFChart myXSLFChart = new MyXSLFChart(part);
        XSLFChartShape XSLFChartShape = new XSLFChartShape(slide, myXSLFChart);

        return XSLFChartShape;
    }

    //a class for providing a XSLFChartShape
    private class XSLFChartShape {
        private CTGraphicalObjectFrame _graphicalObjectFrame;
        private XSLFSlide slide;
        private MyXSLFChart myXSLFChart;

        XSLFChartShape(XSLFSlide slide, MyXSLFChart myXSLFChart) throws Exception {

            String rId = "rId" + (slide.getRelationParts().size()+1);
            slide.addRelation(rId, XSLFRelation.CHART, myXSLFChart);

            long cNvPrId = 1;
            String cNvPrName = "MyChart";
            int cNvPrNameCount = 1;
            for (CTGraphicalObjectFrame currGraphicalObjectFrame : slide.getXmlObject().getCSld().getSpTree().getGraphicFrameList()) {
                if (currGraphicalObjectFrame.getNvGraphicFramePr() != null) {
                    if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr() != null) {
                        cNvPrId++;
                        if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr().getName().startsWith(cNvPrName)) {
                            cNvPrNameCount++;
                        }
                    }
                }
            }

            CTGraphicalObjectFrame graphicalObjectFrame = slide.getXmlObject().getCSld().getSpTree().addNewGraphicFrame();
            CTGraphicalObjectFrameNonVisual cTGraphicalObjectFrameNonVisual = graphicalObjectFrame.addNewNvGraphicFramePr();
            cTGraphicalObjectFrameNonVisual.addNewCNvGraphicFramePr();
            cTGraphicalObjectFrameNonVisual.addNewNvPr();

            CTNonVisualDrawingProps cTNonVisualDrawingProps = cTGraphicalObjectFrameNonVisual.addNewCNvPr();
            cTNonVisualDrawingProps.setId(cNvPrId);
            cTNonVisualDrawingProps.setName("MyChart" + cNvPrNameCount);

            CTGraphicalObject graphicalObject = graphicalObjectFrame.addNewGraphic();
            CTGraphicalObjectData graphicalObjectData = CTGraphicalObjectData.Factory.parse(
                    "<c:chart xmlns:c=\"http://schemas.openxmlformats.org/drawingml/2006/chart\" "
                            +"xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" "
                            +"r:id=\"" + rId + "\"/>"
            );
            graphicalObjectData.setUri("http://schemas.openxmlformats.org/drawingml/2006/chart");
            graphicalObject.setGraphicData(graphicalObjectData);

            _graphicalObjectFrame = graphicalObjectFrame;
            this.slide = slide;
            this.myXSLFChart = myXSLFChart;

            this.setAnchor(new Rectangle());
        }

        private void setAnchor(Rectangle2D anchor) {
            CTTransform2D xfrm = (_graphicalObjectFrame.getXfrm() != null) ? _graphicalObjectFrame.getXfrm() : _graphicalObjectFrame.addNewXfrm();
            CTPoint2D off = xfrm.isSetOff() ? xfrm.getOff() : xfrm.addNewOff();
            long x = Units.toEMU(anchor.getX());
            long y = Units.toEMU(anchor.getY());
            off.setX(x);
            off.setY(y);
            CTPositiveSize2D ext = xfrm.isSetExt() ? xfrm.getExt() : xfrm.addNewExt();
            long cx = Units.toEMU(anchor.getWidth());
            long cy = Units.toEMU(anchor.getHeight());
            ext.setCx(cx);
            ext.setCy(cy);
        }

        private MyXSLFChart getMyXSLFChart() {
            return myXSLFChart;
        }
    }

    //a wrapper class for the ChartSpaceDocument /ppt/charts/chart*.xml in the *.pptx ZIP archive
    private class MyXSLFChart extends POIXMLDocumentPart {

        private CTChartSpace chartSpace;
        private MyXSLFXSSFWorkbook myXSLFXSSFWorkbook;

        private MyXSLFChart(PackagePart part) throws Exception {
            super(part);

            OPCPackage oPCPackage = part.getPackage();
            int chartCount = oPCPackage.getPartsByName(Pattern.compile("/ppt/embeddings/.*.xlsx")).size() + 1;
            PackagePartName partName = PackagingURIHelper.createPartName("/ppt/embeddings/Microsoft_Excel_Worksheet" + chartCount + ".xlsx");
            PackagePart xlsxpart = oPCPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            myXSLFXSSFWorkbook = new MyXSLFXSSFWorkbook(xlsxpart);

            String rId = "rId" + (this.getRelationParts().size()+1);
            XSLFXSSFRelation xSLFXSSFRelationPACKAGE = new XSLFXSSFRelation(
                    "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package");

            this.addRelation(rId, xSLFXSSFRelationPACKAGE, myXSLFXSSFWorkbook);

            chartSpace = ChartSpaceDocument.Factory.newInstance().addNewChartSpace();
            CTExternalData cTExternalData = chartSpace.addNewExternalData();
            cTExternalData.setId(rId);
            //cTExternalData.addNewAutoUpdate().setVal(true);
        }

        private CTChartSpace getChartSpace() {
            return chartSpace;
        }

        private MyXSLFXSSFWorkbook getXSLFXSSFWorkbook() {
            return myXSLFXSSFWorkbook;
        }

        @Override
        protected void commit() throws IOException {
            XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
            xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.type.getName().getNamespaceURI(), "chartSpace", "c"));
            PackagePart part = getPackagePart();
            OutputStream out = part.getOutputStream();
            chartSpace.save(out, xmlOptions);
            out.close();
        }

    }

    //a wrapper class for the XSSFWorkbook /ppt/embeddings/Microsoft_Excel_Worksheet*.xlsx in the *.pptx ZIP archive
    private class MyXSLFXSSFWorkbook extends POIXMLDocumentPart {

        private XSSFWorkbook workbook;

        private MyXSLFXSSFWorkbook(PackagePart part) throws Exception {
            super(part);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
        }

        private XSSFWorkbook getXSSFWorkbook() {
            return workbook;
        }

        @Override
        protected void commit() throws IOException {
            PackagePart part = getPackagePart();
            OutputStream out = part.getOutputStream();
            workbook.write(out);
            workbook.close();
            out.close();
        }
    }

    //a class to note the relations
    private class XSLFXSSFRelation extends POIXMLRelation {
        private XSLFXSSFRelation(String rel) {
            super(null, rel, null);
        }
    }
}