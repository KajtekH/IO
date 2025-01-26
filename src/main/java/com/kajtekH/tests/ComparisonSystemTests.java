package com.kajtekH.tests;

import com.kajtekH.controller.ComparisonSystem;
import com.kajtekH.model.InputFile;
import com.kajtekH.model.OutputFile;
import com.kajtekH.service.ComparisonMethod;
import com.kajtekH.service.DiffMatchPatchMethod;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;

public class ComparisonSystemTests {

    @ParameterizedTest
    @NullSource
    public void TestSetInputFile(InputFile candidate)
    {
        try
        {
            ComparisonSystem comparisonSystem = new ComparisonSystem();
            comparisonSystem.setInputFile1(candidate);
            comparisonSystem.setInputFile2(candidate);
            fail("Wrong data provided");
        }
        catch(IllegalArgumentException e)
        {

        }
    }

    @ParameterizedTest
    @NullSource
    public void TestSetOutputFile(OutputFile candidate)
    {
        try
        {
            ComparisonSystem comparisonSystem = new ComparisonSystem();
            comparisonSystem.setOutputFile(candidate);
            fail("Wrong data provided");
        }
        catch(IllegalArgumentException e)
        {

        }
    }
}

