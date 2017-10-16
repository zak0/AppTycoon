package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.dialog.AppTycoonAlertDialog;
import jaakko.jaaska.apptycoon.utils.Utils;

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
        final int productIndex = args.getInt(UiUpdateHandler.ARG_PRODUCT_INDEX, -1);
        mProduct = company.getProducts().get(productIndex);
        Log.d(TAG, "onContentCreateView() - product = " + mProduct.getName());

        setTitle(mProduct.getName());
        showBackButton();
        setAction("Spec. Update", new Action() {
            @Override
            public void doAction() {
                Resources res = getContext().getResources();

                if (mProduct.getReleasedVersion() == null) {
                    // Only allow specification of the next update if the product has been released.
                    String dialogTitle = res.getString(R.string.dialog_generic_alert_dialog_title);
                    String dialogText = res.getString(R.string.dialog_cannot_spec_update_for_unreleased_product);
                    new AppTycoonAlertDialog(getActivity(), dialogTitle, dialogText).show();
                } else {
                    Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PRODUCT_NEW_RELEASE);
                    Bundle args = msg.getData();
                    args.putInt(UiUpdateHandler.ARG_PRODUCT_INDEX, productIndex);
                    msg.setData(args);
                    msg.sendToTarget();
                }
            }
        });

        // Populate the views
        TextView textViewComplexity = (TextView) view.findViewById(R.id.textViewComplexity);
        TextView textViewQuality = (TextView) view.findViewById(R.id.textViewQuality);

        textViewComplexity.setText(Utils.largeNumberToNiceString(mProduct.getComplexity(), 2));
        textViewQuality.setText(Utils.largeNumberToNiceString(mProduct.getQuality(), 2));

    }
}
