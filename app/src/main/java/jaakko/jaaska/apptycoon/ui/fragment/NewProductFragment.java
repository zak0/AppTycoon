package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductFeature;
import jaakko.jaaska.apptycoon.engine.product.ProductType;
import jaakko.jaaska.apptycoon.engine.project.ProductDevelopmentProject;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.dialog.AddProductFeatureDialog;
import jaakko.jaaska.apptycoon.ui.dialog.AppTycoonDialog;
import jaakko.jaaska.apptycoon.ui.dialog.ChangeProductNameDialog;
import jaakko.jaaska.apptycoon.ui.dialog.EditProductFeatureDialog;
import jaakko.jaaska.apptycoon.ui.listener.FragmentBackButtonOnClickListener;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * A fragment for configuring a new product.
 */

public class NewProductFragment extends Fragment {

    private static final String TAG = "NewProductFragment";

    /** The parent layout view of the fragment. */
    private View mView;

    private ProductFeaturesRecyclerViewAdapter mRecyclerViewAdapter;

    /** The new product being configures. */
    private Product mProduct;

    /** The project for the initial development of this product. */
    private ProductDevelopmentProject mProject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_product, container, false);

        int productTypeId = getArguments().getInt(UiUpdateHandler.ARG_NEW_PRODUCT_TYPE);
        ProductType productType = ProductType.getProductType(productTypeId);

        // Init the new product and the project for its initial development.
        Log.d(TAG, "onCreateView() - type of new product is '" + productType.getName() + "'");
        mProduct = new Product("_unnamed_", productType);
        mProduct.rebuildNewProductDevelopmentProject();
        mProduct.setDevelopmentProject(mProject);

        // Setup the fragment top bar title and action button actions.
        TextView viewBack = (TextView) mView.findViewById(R.id.textViewBack);
        viewBack.setOnClickListener(new FragmentBackButtonOnClickListener());
        viewBack.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK, viewBack.getCurrentTextColor()));

        TextView textViewTitle = (TextView) mView.findViewById(R.id.textViewTitle);
        textViewTitle.setText("New " + productType.getName());

        // "Done" action
        TextView viewDone = (TextView) mView.findViewById(R.id.textViewAction);
        viewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the product to Company's list of products.
                GameEngine.getInstance().getGameState().getCompany().addProduct(mProduct);

                // Go back to "Product Development" fragment
                UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PRODUCTS)
                        .sendToTarget();
            }
        });
        viewDone.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK, viewDone.getCurrentTextColor()));

        // Setup the RecyclerView for the product features.
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewProductFeatures);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mRecyclerViewAdapter = new ProductFeaturesRecyclerViewAdapter();
        recyclerView.setAdapter(mRecyclerViewAdapter);

        // Change name action button.
        TextView viewChangeName = (TextView) mView.findViewById(R.id.textViewChangeNameAction);
        viewChangeName.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK,
                viewChangeName.getCurrentTextColor()));
        viewChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameChangeDialog();
            }
        });

        // Add feature action button
        TextView viewAddFeature = (TextView) mView.findViewById(R.id.textViewAddFeatureAction);
        viewAddFeature.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK,
                viewAddFeature.getCurrentTextColor()));
        viewAddFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFeatureDialog();
            }
        });

        refreshViewsAndNewProductProject();

        return mView;
    }

    /**
     * Call this when the data for the new product has changed to update
     * the changes into the views and also refresh the new product development
     * project.
     */
    private void refreshViewsAndNewProductProject() {
        mProduct.rebuildNewProductDevelopmentProject();

        TextView textViewName = (TextView) mView.findViewById(R.id.textViewProductName);
        TextView textViewComplexity = (TextView) mView.findViewById(R.id.textViewProductComplexity);
        TextView textViewWorkAmount = (TextView) mView.findViewById(R.id.textViewProductDevWorkAmount);

        textViewName.setText(mProduct.getName());
        textViewComplexity.setText(Utils.largeNumberToNiceString(mProduct.getComplexity(), 2));
        textViewWorkAmount.setText(Utils.largeNumberToNiceString(mProduct.getDevelopmentProject().getWorkAmount(), 2));
    }

    /**
     * Builds and shows a dialog for changing the product name.
     */
    private void showNameChangeDialog() {
        new ChangeProductNameDialog(mProduct,
                getActivity(),
                new AppTycoonDialog.CustomCallback() {
                    @Override
                    public void callBack() {
                        refreshViewsAndNewProductProject();
                    }
                }).show();
    }

    /**
     * Build and show a dialog for editing (i.e. setting the feature level) a feature.
     *
     * @param feature The ProductFeature to edit with the dialog.
     */
    private void showEditFeatureDialog(final ProductFeature feature) {
        new EditProductFeatureDialog(mProduct, feature, getActivity(),
                new AppTycoonDialog.CustomCallback() {
                    @Override
                    public void callBack() {
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        refreshViewsAndNewProductProject();
                    }
        }).show();
    }

    /**
     * Show a dialog for adding a feature into the product.
     */
    private void showAddFeatureDialog() {
        new AddProductFeatureDialog(mProduct,
                getActivity(),
                new AppTycoonDialog.CustomCallback() {
                    @Override
                    public void callBack() {
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        refreshViewsAndNewProductProject();
                    }
                }).show();
    }

    /**
     * This is the RecyclerViewAdapter for the features that are already added for the product.
     *
     * This is used in the "new product" fragment.
     */
    private class ProductFeaturesRecyclerViewAdapter extends RecyclerView.Adapter<ProductFeaturesRecyclerViewAdapter.ViewHolder> {

        ProductFeaturesRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product_feature, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.textViewFeatureName = (TextView) view.findViewById(R.id.textViewFeatureName);
            viewHolder.textViewFeatureLevel = (TextView) view.findViewById(R.id.textViewFeatureLevel);
            viewHolder.parent = view;

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Pair<ProductFeature, Integer>> featureLevels = mProduct.getFeatures();
            final ProductFeature feature = featureLevels.get(position).first;

            holder.textViewFeatureName.setText(feature.getName());

            // Clicking a feature shows a dialog for setting the level of the feature
            // and for removing the feature.
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditFeatureDialog(feature);
                }
            });

            Resources res = AppTycoonApp.getContext().getResources();
            String stringFeatureLevel = res.getString(R.string.product_feature_list_item_level,
                    featureLevels.get(position).second);
            holder.textViewFeatureLevel.setText(stringFeatureLevel);
        }

        @Override
        public int getItemCount() {
            return mProduct.getFeaturesAsAList().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View parent; // The top level layout view of the item.
            TextView textViewFeatureName;
            TextView textViewFeatureLevel;

            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
