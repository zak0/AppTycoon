package jaakko.jaaska.apptycoon.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;

/**
 * Created by jaakko on 7.3.2017.
 *
 * Container for various utility methods.
 */

public class Utils {

    private static final String TAG = "Utils";

    /**
     * Selects and returns a random entry from a list.
     * @param list List to select an entry from.
     * @return
     */
    public static Object getRandomFromList(List list) {
        Random r = new Random();
        return list.get(r.nextInt(list.size()));
    }


    public static String millisecondsToTimeString(long milliseconds) {
        // TODO: Custom formatting
        double minutes = Math.floor(milliseconds / 60000);

        double seconds = (double) (milliseconds - minutes * 60000) / 1000;
        seconds = Math.floor(seconds);

        String ret = String.format("%.0fm %.0fs", minutes, seconds);

        return ret;
    }

    /**
     * Takes a large number and prints with a 'power of 1000' suffix added. For example,
     * 12489 becomes 12.49k (with 2 decimals). Numbers less smaller than 1000 will be shown with
     * the specified number of decimals, without a character suffix.
     *
     * @param numberToConvert The large number to convert.
     * @param decimals Number of decimals to show.
     * @return A nicely formatted string presentation of the large number.
     */
    public static String largeNumberToNiceString(double numberToConvert, int decimals) {
        // If the number is large enough for the smallest 'power of 1000', then just
        // use the implementation for numbers of type long.
        if (numberToConvert >= 1000.00d) {
            return largeNumberToNiceString((long) numberToConvert, decimals);
        }

        // Otherwise return the number with desired number of decimals as a string.
        return String.format("%." + decimals + "f", numberToConvert);
    }

    /**
     * Takes a large number and prints with a 'power of 1000' suffix added. For example,
     * 12489 becomes 12.49k (with 2 decimals).
     *
     * @param numberToConvert The large number to print.
     * @param decimals Number of decimals to show.
     * @return A nicely formatted string presentation of the large number.
     */
    public static String largeNumberToNiceString(long numberToConvert, int decimals) {
        String[] suffixes = {"k", "M", "G", "T", "P"};
        String suffix = "";
        double number = 0.0;

        // get a possible "power of 1000" prefix for the number
        for (int i = suffixes.length - 1; i >= 0; i--) {
            if (numberToConvert / Math.pow(1000, i + 1) > 1) {
                number = numberToConvert / Math.pow(1000, i + 1);
                suffix = suffixes[i];
                break;
            }
        }

        //Log.d(TAG, "largeNumberToNiceString() - numberToConvert = " + numberToConvert);
        //Log.d(TAG, "largeNumberToNiceString() - number = " + number);

        // If no "power of 1000" was not found, just use the numberToConvert directly.
        // This happens if the number is less than 1000.
        if (number <= 0.0f) {
            return Long.toString(numberToConvert);
        }

        double residue = number - Math.floor(number);
        residue = residue * Math.pow(10, decimals);
        residue = Math.round(residue) / Math.pow(10, decimals);
        number = Math.floor(number) + residue;

        return String.format("%." + decimals + "f", number) + suffix;
    }


    private static List<String> sFirstNames;
    private static List<String> sLastNames;

    /**
     * Generates a random name from seeds in /res/raw/firstnames.txt and lastnames.txt
     * @return A random name.
     */
    public static String getRandomName() {
        Context context = AppTycoonApp.getContext();

        // Load the seeds if not already loaded.
        if (sFirstNames == null || sLastNames == null) {
            sFirstNames = new ArrayList<>();
            sLastNames = new ArrayList<>();

            // First names
            InputStream in = context.getResources().openRawResource(R.raw.firstnames);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            try {
                line = br.readLine();
                while (line != null) {
                    if (line.length() > 1) {
                        sFirstNames.add(line);
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Last names
            in = context.getResources().openRawResource(R.raw.lastnames);
            br = new BufferedReader(new InputStreamReader(in));
            line = null;
            try {
                line = br.readLine();
                while (line != null) {
                    if (line.length() > 1) {
                        sLastNames.add(line);
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        String ret = getRandomFromList(sFirstNames) + " " + getRandomFromList(sLastNames);
        Log.d(TAG, "new random name: " + ret);
        return ret;
    }

    public static String getRandomCustomer() {


        return "Random Customer Inc.";
    }


}
