package jaakko.jaaska.apptycoon.engine.project;

import jaakko.jaaska.apptycoon.engine.product.ProductFeature;

/**
 * Created by jaakko on 12.5.2017.
 */

public class FeatureTask implements ProjectTask {

    private ProductFeature mFeature;
    private int mStartLevel;
    private int mEndLevel;

    public FeatureTask(ProductFeature feature, int fromLevel, int toLevel) {
        mFeature = feature;
        mStartLevel = fromLevel;
        mEndLevel = toLevel;
    }

    @Override
    public String getTaskTitle() {
        return mFeature.getName() + " to level " + mEndLevel;
    }

    @Override
    public long getQuantity() {
        return mEndLevel - mStartLevel;
    }

    @Override
    public String getTextualQuantity() {
        return "+" + getQuantity();
    }

    @Override
    public long getWorkAmount() {
        return mFeature.getComplexityForUpgrade(mStartLevel, mEndLevel);
    }
}
