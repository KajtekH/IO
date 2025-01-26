package com.kajtekH.tests;

import com.kajtekH.model.InputFile;
import com.kajtekH.service.DiffMatchPatchMethod;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;

public class DiffMatchPatchMethodTests {

    @ParameterizedTest
    @NullSource
    public void TestCompare(InputFile candidate)
    {
        try
        {
            DiffMatchPatchMethod diffMatchPatchMethod = new DiffMatchPatchMethod();
            diffMatchPatchMethod.compare(candidate, candidate);
            fail("Wrong data provided");
        }
        catch(IllegalArgumentException e)
        {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
