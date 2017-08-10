package jaakko.jaaska.apptycoon;

        import static org.junit.Assert.*;
        import org.junit.Test;

        import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Created by jaakko on 8.3.2017.
 */

public class UtilityTests {

    @Test
    public void number_to_nice_string_converter_works() {
        assertEquals("123,457M", Utils.largeNumberToNiceString(123456789, 3));
    }



}
