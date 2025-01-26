package com.kajtekH.tests;

import com.kajtekH.model.OutputFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.fail;

public class OutputFileTests {

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void TestData(String candidate)
    {
        try
        {
            OutputFile outputFile = new OutputFile(candidate);
            fail("Wrong data provided");
        }
        catch(IllegalArgumentException e)
        {

        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test.txt", "Test1.csv"})
    public void TestValidData(String candidate)
    {
        try
        {
            OutputFile outputFile = new OutputFile(candidate);
        }
        catch(IllegalArgumentException e)
        {
            fail("Wrong data provided");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test.txt", "Test1.csv"})
    public void TestSetPath(String candidate)
    {
        try
        {
            OutputFile outputFile = new OutputFile("Initial.txt");
            outputFile.setPath(candidate);
        }
        catch(IllegalArgumentException e)
        {
            fail("Wrong data provided");
        }
    }
}
