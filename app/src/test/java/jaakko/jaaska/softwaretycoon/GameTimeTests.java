package jaakko.jaaska.softwaretycoon;

import static org.junit.Assert.*;
import org.junit.Test;

import jaakko.jaaska.softwaretycoon.engine.time.GameTime;
import jaakko.jaaska.softwaretycoon.engine.time.WorkTime;


/**
 * Created by jaakko on 8.3.2017.
 */

public class GameTimeTests {

    @Test
    public void gametime_toString_looks_good() {
        // By setting hours, days, and months to impossible
        // values, this also tests the cleanup() method.
        GameTime gt = new GameTime(46, 33, 24, 6);
        String expected = "Y8 M1 D4 H22";
        assertEquals(expected, gt.toString());
    }

    @Test
    public void gametime_add_another_gametime() {
        GameTime gt = new GameTime(12, 30, 11, 5);
        GameTime add = new GameTime(13, 1, 2, 3);
        gt.add(add);
        assertEquals("Y9 M2 D2 H1", gt.toString());
    }

    @Test
    public void gametime_add_direct_numbers() {
        GameTime gt = new GameTime(0, 1, 2, 3);
        gt.add(3620000, 1, 2, 3, 4);
        assertEquals("Y7 M5 D3 H2", gt.toString());
    }

    @Test
    public void same_day_and_month_comparison_works() {
        GameTime a = new GameTime(1234, 2, 16, 7, 5);
        GameTime sameAsA = new GameTime(4321, 6, 16, 7, 0);
        GameTime dayDiffFromA = new GameTime(4321, 6, 12, 7, 0);
        GameTime monthDiffFromA = new GameTime(4321, 6, 16, 9, 0);

        boolean[] expected = {true, false, false};
        boolean[] results = {GameTime.sameMonthAndDay(a, sameAsA),
                GameTime.sameMonthAndDay(a, dayDiffFromA),
                GameTime.sameMonthAndDay(a, monthDiffFromA)};
        assertArrayEquals(expected, results);
    }

    @Test
    public void day_of_week_getter_works() {
        GameTime a = new GameTime(1, 1, 1, 1); // Monday
        GameTime b = new GameTime(1, 3, 3, 4); // Tuesday

        assertEquals("Monday", a.getDayOfWeekString(true));
        assertEquals("Mon", a.getDayOfWeekString(false));

        assertEquals("Tuesday", b.getDayOfWeekString(true));
        assertEquals("Tue", b.getDayOfWeekString(false));
    }

    @Test
    public void check_if_time_is_work_time_works() {
        WorkTime workTime = new WorkTime(); // WorkTime with hardcoded defaults defaults.
        GameTime isWorkTime = new GameTime(1234, 10, 4, 6, 12);
        GameTime isNotWorkTime = new GameTime(1234, 19, 4, 6, 12);

        assertEquals(true, workTime.atWork(isWorkTime));
        assertEquals(false, workTime.atWork(isNotWorkTime));
    }

    @Test
    public void check_if_time_is_work_time_works_when_working_time_spans_to_next_day() {
        WorkTime workTime = new WorkTime(); // WorkTime with hardcoded defaults defaults.
        workTime.setDailyWorkTime(GameTime.MONDAY, 22, 8);
        GameTime duringWorkTime = new GameTime(1234, 1, 2, 1, 1);
        GameTime justAfterWorkTime = new GameTime(1234, 6, 3, 1, 1);

        assertEquals(true, workTime.atWork(duringWorkTime));
        assertEquals(false, workTime.atWork(justAfterWorkTime));
    }

}
