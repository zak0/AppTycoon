package jaakko.jaaska.softwaretycoon.engine;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 2.4.2017.
 *
 * Container and getter for programming language name related static data.
 */

public class Language {

    public static final int PROFICIENCY_JUNIOR = 1;
    public static final int PROFICIENCY_INTERMEDIATE = 2;
    public static final int PROFICIENCY_SENIOR = 3;

    public static final int JAVA            = 1;
    public static final int C               = 2;
    public static final int JAVASCRIPT      = 3;
    public static final int CPP             = 4;
    public static final int SWIFT           = 5;
    public static final int OBJECTIVE_C     = 6;
    public static final int PYTHON          = 7;

    private static List<Language> sLangs = null;
    private static List<Integer> sProficiencies = null;
    private static SparseArray<Double> sProficiencyFactors = null;

    private String mName = "";
    private int mLang = 0;

    private Language(String name, int language) {
        mName = name;
        mLang = language;
    }

    /**
     * Initializes the static programming language data.
     */
    private static void initStaticData() {
        // Init only once.
        if (sLangs != null && sProficiencies != null) {
            return;
        }

        // Proficiency levels
        sProficiencies = new ArrayList<>();
        sProficiencies.add(PROFICIENCY_JUNIOR);
        sProficiencies.add(PROFICIENCY_INTERMEDIATE);
        sProficiencies.add(PROFICIENCY_SENIOR);

        // Proficiency factors, i.e. how much each proficiency
        // affects the tech skill of a developer during project
        // implementation phases.
        sProficiencyFactors = new SparseArray<>();
        sProficiencyFactors.append(PROFICIENCY_JUNIOR, 0.5);
        sProficiencyFactors.append(PROFICIENCY_INTERMEDIATE, 1.0);
        sProficiencyFactors.append(PROFICIENCY_SENIOR, 1.5);

        sLangs = new ArrayList<>();
        sLangs.add(new Language("Java", JAVA));
        sLangs.add(new Language("C", C));
        sLangs.add(new Language("C++", CPP));
        sLangs.add(new Language("JavaScript", JAVASCRIPT));
    }

    /**
     * @return The number of different defined languages.
     */
    public static int getLanguageCount() {
        initStaticData();
        return sLangs.size();
    }

    /**
     * @return A random proficiency level of all the possible ones.
     */
    public static int getRandomProficiency() {
        initStaticData();
        return (int) Utils.getRandomFromList(sProficiencies);
    }

    /**
     *
     * @return List of all the languages as Language objects.
     */
    public static List<Language> getAllLanguages() {
        initStaticData();
        return sLangs;
    }

    /**
     *
     * @return A random Language of all the specified languages.
     */
    public static int getRandomLanguage() {
        initStaticData();
        return (int) Utils.getRandomFromList(sLangs);
    }

    /**
     * Returns the proficiency factor of given proficiency level.
     * @param proficiency
     * @return
     */
    public static double getProficiencyFactor(int proficiency) {
        return sProficiencyFactors.get(proficiency);
    }

    /**
     * Gets the name for a specific language.
     * @param language
     * @return
     */
    public static String getLanguageName(int language) {
        initStaticData();

        for (Language l : sLangs) {
            if (l.mLang == language) {
                return l.mName;
            }
        }

        return null;
    }

    /**
     * @return The numeric identifier of the language.
     */
    public int getId() {
        return mLang;
    }

    /**
     * @return The name of the language.
     */
    public String getName() {
        return mName;
    }
}
