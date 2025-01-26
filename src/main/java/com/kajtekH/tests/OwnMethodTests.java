package com.kajtekH.tests;

import com.kajtekH.model.InputFile;
import com.kajtekH.service.OwnMethod;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;

public class OwnMethodTests {

    @ParameterizedTest
    @NullSource
    public void TestCompare(InputFile candidate)
    {
        try
        {
            OwnMethod ownMethod = new OwnMethod();
            ownMethod.compare(candidate, candidate);
            fail("Wrong data provided");
        }
        catch(IllegalArgumentException e)
        {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
