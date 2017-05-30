package jaakko.jaaska.softwaretycoon.engine.product;

import android.util.Log;
import android.util.SparseArray;

/**
 * Created by jaakko on 30.4.2017.
 */

public abstract class ProductFeature {

    private static final String TAG = "ProductFeature";

    /** ID of the feature */
    private int mFeatureId;

    /** Human readable name of the feature */
    private String mName;

    private ProductFeature(int featureId, String name) {
        mFeatureId = featureId;
        mName = name;
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

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        int objId = ((ProductFeature) obj).mFeatureId;
        return mFeatureId == objId;
    }

    @Override
    public String toString() {
        return mName;
    }

    //
    //
    // Static content
    public static final int PRODUCT_FEATURE_CORE_FEATURES = 1;
    public static final int PRODUCT_FEATURE_UI_ANIMATIONS = 10;
    public static final int PRODUCT_FEATURE_RESPONSIVE_DESIGN = 20;
    public static final int PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION = 30;

    private static SparseArray<ProductFeature> sProductFeatures;

    public static ProductFeature getFeature(int feature) {
        initStaticContent();
        return sProductFeatures.get(feature);
    }

    public static void initStaticContent() {
        if (sProductFeatures != null) {
            // Init needed only once
            return;
        }

        sProductFeatures = new SparseArray<>();


        sProductFeatures.append(PRODUCT_FEATURE_CORE_FEATURES, new ProductFeature(PRODUCT_FEATURE_CORE_FEATURES, "Core Functionality") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (100 + level);
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_UI_ANIMATIONS, new ProductFeature(PRODUCT_FEATURE_UI_ANIMATIONS, "UI Animations") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (100 + level);
            }

        });

        sProductFeatures.append(PRODUCT_FEATURE_RESPONSIVE_DESIGN, new ProductFeature(PRODUCT_FEATURE_RESPONSIVE_DESIGN, "Responsive Design") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (80 + level);
            }
        });

        sProductFeatures.append(PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION, new ProductFeature(PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION, "Social Media Integration") {
            @Override
            public int getComplexityForLevel(int level) {
                return level * (200 + level * 2);
            }
        });
    }

}
