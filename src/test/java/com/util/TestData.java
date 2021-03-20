package com.util;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Properties;

public class TestData
{
    private static Properties testData;
    static
    {
        String DataFile;

        String env = System.getProperty("env" , "test");

        if (env.equalsIgnoreCase("int"))
        {
            DataFile = System.getProperty("testDataFile", "testData.INT.properties");
        } else {
            DataFile = System.getProperty("testDataFile", "testData.TEST.properties");
        }

        testData = new Properties();
        try
        {
            testData.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(DataFile));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getDataItem(String s)
    {
        return testData.getProperty(s);
    }


    public static String getPassword()
    {
        byte[] decodedBytes = Base64.decodeBase64(TestData.getDataItem("tda.password"));
        return new String(decodedBytes);
    }

}
