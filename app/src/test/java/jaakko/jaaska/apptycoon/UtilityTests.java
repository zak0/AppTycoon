package jaakko.jaaska.apptycoon;

import static org.junit.Assert.*;
import org.junit.Test;

import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Test cases for simple utilities implemented in {@see jaakko.jaaska.apptycoon.utils.Utils}.
 */

public class UtilityTests {

    @Test
    public void numberToNiceStringConverterWorks() {
        // Tests with longs
        assertEquals("123.457M", Utils.largeNumberToNiceString(123456789, 3));

        // Tests with doubles
        assertEquals("0.89", Utils.largeNumberToNiceString(0.888888888888, 2));
        assertEquals("14", Utils.largeNumberToNiceString(14.33338888, 0));

    }



}
