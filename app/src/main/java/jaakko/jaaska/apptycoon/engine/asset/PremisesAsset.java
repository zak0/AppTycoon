package jaakko.jaaska.apptycoon.engine.asset;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.engine.unlocks.Unlockable;

/**
* Company premises.
 */

public class PremisesAsset extends Unlockable implements Asset {

    private static final String TAG = "PremisesAsset";

    public static final int GARAGE = 10;
    public static final int SMALL_OFFICE_SPACE = 20;
    public static final int MEDIUM_OFFICE_SPACE = 30;
    public static final int SKYSCRAPER = 100;

    private String mName;
    private String mDescription;
    private int mTypeId;

    /** The maximum number of employees the company can have when housed in these premises. */
    private long mMaxHeadCount;

    /** The "running" costs of the premises that is deducted from funds every second. */
    private double mCostPerSecond;

    /** The one-time cost of getting this property. */
    private long mAcquisitionCost;

    /** Factor by which gained reputation is multiplied when the company is based in these premises. */
    private double mReputationGainFactor;

    private PremisesAsset(int type, String name, String description,
                          long acquisitionCost, double costPerSecond,
                          long maxHeadCount, double reputationGainFactor) {
        mTypeId = type;
        mName = name;
        mDescription = description;
        mAcquisitionCost = acquisitionCost;
        mCostPerSecond = costPerSecond;
        mMaxHeadCount = maxHeadCount;
        mReputationGainFactor = reputationGainFactor;
    }

    public long getMaximumHeadCount() {
        return mMaxHeadCount;
    }

    public double getReputationGainFactor() {
        return mReputationGainFactor;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public long getCount() {
        // Asset count is always 1 for premises.
        // TODO: Or maybe a possibility to have, e.g. a lot with possibility to build multiple buildings.
        return 1;
    }

    @Override
    public long getAcquisitionCost() {
        return mAcquisitionCost;
    }

    @Override
    public double getCostPerSecond() {
        return mCostPerSecond;
    }

    @Override
    public boolean isUnlocked() {
        return conditionsFulfilled();
    }

    /*
     *
     *
     *
     *
     *
     *
     *
     * Statics:
     * The "master" data for premises is defined here. This data is then accessed through static
     * methods below.
     */

    private static SparseArray<PremisesAsset> sPremisesById;

    public static List<PremisesAsset> getAllPremisesAsList() {
        initStaticData();

        ArrayList<PremisesAsset> all = new ArrayList<>();

        for (int i = 0; i < sPremisesById.size(); i++) {
            all.add(sPremisesById.get(sPremisesById.keyAt(i)));
        }

        return all;
    }

    public static PremisesAsset getPremisesAssetById(int id) {
        initStaticData();
        return sPremisesById.get(id);
    }

    private static void initStaticData() {
        if (sPremisesById != null) {
            Log.d(TAG, "initStaticData() - static data already initialized. skipping.");
            return;
        }

        sPremisesById = new SparseArray<>();

        sPremisesById.append(GARAGE,
                new PremisesAsset(GARAGE, "Garage",
                        "Garage", 0, 0.0, 3, 1.00));

        sPremisesById.append(SMALL_OFFICE_SPACE,
                new PremisesAsset(SMALL_OFFICE_SPACE, "Small Office Space",
                        "A small office with an open floor plan.", 3000, 0.5, 15, 1.00));

        sPremisesById.append(MEDIUM_OFFICE_SPACE,
                new PremisesAsset(MEDIUM_OFFICE_SPACE, "Medium Office Space",
                        "A medium office with an open floor plan and a kitchen.", 32000, 3.0, 50, 1.05));

        sPremisesById.append(SKYSCRAPER,
                new PremisesAsset(SKYSCRAPER, "Downtown Skyscraper",
                        "A modern piece of the city skyline.", 1800000000, 115400, 750, 50.5));
    }

}
