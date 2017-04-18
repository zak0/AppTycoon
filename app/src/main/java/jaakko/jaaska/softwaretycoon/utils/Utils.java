package jaakko.jaaska.softwaretycoon.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jaakko.jaaska.softwaretycoon.SoftwareTycoonApp;
import jaakko.jaaska.softwaretycoon.R;

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
        Context context = SoftwareTycoonApp.getContext();

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


}
