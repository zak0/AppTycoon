package jaakko.jaaska.softwaretycoon.engine.project;

/**
 * One phase in the development life cycle of a development model.
 *
 * Setter methods return this to allow nice chaining when defining phases.
 * Created by jaakko on 15.4.2017.
 */

public class DevPhase {

    private DevModel mModel; // Model this phase is part of.
    private String mName;
    private DevPhase mNextPhase;

    /** The ratio of work of entire project that is done at this phase of the dev process.
     * Actual fraction of the work is then
     * [work ratio of a phase] / [sum of all workRatios of all phases of the model].
     */
    private int mWorkRatio;

    //
    // Skill effect ratios for the work done at this phase.
    private int mTechSkillEffect;
    private int mDesignSkillEffect;
    private int mBusinessSkillEffect;
    private int mPeopleSkillEffect;

    public DevPhase(DevModel model, String name, int workRatio) {
        mModel = model;
        mName = name;
        mWorkRatio = workRatio;
    }

    public DevPhase setNextPhase(DevPhase nextPhase) {
        mNextPhase = nextPhase;
        return this;
    }

    public DevPhase setSkillEffectRatios(int tech, int design, int business, int people) {
        mTechSkillEffect = tech;
        mDesignSkillEffect = design;
        mBusinessSkillEffect = business;
        mPeopleSkillEffect = people;
        return this;
    }

    /**
     * Calculates the actual fractional part (percentage divided by 100.0) of the entire project
     * work that is done in this phase.
     * @return
     */
    public double getWorkFraction() {
        // TODO: WIP
        return 0.0;
    }

    public DevPhase getNextPhase() {
        return mNextPhase;
    }

}
