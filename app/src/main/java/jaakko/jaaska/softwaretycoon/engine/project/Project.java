package jaakko.jaaska.softwaretycoon.engine.project;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.softwaretycoon.engine.Language;

/**
 * Created by jaakko on 26.3.2017.
 */

public class Project {

    private ProjectType mType;
    private List<ProjectType> mSubTypes;
    private String mName;
    private String mDescription;

    /** Customer is null for in-house projects. */
    private String mCustomer;

    /** Languages the project will be implemented with. */
    private List<Language> mLanguages;

    /** Project milestones. Project has always atleas one milestone (the 100% completion one). */
    private List<ProjectMilestone> mMilestones;

    private int mRequiredFunctionality;
    private int mRequiredQuality;
    private int mFunctionality;
    private int mQuality;

    /**
     * Every public constructor should call this constructor first.
     * This constructor initializes lists and other variables that need initializing.
     */
    private Project() {
        mSubTypes = new ArrayList<>();
        mLanguages = new ArrayList<>();
        mMilestones = new ArrayList<>();

        mFunctionality = 0;
        mQuality = 0;
    }

    /**
     * Constructor for in-house projects.
     * @param name
     * @param description
     * @param type
     */
    public Project(String name, String description, ProjectType type) {
        this();
        mName = name;
        mDescription = description;
        mType = type;
        mCustomer = null;
    }

    /**
     * Constructor for customer projects.
     * @param name
     * @param description
     * @param type
     */
    public Project(String name, String description, ProjectType type, String customer) {
        this();
        mName = name;
        mDescription = description;
        mType = type;
        mCustomer = customer;
    }

    /**
     * Set project requirements.
     *
     * @param functionality
     * @param quality
     * @return This
     */
    public Project setRequirements(int functionality, int quality) {
        mRequiredFunctionality = functionality;
        mRequiredQuality = quality;
        return this;
    }

    public enum ProjectType {
        INTERNAL_DEVELOPMENT,
        WEB_APP,
        SERVER_BACKEND,
        MOBILE_APP,
        BROWSER_GAME,
        MOBILE_GAME,
        PC_APPLICATION,
        PC_GAME,
        CONSOLE_GAME,
        IT_SYSTEM,
        CLOUD_SOLUTION,
        ARTIFICIAL_INTELLIGENCE
    }

    public static String getRandomCustomer() {
        return "Random Customer Inc.";
    }

}
