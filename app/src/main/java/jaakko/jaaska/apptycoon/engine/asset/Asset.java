package jaakko.jaaska.apptycoon.engine.asset;

/**
 * Assets are company's property some of which are required, some not necessary but benefit the
 * company somehow.
 *
 * This interface defines the common characteristics for all assets.
 */

public interface Asset {

    String getName();
    String getDescription();

    long getCount();

    long getAcquisitionCost();
    double getCostPerSecond();
}