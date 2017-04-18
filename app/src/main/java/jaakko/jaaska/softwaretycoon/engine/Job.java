package jaakko.jaaska.softwaretycoon.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaakko on 26.3.2017.
 */

public class Job {

    private static final String TAG = "Job";

    private JobType mType;
    private String mTitle;
    private long mBaseSalary;

    // Skill requirements
    private int mReqTechSkill;
    private int mReqDesignSkill;
    private int mReqBusinessSkill;
    private int mReqPeopleSkill;

    public Job(String title, JobType type) {
        mTitle = title;
        mType = type;
        mBaseSalary = 0l;
    }

    public Job(String title, JobType type, long baseSalary, int techSkill, int designSkill, int businessSkill, int peopleSkill) {
        mTitle = title;
        mType = type;
        mBaseSalary = baseSalary;
        mReqTechSkill = techSkill;
        mReqDesignSkill = designSkill;
        mReqBusinessSkill = businessSkill;
        mReqPeopleSkill = peopleSkill;
    }

    public void setBaseSalary(long baseSalary) {
        mBaseSalary = baseSalary;
    }

    public void setSkillRequirements(int techSkill, int designSkill, int businessSkill, int peopleSkill) {
        mReqTechSkill = techSkill;
        mReqDesignSkill = designSkill;
        mReqBusinessSkill = businessSkill;
        mReqPeopleSkill = peopleSkill;
    }

    public long getBaseSalary() {
        return mBaseSalary;
    }

    public int[] getSkillRequirements() {
        int[] ret = {mReqTechSkill, mReqDesignSkill, mReqBusinessSkill, mReqPeopleSkill};
        return ret;
    }

    public int getRequiredTechSkill() {
        return mReqTechSkill;
    }

    public int getRequiredDesignSkill() {
        return mReqDesignSkill;
    }

    public int getRequiredBusinessSkill() {
        return mReqBusinessSkill;
    }

    public int getRequiredPeopleSkill() {
        return mReqPeopleSkill;
    }

    public enum JobType {
        DEVELOPMENT,
        TESTING,
        MANAGEMENT,
        SALES,
        HR,
        IT,
        CUSTOMER_SUPPORT,
        FACILITIES,
        CEO
    }


    //
    //
    // Static
    //
    private static List<Job> sAllJobs;

