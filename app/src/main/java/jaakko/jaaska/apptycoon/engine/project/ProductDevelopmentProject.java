package jaakko.jaaska.apptycoon.engine.project;

import android.widget.Toast;

import java.util.ArrayList;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.product.Product;

/**
 * A project that results in a new version of a product.
 */

public class ProductDevelopmentProject extends Project {

    private Product mProduct;
    private double mFractionalQuality;
    private long mQualityGain;

    public ProductDevelopmentProject(String description, Product product) {
        // Name of the new product dev. project is "New [product type]"
        super("R&D - " + product.getType().getName(), description);
        mProduct = product;
    }

    @Override
    protected long calculateWorkAmount() {

        long workAmount = 0;

        // Work amount is the sum of work amounts of all the tasks.
        for (ProjectTask task : mTasks) {
            workAmount += task.getWorkAmount();
        }

        return workAmount;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    /** Delivering the project releases the new version of the product. */
    @Override
    public void deliver() {
        mProduct.releaseNextVersion();
        GameEngine.getInstance().getGameState().getCompany().onIncomeChanged();

        Toast.makeText(AppTycoonApp.getContext(),
                mProduct.getName() + " released",
                Toast.LENGTH_LONG).show();
    }

    public void removeAllTasks() {
        mTasks = new ArrayList<>();
    }

    @Override
    public void progress(double workAmount, double qualityAmount, long time) {
        super.progress(workAmount, qualityAmount, time);

        // Add quality gain.
        mFractionalQuality += qualityAmount;
        long qualityIntPart = (long) mFractionalQuality;
        mFractionalQuality -= qualityIntPart;
        mQualityGain += qualityIntPart;

    }

    public long getQualityGain() {
        return mQualityGain;
    }
}
