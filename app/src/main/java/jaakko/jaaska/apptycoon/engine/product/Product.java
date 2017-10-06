package jaakko.jaaska.apptycoon.engine.product;

import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jaakko.jaaska.apptycoon.engine.project.FeatureTask;
import jaakko.jaaska.apptycoon.engine.project.NewProductTask;
import jaakko.jaaska.apptycoon.engine.project.ProductDevelopmentProject;

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

    /** The version of this product currently in distribution */
    private Product mReleasedVersion;

    /** Calculated complexity of the product */
    private long mComplexity;

    /** Current quality of the product */
    private long mQuality;

    /** Number of active users the product has */
    private long mUsers;

    /** Number of all-time downloads (or purchases for a paid product) */
    private long mDownloads;

    /** Money made from one sold unit */
    private int mUnitPrice;

    /** Amount of money the product has earned all-time */
    private long mTotalEarnings;

    /**
     * Array of all the features that directly generate income.
     * Key of the array is the ID of the feature.
     *
     * All income values are money per second per active user.
     */
    private SparseArray<Double> mFeatureIncomes;

    /** The development project for the next release. This will be null until the next version
     * is defined. Be sure to nullify this after the project is done!
     */
    private ProductDevelopmentProject mDevProject;

    public Product(String name, ProductType type) {
        mName = name;
        mType = type;
        mReleaseCount = 0;
        mComplexity = 0;
        mQuality = 0;

        mUnitPrice = 0;
        mDownloads = 0;
        mTotalEarnings = 0;
        mUsers = 1288; // TODO: Set to something for dev purposes

        mFeatures = new ArrayList<>();
        mFeatureIncomes = new SparseArray<>();
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

    /**
     * Takes care of all the housekeeping needed for releasing the next version.
     */
    public void releaseNextVersion() {
        Log.d(TAG, "releaseNextVersion()");
        mReleaseCount++;
        mDevProject = null;
        mReleasedVersion = cloneForStorage();
    }

    /**
     * Returns the version that's been released and currently in distribution.
     *
     * @return Product object representing the version that's released
     */
    public Product getReleasedVersion() {
        Log.d(TAG, "getReleasedVersion() - null? " + (mReleasedVersion == null));
        return mReleasedVersion;
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

    public int getReleaseCount() {
        return mReleaseCount;
    }

    public ProductDevelopmentProject getDevelopmentProject() {
        return mDevProject;
    }

    public void setDevelopmentProject(ProductDevelopmentProject project) {
        mDevProject = project;
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

        // Use iterator instead of a foreach loop in order to allow removing items from the list
        // while iterating through it.
        Iterator<Pair<ProductFeature, Integer>> iterator = mFeatures.iterator();
        while (iterator.hasNext()) {
            Pair<ProductFeature, Integer> pair = iterator.next();
            ProductFeature existingFeature = pair.first;
            if (feature.equals(existingFeature)) {
                Log.d(TAG, "addFeature() - removed existing '" + existingFeature + "'");
                iterator.remove();
            }
        }
        mFeatures.add(new Pair<>(feature, level));

        // In case of a feature that gives income (ads, in-app purchases, subscriptions, ...),
        // handle the change in income for this product.
        if (feature.isIncomeFeature()) {
            handleIncomeFeature(feature);
        }

        // Recalculate the complexity of this product.
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

    /**
     * Handles the change in income by incrementing the correct income value for a given income
     * feature.
     *
     * Call this method with the feature giving income whenever it is added.
     *
     * @param feature A feature that causes the change in income. This should be an income feature.
     */
    private void handleIncomeFeature(ProductFeature feature) {
        if (!feature.isIncomeFeature()) {
            Log.d(TAG, "handleIncomeFeature() - feature id " + feature.getFeatureId() + " is not" +
                    "an income feature -> did nothing");
            return;
        }

        // TODO: Get the correct income for the feature per second per user. Now it's just 0.001 * [level of the feature].
        mFeatureIncomes.append(feature.getFeatureId(), (double) getLevelOfAFeature(feature) * 0.001d);
    }

    /**
     * Returns the total income this product currently generates per second.
     *
     * @return Income per second
     */
    public double getIncomePerSecond() {
        double sum = 0.0f;

        //
        // Add income from features that generate income.

        // Check for all the income features if this product has them.
        for (int i : ProductFeature.getIncomeFeatureIds()) {
            Double income = mFeatureIncomes.get(i);

            // Variable 'income' is now null if this product did not have the feature.
            sum += (income == null ? 0.0f : income) * (double) mUsers;
        }

        return sum;
    }

    /**
     * Rebuilds the development project for a new product based on the type and
     * features of this product.
     *
     * Call this when the product or its features change during configuration of
     * a new product.
     *
     * If the project is not already created, this creates it.
     */
    public void rebuildNewProductDevelopmentProject() {
        if (mDevProject == null) {
            Log.d(TAG, "rebuildNewProductDevelopmentProject() - new project created");
            mDevProject = new ProductDevelopmentProject("no description", this);
        }
        Log.d(TAG, "rebuildNewProductDevelopmentProject() - start");

        // Set the project description to include the product name.
        mDevProject.setDescription(getName());

        mDevProject.removeAllTasks();

        // Add the task for the initial product development.
        mDevProject.addTask(new NewProductTask(this));

        // Add tasks for features.
        for (Pair<ProductFeature, Integer> pair : mFeatures) {
            ProductFeature feature = pair.first;
            int level = pair.second;

            FeatureTask featureTask = new FeatureTask(feature, 0, level);
            mDevProject.addTask(featureTask);
        }

        // Recalculate the work amount for the project.
        mDevProject.updateWorkAmount();

        Log.d(TAG, "rebuildNewProductDevelopmentProject() - work amount of the project is " +
                mDevProject.getWorkAmount());
    }

    /**
     * Clones the current Product object and stores all the information, that is necessary to
     * be stored (for keeping track of different release versions of the same product), into a
     * new object.
     *
     * @return A new object with all the necessary fields copied.
     */
    private Product cloneForStorage() {
        Log.d(TAG, "cloneForStorage()");
        Product ret = new Product(mName, mType);

        ret.mReleaseCount = mReleaseCount;
        ret.mComplexity = mComplexity;
        ret.mQuality = mQuality;
        ret.mUnitPrice = mUnitPrice;
        ret.mFeatures = new ArrayList<>(mFeatures);
        ret.mFeatureIncomes = mFeatureIncomes.clone();
        ret.mSubProducts = new ArrayList<>(mSubProducts);

        return ret;
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
