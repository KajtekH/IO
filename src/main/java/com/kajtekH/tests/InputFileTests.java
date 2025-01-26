package com.kajtekH.tests;

import com.kajtekH.model.InputFile;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

public class InputFileTests {

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void TestData(String candidate)
    {
        try
        {
            InputFile inputFile = new InputFile(candidate);
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
            InputFile inputFile = new InputFile(candidate);
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
            InputFile inputFile = new InputFile("Initial.txt");
            inputFile.setPath(candidate);
        }
        catch(IllegalArgumentException e)
        {
            fail("Wrong data provided");
        }
    }
}
