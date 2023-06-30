package com.example.super_cep.model.SpinnerData;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonConfigDataManagerTest {

    @Test
    public void testSerialize() {
        String dataProvider = JsonConfigDataManager.serialize(new ConfigDataProvider(Map.of("key", List.of("value"))));
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