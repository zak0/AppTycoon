package jaakko.jaaska.apptycoon.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;

/**
 * Fragment for displaying more detailed stats for a product and its history.
 */

public class ProductStatsFragment extends AppTycoonFragment {

    private static final String TAG = "ProductStatsFragment";

    Product mProduct;

    public ProductStatsFragment() {
        super("Product Name", R.layout.fragment_product_stats);
    }

    @Override
    protected void onContentCreateView(View view) {
        Bundle args = getArguments();
        Company company = GameEngine.getInstance().getGameState().getCompany();
        mProduct = company.getProducts().get(args.getInt(UiUpdateHandler.ARG_PRODUCT_INDEX, -1));
        Log.d(TAG, "onContentCreateView() - product = " + mProduct.getName());

        setTitle(mProduct.getName());
        showBackButton();
        setAction("Spec. Update", new Action() {
            @Override
            public void doAction() {
                Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PRODUCT_NEW_RELEASE);
                msg.sendToTarget();
            }
        });
    }
}
