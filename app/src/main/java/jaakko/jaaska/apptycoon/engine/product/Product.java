package jaakko.jaaska.apptycoon.engine.product;

import android.support.v4.util.Pair;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * TODO: Refactor the ProductFeatures to be similar to EmployeeType: instance specific data (like level) into ProductFeature class, Product to have its features as List<ProductFeature>.
 */

public class Product {

    private static final String TAG = "Product";

    private String mName;

    /** Number of releases of this product */
    private int mReleaseCount;

    /** The main type */
    private ProductType mType;

    /** Features and their current levels */
    private List<Pair<ProductFeature, Integer>> mFeatures;

    /** Sub-products */
    private List<Product> mSubProducts;

    /** Calculated complexity of the product */
    private long mComplexity;

    /** Current quality of the product */
    private long mQuality;

    /** Money made from one sold unit */
    private int mUnitPrice;


    private long mUnitsSold;
    private long mMoneyMade;

    public Product(String name, ProductType type) {
        mName = name;
        mType = type;
        mReleaseCount = 0;
        mComplexity = 0;
        mQuality = 0;

        mUnitPrice = 5;
        mUnitsSold = 0;
        mMoneyMade = 0;

        mFeatures = new ArrayList<>();
        mSubProducts = new ArrayList<>();

        // Every product has the "Core functionality" feature. So, we add it here.
        ProductFeature coreFeatures = ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_CORE_FEATURES);
        mFeatures.add(new Pair<>(coreFeatures, 1));

        calculateComplexity();
    }

    /**
     * Calculates the complexity of the product and stores it into {@link #mComplexity}.
     * This should be called always after the product is changed so that the complexity potentially
     * changes as a result.
     * @return The calculated complexity
     */
    private long calculateComplexity() {
        mComplexity = mType.getBaseComplexity();

        // Re-calculate complexity also for subproducts.
        for (Product subProduct : mSubProducts) {
            mComplexity += subProduct.calculateComplexity();
        }

        // Add complexity effect of features.
        for (Pair<ProductFeature, Integer> featureAndLevel : mFeatures) {
            mComplexity += featureAndLevel.first.getComplexityForLevel(featureAndLevel.second);
        }

        return mComplexity;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getQuality() {
        return mQuality;
    }

    public List<Pair<ProductFeature, Integer>> getFeatures() {
        return mFeatures;
    }

    /**
     * Searches the feature from List<Pair<ProductFeature, Integer>> and
     * gets its level.
     *
     * TODO: Remove this method when ProductFeature is refactored.
     *
     * @param feature Feature to look the level for.
     * @return The level of the feature. -1 in case the feature was not found.
     */
    public int getLevelOfAFeature(ProductFeature feature) {
        for (Pair<ProductFeature, Integer> pair : mFeatures) {
            if (pair.first.equals(feature)) {
                return pair.second;
            }
        }

        return -1;
    }

    /**
     * Builds a single list of all the features.
     *
     * TODO: Remove this method when ProductFeature is refactored.
     * @return The list with all the features.
     */
    public List<ProductFeature> getFeaturesAsAList() {
        ArrayList<ProductFeature> features = new ArrayList<>();

        for (Pair<ProductFeature, Integer> pair : mFeatures) {
            features.add(pair.first);
        }

        return features;
    }

    public ProductType getType() {
        return mType;
    }

    public long getComplexity() {
        return mComplexity;
    }

    /**
     * Add or upgrade a feature for a product. If the feature already exists for the product, it
     * will be replaced with the new one with the given level.
     *
     * @param feature Feature to add or replace
     * @param level Level of the feature
     */
    public void addFeature(ProductFeature feature, int level) {
        Log.d(TAG, "addFeature() - adding '" + feature.getName() + "', level " + level);

        //for (Pair<ProductFeature, Integer> pair : mFeatures) {
        Iterator<Pair<ProductFeature, Integer>> iter = mFeatures.iterator();
        while (iter.hasNext()) {
            Pair<ProductFeature, Integer> pair = iter.next();
            ProductFeature existingFeature = pair.first;
            if (feature.equals(existingFeature)) {
                Log.d(TAG, "addFeature() - removed existing '" + existingFeature + "'");
                iter.remove();
            }
        }
        mFeatures.add(new Pair<ProductFeature, Integer>(feature, level));
        calculateComplexity();
    }

    /**
     * Remove a feature from the product.
     *
     * @param feature Feature to remove.
     */
    public void removeFeature(ProductFeature feature) {

        Pair<ProductFeature, Integer> pairToRemove = null;

        for (Pair<ProductFeature, Integer> pair : mFeatures) {
            if (pair.first.equals(feature)) {
                pairToRemove = pair;
                break;
            }
        }

        if (pairToRemove != null) {
            mFeatures.remove(pairToRemove);
            Log.d(TAG, "removeFeature() - removed feature '" + pairToRemove.first.getName() + "'");
        } else {
            Log.e(TAG, "removeFeature() - tried to remove a non-existing feature");
        }

        calculateComplexity();

    }



    //
    //
    // Static utility methods
    //
    //

    /**
     * Generates a random product.
     *
     * @return Generated product
     *
     */
    public static Product getRandomProduct() {
        // Get a random type that is suitable to be a parent.
        ProductType type = ProductType.getRandomProductType();
        while (type.mustHaveParent()) {
            type = ProductType.getRandomProductType();
        }
        Product product = new Product("noname", type);

        Random r = new Random();

        // Set a random set of features for the product.
        List<ProductFeature> features = type.getPossibleFeatures();
        int numOfFeatures = r.nextInt(features.size() + 1);

        Log.d(TAG, "getRandomProduct() - type has " + features.size() + " features.");
        Log.d(TAG, "getRandomProduct() - add " + numOfFeatures + " features.");

        for (int i = 0; i < numOfFeatures; i++) {
            // Feature level is between 1 and 5.
            int featureLevel = r.nextInt(4) + 1;
            ProductFeature feature = features.get(r.nextInt(features.size()));
            product.addFeature(feature, featureLevel);
        }

        product.calculateComplexity();

        return product;
    }

}
