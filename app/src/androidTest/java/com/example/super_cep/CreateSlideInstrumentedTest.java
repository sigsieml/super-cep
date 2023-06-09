package com.example.super_cep;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateSlideInstrumentedTest {

    @Test
    public void createSlideAndAddText() {
        XMLSlideShow slideShow = new XMLSlideShow();
        XSLFSlide slide =  slideShow.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        textBox.appendText("salut", true);
        System.out.println("textBox.getText() = " + textBox.getText());
        Assert.assertEquals(textBox.getText(), "salut");

    }
}
