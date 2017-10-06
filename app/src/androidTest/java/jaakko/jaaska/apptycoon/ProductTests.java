package jaakko.jaaska.apptycoon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductFeature;
import jaakko.jaaska.apptycoon.engine.product.ProductType;
import jaakko.jaaska.apptycoon.engine.project.ProductDevelopmentProject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests Product related cases.
 */

@RunWith(JUnit4.class)
public class ProductTests {

    private double mDelta = 0.0001; // Error margin for asserting doubles.

    @Before
    public void reset() {
        InstrumentedTestHelper.resetGameStateForTesting();
    }

    @Test
    public void featuresAffectComplexity() {
        Product product = new Product("Test Product", ProductType.getProductType(ProductType.PRODUCT_TYPE_MOBILE_GAME));
        long noFeatures = product.getComplexity();

        ProductFeature feature = ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_BANNER_ADS);
        long featureComplexity = feature.getComplexityForLevel(9);
        product.addFeature(feature, 9);

        long expectedComplexity = noFeatures + featureComplexity;
        assertEquals(expectedComplexity, product.getComplexity());

        product.removeFeature(feature);
        assertEquals(noFeatures, product.getComplexity());
    }

    @Test
    public void inComeFeaturesAffectIncome() {
        // TODO: Income from features not properly implemented yet.
    }

    @Test
    public void featuresAreProperlyAddedAndRemoved() {
        // Check number of features before and after addition.
        Product product = new Product("New Product", ProductType.getProductType(ProductType.PRODUCT_TYPE_MOBILE_APP));
        assertEquals(1, product.getFeatures().size());

        ProductFeature addedFeature = ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION);
        product.addFeature(addedFeature, 3);
        assertEquals(2, product.getFeatures().size());

        // Check number of features before and after removal.
        product.removeFeature(addedFeature);
        assertEquals(1, product.getFeatures().size());
    }

    /**
     * Tests project generation for a new product.
     */
    @Test
    public void developmentProjectGenarationWorksForNewProducts() {
        // Project generation for a new product
        Product product = new Product("New Product", ProductType.getProductType(ProductType.PRODUCT_TYPE_MOBILE_GAME));
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_BANNER_ADS), 1);
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION), 3);
        product.rebuildNewProductDevelopmentProject();

        ProductDevelopmentProject project = product.getDevelopmentProject();
        assertNotEquals(null, project);

        // Currently the work amount for the project is calculated as follows:
        // [base complexity of the product type] * 2 ^ [number of features] = [work amount]
        long expectedWorkAmount = (long) (product.getType().getBaseComplexity() * Math.pow(2, 3));

        assertEquals(expectedWorkAmount, project.getWorkAmount());
    }

    /**
     * Tests that cloning a project for historical storage copies all the right fields and that
     * the project is actually a copy and no fields are references to the "parent" Product object.
     */
    @Test
    public void projectCloningWorks() {
        // Generate a new product.
        Product product = new Product("New Product", ProductType.getProductType(ProductType.PRODUCT_TYPE_MOBILE_GAME));
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_BANNER_ADS), 1);
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION), 3);
        product.rebuildNewProductDevelopmentProject();

        // Then release it
        product.releaseNextVersion();

        // Now the released version should match the original one.
        Product clone = product.getReleasedVersion();
        assertNotNull(clone);
        assertEquals(product.getName(), clone.getName());
        assertEquals(product.getType(), clone.getType());
        assertEquals(product.getFeaturesAsAList().size(), clone.getFeaturesAsAList().size());
        assertEquals(product.getReleaseCount(), clone.getReleaseCount());
        assertEquals(product.getComplexity(), clone.getComplexity());
        assertEquals(product.getQuality(), clone.getQuality());
        assertEquals(product.getIncomePerSecond(), clone.getIncomePerSecond(), mDelta);
        // NOTE: Sub-product copying not tested, as not currently implemented.

        // Next, alter the original one.
        product.setName("Changed Name");
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_PAID_SUBSCRIPTIONS), 5);
        product.addFeature(ProductFeature.getFeature(ProductFeature.PRODUCT_FEATURE_VIDEO_ADS), 6);

        // The clone should stay the same, i.e. no longer match the original one.
        assertNotEquals(product.getName(), clone.getName());
        assertNotEquals(product.getFeaturesAsAList().size(), clone.getFeaturesAsAList().size());
        assertNotEquals(product.getComplexity(), clone.getComplexity());
        assertNotEquals(product.getIncomePerSecond(), clone.getIncomePerSecond(), mDelta);
    }

    /**
     * Tests project generation for a new version of an existing product.
     */
    @Test
    public void developmentProjectGenarationWorksForNewVersions() {
        // TODO: Not yet implemented into the game

    }
}
