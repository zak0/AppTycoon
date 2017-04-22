package jaakko.jaaska.softwaretycoon.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.engine.people.Employee;
import jaakko.jaaska.softwaretycoon.engine.time.GameTime;

/**
 * Created by jaakko on 7.3.2017.
 */

public class Company {

    private String mName;
    private int mReputation;
    private long mValue;
    private long mFunds;

    private Employee mFounder;
    private List<Employee> mEmployees;

    // Cumulative skills of all the employees.
    private long mCumulativeTechSkill;
    private long mCumulativeDesignSkill;
    private long mCumulativeBusinessSkill;
    private long mCumulativePeopleSkill;

    // Cumulative language specific developer tech skills.
    private Map<Integer, Long> mCumulativeLanguageTechSkills;

    public Company(String name, int reputation, long value, long funds) {
        mName = name;
        mReputation = reputation;
        mValue = value;
        mFunds = funds;

        mCumulativeTechSkill = 0;
        mCumulativeDesignSkill = 0;
        mCumulativeBusinessSkill = 0;
        mCumulativePeopleSkill = 0;

        mEmployees = new ArrayList<>();
        mCumulativeLanguageTechSkills = new HashMap<>();



        // Prepopulate the cumulative language specific tech skill map.
        for (Language lang : Language.getAllLanguages()) {
            mCumulativeLanguageTechSkills.put(lang.getId(), 0l);
        }
    }

    public String getName() {
        return mName;
    }

    public int getReputation() {
        return mReputation;
    }

    public long getValue() {
        return mValue;
    }

    public long getFunds() {
        return mFunds;
    }

    /**
     * Adds a new employee into the company. Takes care of handling changes in monthly expenses
     * as well as changes in company productivity. So this is what to call when adding a new
     * employee to the company.
     *
     * @param employee Employee to add.
     */
    public void addEmployee(Employee employee) {
        GameTime now = GameEngine.getInstance().getGameState().getGameTime();

        mEmployees.add(employee);

        // Add employee skills into company cumulative skills.
        int[] skills = employee.getSkills();
        mCumulativeTechSkill += skills[0];
        mCumulativeDesignSkill += skills[1];
        mCumulativeBusinessSkill += skills[2];
        mCumulativePeopleSkill += skills[3];

        // Add programming language specific skills into the map.
        int newSkill;
        int lang = -1;
        int proficiency = -1;
        for (int i = 0; i < employee.getLanguageSkills().size(); i++) {
            // The list is a SparseArray where indices are language IDs.
            lang = employee.getLanguageSkills().keyAt(i);
            proficiency = employee.getLanguageSkills().valueAt(i);

            // Get the language specific tech skill.
            // Language specific skill is always at least one.
            newSkill = (int) (skills[0] * Language.getProficiencyFactor(proficiency));
            newSkill = newSkill <= 0 ? 1 : newSkill;

            mCumulativeLanguageTechSkills.put(lang,
                    mCumulativeLanguageTechSkills.get(lang) + newSkill); // Incremented skill value
        }

        // Set hire date to now.
        employee.setHireDate(now);
    }
}
