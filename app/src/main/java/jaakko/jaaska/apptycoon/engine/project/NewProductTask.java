package jaakko.jaaska.apptycoon.engine.project;

import jaakko.jaaska.apptycoon.engine.product.Product;

/**
 * A ProjectTask for the initial development of a new product.
 */

public class NewProductTask implements ProjectTask {

    /** The new product being developed. */
    private Product mProduct;

    public NewProductTask(Product product) {
        mProduct = product;
    }

    @Override
    public String getTaskTitle() {
        return "Product Development";
    }

    @Override
    public long getQuantity() {
        return 1;
    }

    @Override
    public String getTextualQuantity() {
        return null;
    }

    @Override
    public long getWorkAmount() {
        // Work amount for the "initial development" of the product.
        //
        // [base complexity of the product type] * 2 ^ [number of features] = [work amount]
        long workAmount = mProduct.getType().getBaseComplexity();
        workAmount = (long) (workAmount * Math.pow(2, mProduct.getFeatures().size()));

        return workAmount;
    }
}
