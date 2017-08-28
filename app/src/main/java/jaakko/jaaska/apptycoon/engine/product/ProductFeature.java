package jaakko.jaaska.apptycoon.engine.product;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Products consist of ProductFeatures.
 */

public abstract class ProductFeature {

    private static final String TAG = "ProductFeature";

    /** ID of the feature */
    private int mFeatureId;

    /** Human readable name of the feature */
    private String mName;

    /** Flag for telling if this feature can be leveled up. */
    private boolean mCanBeLeveledUp;

    /**
     * This is the "default" constructor. This constructs a ProductFeature that can be leveled
     * up.
     *
     * @param featureId Numeric ID of the feature.
     * @param name Human-readable name of the feature.
     */
    private ProductFeature(int featureId, String name) {
        mFeatureId = featureId;
        mName = name;
        mCanBeLeveledUp = true;
    }

    /**
     * Constructor with the boolean param for toggling is the feature can be leveled up or not.
     *
     * @param featureId Numeric ID of the feature.
     * @param name Human-readable name of the feature.
     * @param canBeLeveledUp True when the feature can be leveled up. False when not.
     */
    private ProductFeature(int featureId, String name, boolean canBeLeveledUp) {
        this(featureId, name);
        mCanBeLeveledUp = canBeLeveledUp;
    }

    /**
     * Return the amount of complexity this feature adds to the product at a specific feature
     * level.
     * @param level Level for which to get the complexity gain.
     * @return Amount of complexity this feature adds to the product.
     */
    public abstract int getComplexityForLevel(int level);

    /**
     * Return the amount of work (or complexity) needed to upgrade this feature from one level
     * to another. By default this is just sequentially upgrading one level at a time. But
     * this can be overridden to provide different kinds of formulas for calculating this.
     * @param fromLevel From which level of the feature to upgrade.
     * @param toLevel To which level to upgrade.
     * @return The amount of complexity that the upgrade costs.
     */
    public int getComplexityForUpgrade(int fromLevel, int toLevel) {
        int ret = 0;
        for (int i = fromLevel + 1; i <= toLevel; i++) {
            ret += getComplexityForLevel(i);
        }
        return ret;
    }

    public int getFeatureId() {
        return mFeatureId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProductFeature)) {
            return false;
        }
        int objId = ((ProductFeature) obj).mFeatureId;
        return mFeatureId == objId;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * Checks if this feature is one that directly gives income.
     *
     * @return True when this is a income feature, false otherwise
     */
    public boolean isIncomeFeature() {
        initStaticContent();
        return sIncomeFeatures.contains(mFeatureId);
    }

    /**
     * @return List of feature IDs of features that directly generate income.
     */
    public static List<Integer> getIncomeFeatureIds() {
        initStaticContent();
        return sIncomeFeatures;
    }

    //
    //
    // Static content
    public static final int PRODUCT_FEATURE_CORE_FEATURES = 1;
    public static final int PRODUCT_FEATURE_UI_ANIMATIONS = 10;
    public static final int PRODUCT_FEATURE_RESPONSIVE_DESIGN = 20;
    public static final int PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION = 30;

    public static final int PRODUCT_FEATURE_BANNER_ADS = 100;
    public static final int PRODUCT_FEATURE_VIDEO_ADS = 110;
    public static final int PRODUCT_FEATURE_IN_APP_PURCHASES = 120;
    public static final int PRODUCT_FEATURE_PAID_SUBSCRIPTIONS = 130;

    private static SparseArray<ProductFeature> sProductFeatures;
    private static List<Integer> sIncomeFeatures; // Features that directly give income.

    public static ProductFeature getFeature(int feature) {
        initStaticContent();
        return sProductFeatures.get(feature);
    }

    public static void initStaticContent() {
        if (sProductFeatures != null && sIncomeFeatures != null) {
            // Init needed only once
            return;
        }

        sProductFeatures = new SparseArray<>();
        sIncomeFeatures = new ArrayList<>();

        // Add income feature IDs to the list.
        sIncomeFeatures.add(PRODUCT_FEATURE_BANNER_ADS);
        sIncomeFeatures.add(PRODUCT_FEATURE_VIDEO_ADS);
        sIncomeFeatures.add(PRODUCT_FEATURE_IN_APP_PURCHASES);
        sIncomeFeatures.add(PRODUCT_FEATURE_PAID_SUBSCRIPTIONS);


        sProductFeatures.append(PRODUCT_FEATURE_CORE_FEATURES, new ProductFeature(PRODUCT_FEATURE_CORE_FEATURES, "Core Functionality") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (1000 + level);
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_UI_ANIMATIONS, new ProductFeature(PRODUCT_FEATURE_UI_ANIMATIONS, "UI Animations") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (1000 + level);
            }

        });

        sProductFeatures.append(PRODUCT_FEATURE_RESPONSIVE_DESIGN, new ProductFeature(PRODUCT_FEATURE_RESPONSIVE_DESIGN, "Responsive Design") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (800 + level);
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION, new ProductFeature(PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION, "Social Media Integration") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (2000 + level * 2);
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_BANNER_ADS, new ProductFeature(PRODUCT_FEATURE_BANNER_ADS, "Banner Ads", false) {
            @Override
            public int getComplexityForLevel(int level) {
                return 5000;
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_VIDEO_ADS, new ProductFeature(PRODUCT_FEATURE_VIDEO_ADS, "Video Ads", false) {
            @Override
            public int getComplexityForLevel(int level) {
                return 9000;
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_IN_APP_PURCHASES, new ProductFeature(PRODUCT_FEATURE_IN_APP_PURCHASES, "In-app Purchases", false) {
            @Override
            public int getComplexityForLevel(int level) {
                return 8000;
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_PAID_SUBSCRIPTIONS, new ProductFeature(PRODUCT_FEATURE_PAID_SUBSCRIPTIONS, "Paid Subcriptions", false) {
            @Override
            public int getComplexityForLevel(int level) {
                return 85000;
            }
        });


    }

}
