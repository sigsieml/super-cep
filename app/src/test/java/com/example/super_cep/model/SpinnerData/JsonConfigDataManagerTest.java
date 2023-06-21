package com.example.super_cep.model.SpinnerData;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JsonConfigDataManagerTest {

    @Test
    public void testSerialize() {
        String dataProvider = JsonConfigDataManager.serialize(new ConfigDataProvider());
        try (FileOutputStream out = new FileOutputStream("configData.json")) {
            out.write(dataProvider.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert true;
    }

}