    private static void initStaticData() {
        // Init only once.
        if (sAllJobs != null) {
            Log.e(TAG, "Tried to init statics again.");
            return;
        }

        sAllJobs = new ArrayList<>();

        sAllJobs.add(new Job("Application Developer", JobType.DEVELOPMENT, 2300, 5, 0, 0, 0));
        sAllJobs.add(new Job("Application Support Analyst", JobType.CUSTOMER_SUPPORT, 2400, 5, 0, 1, 2));
        sAllJobs.add(new Job("Applications Engineer", JobType.DEVELOPMENT, 2700, 6, 2, 0, 0));
        sAllJobs.add(new Job("Associate Developer", JobType.DEVELOPMENT, 2900, 7, 2, 1, 0));
        sAllJobs.add(new Job("Chief Executive Officer (CEO)", JobType.MANAGEMENT, 8200, 20, 20, 20, 20));
        sAllJobs.add(new Job("Chief Technology Officer (CTO)", JobType.MANAGEMENT, 6700, 20, 20, 10, 20));
        sAllJobs.add(new Job("Chief Information Officer (CIO)", JobType.MANAGEMENT, 6700, 15, 20, 15, 20));
        sAllJobs.add(new Job("Computer and Information Systems Manager", JobType.IT, 4100, 10, 1, 5, 10));
        //sAllJobs.add(new Job("Computer Systems Manager"
        sAllJobs.add(new Job("Customer Support Administrator", JobType.CUSTOMER_SUPPORT, 2100, 5, 1, 0, 10));
        sAllJobs.add(new Job("Customer Support Specialist", JobType.CUSTOMER_SUPPORT, 1700, 3, 0, 0, 6));
        //sAllJobs.add(new Job("Data Center Support Specialist"
        //sAllJobs.add(new Job("Data Quality Manager"
        sAllJobs.add(new Job("Database Administrator", JobType.DEVELOPMENT, 2250, 5, 0, 0, 0));
        sAllJobs.add(new Job("Desktop Support Manager", JobType.CUSTOMER_SUPPORT, 2200, 5, 0, 0, 6));
        sAllJobs.add(new Job("Desktop Support Specialist", JobType.CUSTOMER_SUPPORT, 1700, 3, 0, 0, 6));
        sAllJobs.add(new Job("Developer", JobType.DEVELOPMENT, 2300, 5, 0, 0, 0));

        /*
        sAllJobs.add(new Job("Director of Technology"
        sAllJobs.add(new Job("Front End Developer"
        sAllJobs.add(new Job("Help Desk Specialist"
        sAllJobs.add(new Job("Help Desk Technician"
        sAllJobs.add(new Job("Information Technology Coordinator"
        sAllJobs.add(new Job("Information Technology Director"
        sAllJobs.add(new Job("Information Technology Manager"
        sAllJobs.add(new Job("IT Support Manager"
        sAllJobs.add(new Job("IT Support Specialist"
        sAllJobs.add(new Job("IT Systems Administrator"
        sAllJobs.add(new Job("Java Developer"
        sAllJobs.add(new Job("Junior Software Engineer"
        sAllJobs.add(new Job("Management Information Systems Director"
        sAllJobs.add(new Job(".NET Developer"
        sAllJobs.add(new Job("Network Architect"
        sAllJobs.add(new Job("Network Engineer"
        sAllJobs.add(new Job("Network Systems Administrator"
        sAllJobs.add(new Job("Programmer"
        sAllJobs.add(new Job("Programmer Analyst"
        sAllJobs.add(new Job("Security Specialist"
        sAllJobs.add(new Job("Senior Applications Engineer"
        sAllJobs.add(new Job("Senior Database Administrator"
        sAllJobs.add(new Job("Senior Network Architect"
        sAllJobs.add(new Job("Senior Network Engineer"
        sAllJobs.add(new Job("Senior Network System Administrator"
        sAllJobs.add(new Job("Senior Programmer"
        sAllJobs.add(new Job("Senior Programmer Analyst"
        sAllJobs.add(new Job("Senior Security Specialist"
        sAllJobs.add(new Job("Senior Software Engineer"
        sAllJobs.add(new Job("Senior Support Specialist"
        sAllJobs.add(new Job("Senior System Administrator"
        sAllJobs.add(new Job("Senior System Analyst"
        sAllJobs.add(new Job("Senior System Architect"
        sAllJobs.add(new Job("Senior System Designer"
        sAllJobs.add(new Job("Senior Systems Analyst"
        sAllJobs.add(new Job("Senior Systems Software Engineer"
        sAllJobs.add(new Job("Senior Web Administrator"
        sAllJobs.add(new Job("Senior Web Developer"
        sAllJobs.add(new Job("Software Architect"
        sAllJobs.add(new Job("Software Developer"
        sAllJobs.add(new Job("Software Engineer"
        sAllJobs.add(new Job("Software Quality Assurance Analyst"
        sAllJobs.add(new Job("Support Specialist"
        sAllJobs.add(new Job("Systems Administrator"
        sAllJobs.add(new Job("Systems Analyst"
        sAllJobs.add(new Job("System Architect"
        sAllJobs.add(new Job("Systems Designer"
        sAllJobs.add(new Job("Systems Software Engineer"
        ​sAllJobs.add(new Job("Technical Operations Officer"
        sAllJobs.add(new Job("Technical Support Engineer"
        sAllJobs.add(new Job("Technical Support Specialist"
        sAllJobs.add(new Job("Technical Specialist"
        sAllJobs.add(new Job("Telecommunications Specialist"
        sAllJobs.add(new Job("Web Administrator"
        sAllJobs.add(new Job("Web Developer"
        sAllJobs.add(new Job("Webmaster"
         */

    }

    public static List<Job> getAllJobs() {
        initStaticData();
        return sAllJobs;
    }
}
