package jaakko.jaaska.apptycoon.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductType;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.listener.FragmentBackButtonOnClickListener;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;

/**
 * A fragment for configuring a new product.
 */

public class NewProductFragment extends Fragment {

    private static final String TAG = "NewProductFragment";

    /** The new product being configures. */
    private Product mProduct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_product, container, false);

        int productTypeId = getArguments().getInt(UiUpdateHandler.ARG_NEW_PRODUCT_TYPE);
        ProductType productType = ProductType.getProductType(productTypeId);

        Log.d(TAG, "onCreateView() - type of new product is '" + productType.getName() + "'");
        mProduct = new Product("_unnamed_", productType);

        TextView viewBack = (TextView) view.findViewById(R.id.textViewBack);
        viewBack.setOnClickListener(new FragmentBackButtonOnClickListener(MainActivity.FRAGMENT_PRODUCTS));
        viewBack.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK, viewBack.getCurrentTextColor()));

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText("New " + productType.getName());

        return view;
    }
}
