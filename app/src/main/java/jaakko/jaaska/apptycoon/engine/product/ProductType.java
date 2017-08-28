package jaakko.jaaska.apptycoon.engine.product;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Created by jaakko on 30.4.2017.
 */

public class ProductType {

    /** Type ID of the type */
    private int mType;

    /** Human readable name of the product type */
    private String mName;

    /** Flag for telling if this type can only exist as a child. */
    private boolean mMustHaveParent;

    /** Base complexity of the type */
    private long mBaseComplexity;

    /** Possible subtypes for this type. */
    private List<ProductType> mPossibleSubTypes;

    /** If this list has entries, it means that a product of this type cannot
     * be a product without sub-products of at least these sub-types. */
    private List<ProductType> mRequiredSubTypes;

    /** List of features a product of this type can have. */
    private List<ProductFeature> mPossibleFeatures;

    /** Constructor is private as instances are only built once by
     * the {@link #initStaticContent()} method.
     * @param type Type ID of the type
     * @param name Human readable name of the type
     */
    private ProductType(int type, String name, long baseComplexity) {
        mName = name;
        mPossibleSubTypes = new ArrayList<>();
        mRequiredSubTypes = new ArrayList<>();
        mPossibleFeatures = new ArrayList<>();
        mMustHaveParent = false;
        mBaseComplexity = baseComplexity;
        mType = type;

        addTypeToArrays(type, this);
    }

    /**
     * This is the constructor called for subtypes as this includes
     * the mustHaveParent parameter.
     *
     * @param type Type ID of the type
     * @param name Human readable name of the type
     * @param mustHaveParent True when this type cannot be the parent type on its own
     * @param baseComplexity Minimum amount of complexity a product of this type has
     */
    private ProductType(int type, String name, boolean mustHaveParent, long baseComplexity) {
        this(type, name, baseComplexity);
        mMustHaveParent = mustHaveParent;
    }

    public boolean mustHaveParent() {
        return mMustHaveParent;
    }

    public String getName() {
        return mName;
    }

    private ProductType addPossibleSubType(ProductType subType) {
        mPossibleSubTypes.add(subType);
        return this;
    }

    private ProductType addRequiredSubType(ProductType subType) {
        mRequiredSubTypes.add(subType);
        return this;
    }

    private ProductType addPossibleFeature(int featureId) {
        ProductFeature feature = ProductFeature.getFeature(featureId);
        mPossibleFeatures.add(feature);
        return this;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String toString() {
        return mName;
    }

    public long getBaseComplexity() {
        return mBaseComplexity;
    }

    public List<ProductFeature> getPossibleFeatures() {
        return mPossibleFeatures;
    }

    //
    //
    // Static content.
    public static final int PRODUCT_TYPE_WEB_APP = 10;
    public static final int PRODUCT_TYPE_MOBILE_APP = 20;
    public static final int PRODUCT_TYPE_MOBILE_GAME = 25;
    public static final int PRODUCT_TYPE_DESKTOP_APP = 30;
    public static final int PRODUCT_TYPE_SERVER_BACKEND = 40;

    private static SparseArray<ProductType> sProductTypes;
    private static ArrayList<Integer> sProductTypeIds;

    public static ProductType getProductType(int type) {
        initStaticContent();
        return sProductTypes.get(type);
    }

    private static void initStaticContent() {
        if (sProductTypes != null) {
            // Don't init statics more than once.
            return;
        }

        sProductTypes = new SparseArray<>();
        sProductTypeIds = new ArrayList<>();

        ProductType webApp = new ProductType(PRODUCT_TYPE_WEB_APP, "Web App", 3000);
        ProductType mobileApp = new ProductType(PRODUCT_TYPE_MOBILE_APP, "Mobile App", 4000);
        ProductType mobileGame = new ProductType(PRODUCT_TYPE_MOBILE_GAME, "Mobile Game", 4500);
        ProductType desktopApp = new ProductType(PRODUCT_TYPE_DESKTOP_APP, "Desktop App", 5000);
        ProductType serverBackend = new ProductType(PRODUCT_TYPE_SERVER_BACKEND, "Server Backend", true, 2000);

        webApp.addPossibleSubType(serverBackend)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_RESPONSIVE_DESIGN)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_UI_ANIMATIONS);

        mobileApp.addPossibleSubType(serverBackend)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_RESPONSIVE_DESIGN)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_UI_ANIMATIONS)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_PAID_SUBSCRIPTIONS);

        desktopApp.addPossibleSubType(serverBackend)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_RESPONSIVE_DESIGN)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_SOCIAL_MEDIA_INTEGRATION)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_UI_ANIMATIONS);

        mobileGame.addPossibleSubType(serverBackend)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_BANNER_ADS)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_VIDEO_ADS)
            .addPossibleFeature(ProductFeature.PRODUCT_FEATURE_IN_APP_PURCHASES);

        /*
        addTypeToArrays(PRODUCT_TYPE_WEB_APP, webApp);
        addTypeToArrays(PRODUCT_TYPE_MOBILE_APP, mobileApp);
        addTypeToArrays(PRODUCT_TYPE_MOBILE_GAME, mobileGame);
        addTypeToArrays(PRODUCT_TYPE_DESKTOP_APP, desktopApp);
        addTypeToArrays(PRODUCT_TYPE_SERVER_BACKEND, serverBackend);
        */

    }

    /**
     * Adds product type ID and product type into respective lists. This is used for initializing
     * static content.
     * @param typeId ID constant of the type
     * @param type The ProductType
     */
    private static void addTypeToArrays(int typeId, ProductType type) {
        sProductTypes.append(typeId, type);
        sProductTypeIds.add(typeId);
    }

    /**
     * Gets a random ProductType from all the available ones.
     * @return A random ProductType
     */
    public static ProductType getRandomProductType() {
        initStaticContent();
        int typeId = (Integer) Utils.getRandomFromList(sProductTypeIds);
        return sProductTypes.get(typeId);
    }

    /**
     * Builds a list of all the product types.
     * @return The list with all the product types.
     */
    public static List<ProductType> getAllTypes() {
        ArrayList<ProductType> types = new ArrayList<>();

        for (int typeId : sProductTypeIds) {
            types.add(sProductTypes.get(typeId));
        }

        return types;
    }


}
