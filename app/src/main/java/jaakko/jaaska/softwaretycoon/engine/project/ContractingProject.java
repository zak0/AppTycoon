package jaakko.jaaska.softwaretycoon.engine.project;

import android.support.v4.util.Pair;

import java.util.Random;

import jaakko.jaaska.softwaretycoon.engine.product.Product;
import jaakko.jaaska.softwaretycoon.engine.product.ProductFeature;
import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 1.5.2017.
 */

public class ContractingProject extends Project {

    /** Product that is developed in this project */
    private Product mProduct;

    /** Customer is null for in-house projects. */
    private String mCustomer;

    /** Contracting projects have a quality requirement. */
    private long mRequiredQuality;

    /** Current quality of the project deliverables. For contracting projects, quality is only
     * tracked on the project level. Product quality is not tracked. */
    private long mQuality;

    /** Amount of time that is left to complete the customer project. */
    private long mTimeLeft;

    /** Amount this project pays if delivered in time. */
    private long mReward;


    public ContractingProject(String name, String description) {
        super(name, description);
    }

    public long getReward() {
        return mReward;
    }

    public String getCustomer() {
        return mCustomer;
    }

    public long getRequiredQuality() {
        return mRequiredQuality;
    }

    @Override
    protected long calculateWorkAmount() {
        return mProduct.getComplexity();
    }

    public long getTimeLeft() {
        return mTimeLeft - getTimeSpent();
    }

    public Product getProduct() {
        return mProduct;
    }

    public long getQuality() {
        return mQuality;
    }

    @Override
    public boolean isReady() {
        return getWorkProgress() >= getWorkAmount() && mQuality >= mRequiredQuality;
    }

    //
    //
    // Static utility methods
    //
    //

    /**
     * Generates a totally random contracting project.
     * @return The generated project
     */
    public static ContractingProject getRandomContractingProject() {
        String customer = Utils.getRandomCustomer();
        Product product = Product.getRandomProduct();
        ContractingProject project = new ContractingProject(product.getType().getName(),
                product.getType().getName() + " for " + customer);
        project.mProduct = product;
        project.updateWorkAmount();

        Random r = new Random();
        project.mRequiredQuality = Math.abs(r.nextLong()) % (project.getWorkAmount() + 1);
        project.mCustomer = customer;
        project.mReward = project.mRequiredQuality + project.getWorkAmount();
        project.mTimeLeft = 120000 + r.nextInt(900000);

        // Add all the features as tasks.
        for (Pair<ProductFeature, Integer> pair : product.getFeatures()) {
            FeatureTask task = new FeatureTask(pair.first, 0, pair.second);
            project.addTask(task);
        }

        return project;
    }
}
