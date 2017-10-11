package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductFeature;
import jaakko.jaaska.apptycoon.engine.project.ProductDevelopmentProject;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.dialog.AddProductFeatureDialog;
import jaakko.jaaska.apptycoon.ui.dialog.AppTycoonDialog;
import jaakko.jaaska.apptycoon.ui.dialog.ChangeProductNameDialog;
import jaakko.jaaska.apptycoon.ui.dialog.EditProductFeatureDialog;
import jaakko.jaaska.apptycoon.ui.listener.TextViewListenerBinder;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Fragment for defining the next release for an existing product.
 */

public class NewProductReleaseFragment extends AppTycoonFragment {

    private View mView;
    private Product mProduct;
    private FeaturesRecyclerViewAdapter mRecyclerViewAdapter;

    public NewProductReleaseFragment() {
        super("Specify Next Release", R.layout.fragment_new_product_release);
    }

    @Override
    protected void onContentCreateView(View view) {
        int productIndex = getArguments().getInt(UiUpdateHandler.ARG_PRODUCT_INDEX);
        mProduct = GameEngine.getInstance().getGameState().getCompany().getProducts().get(productIndex);

        // (Re)build the project.
        // This will first initialize a new project (if not already initialized) and then
        // recalculates the work amount.
        mProduct.rebuildNewVersionDevelopmentProject();

        Bundle args = new Bundle();
        args.putInt(UiUpdateHandler.ARG_PRODUCT_INDEX, productIndex);
        showCustomBackButton(MainActivity.FRAGMENT_PRODUCT_STATS, args);
        setAction("Done", new Action() {
            @Override
            public void doAction() {
                // The development project should already be set to the product, so we just need
                // to navigate back.
                UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PRODUCTS).sendToTarget();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewProductFeatures);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewAdapter = new FeaturesRecyclerViewAdapter();
        recyclerView.setAdapter(mRecyclerViewAdapter);

        TextView textViewAddFeature = (TextView) view.findViewById(R.id.textViewAddFeatureAction);
        TextViewListenerBinder.bindActionToTextView(textViewAddFeature, new jaakko.jaaska.apptycoon.ui.Action() {
            @Override
            public void doAction() {
                new AddProductFeatureDialog(mProduct, getActivity(), new AppTycoonDialog.CustomCallback() {
                    @Override
                    public void callBack() {
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        projectChanged();
                    }
                }).show();
            }
        });

        TextView textViewChangeName = (TextView) view.findViewById(R.id.textViewChangeNameAction);
        TextViewListenerBinder.bindActionToTextView(textViewChangeName, new jaakko.jaaska.apptycoon.ui.Action() {
            @Override
            public void doAction() {
                new ChangeProductNameDialog(mProduct, getActivity(), new AppTycoonDialog.CustomCallback() {
                    @Override
                    public void callBack() {
                        projectChanged();
                    }
                }).show();
            }
        });

        mView = view;
        refreshViews();
    }

    /**
     * Call this whenever the contents of the project and/or the specifications of the update
     * change. This method will first update the project to match these changes and then handles
     * the refreshing of the UI to reflect the changes as well.
     */
    private void projectChanged() {
        mProduct.rebuildNewVersionDevelopmentProject();
        mRecyclerViewAdapter.notifyDataSetChanged();
        refreshViews();
    }

    /**
     * Refreshes the data views. Does not refresh the RecyclerView.
     */
    private void refreshViews() {
        TextView textViewProductName = (TextView) mView.findViewById(R.id.textViewProductName);
        TextView textViewComplexity = (TextView) mView.findViewById(R.id.textViewComplexity);
        TextView textViewWorkAmount = (TextView) mView.findViewById(R.id.textViewDevWorkAmount);

        textViewProductName.setText(mProduct.getName());
        textViewComplexity.setText(Utils.largeNumberToNiceString(mProduct.getComplexity(), 2));
        textViewWorkAmount.setText(Utils.largeNumberToNiceString(mProduct.getDevelopmentProject().getWorkAmount(), 2));
    }

    private class FeaturesRecyclerViewAdapter extends RecyclerView.Adapter<FeaturesRecyclerViewAdapter.ViewHolder> {

        // Types of features. These are used for determining which kind of list item to show.
        private static final int NEW = 0;
        private static final int UPGRADED = 1;
        private static final int UNCHANGED = 2;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            int listItemResId;

            switch (viewType) {
                case NEW:
                    listItemResId = R.layout.list_item_product_feature_new;
                    break;
                case UPGRADED:
                    listItemResId = R.layout.list_item_product_feature_upgraded;
                    break;
                case UNCHANGED:
                default:
                    listItemResId = R.layout.list_item_product_feature;
                    break;
            }

            View view = LayoutInflater.from(parent.getContext()).inflate(listItemResId, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);

            // All the list item layouts have the name and level views.
            viewHolder.textViewName = (TextView) view.findViewById(R.id.textViewFeatureName);
            viewHolder.textViewLevel = (TextView) view.findViewById(R.id.textViewFeatureLevel);

            // Upgraded also display the level difference.
            if (viewType == UPGRADED) {
                viewHolder.textViewLevelGain = (TextView) view.findViewById(R.id.textViewFeatureLevelAddition);
            }


            return viewHolder;
        }

        /**
         * Determine if the feature is new/upgraded/unchanged.
         *
         * @param position Position of the view in the adapter.
         * @return The type constant of the view in this position (NEW/UPGRADED/UNCHANGED)
         */
        @Override
        public int getItemViewType(int position) {
            // The current feature and its level
            Pair<ProductFeature, Integer> pair = mProduct.getFeatures().get(position);
            int featureId = pair.first.getFeatureId();
            int level = pair.second;

            Pair<ProductFeature, Integer> prevPair = mProduct.getReleasedVersion().getFeature(featureId);

            if (prevPair == null) {
                // If prevPair is null, the previous version did not have this, so this is a NEW feature.
                return NEW;
            } else if (level > prevPair.second) {
                // The previous version had this, but the level was lower, so this is an UPGRADED feature.
                return UPGRADED;
            } else {
                // The previous version had this feature, and it was of equal or lower level.
                // This is considered UNCHANGED. Downgrading features is not possible.
                return UNCHANGED;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ProductFeature feature = mProduct.getFeatures().get(position).first;
            int level = mProduct.getFeatures().get(position).second;
            Resources res = getContext().getResources();
            String stringLevel = res.getString(R.string.product_feature_list_item_level, level);
            holder.textViewName.setText(feature.getName());
            holder.textViewLevel.setText(stringLevel);
            holder.feature = feature;

            // If this is an upgraded feature, also display the level difference.
            if (getItemViewType(position) == UPGRADED) {
                int newLevel = mProduct.getFeatures().get(position).second;
                int oldLevel = mProduct.getReleasedVersion().getFeature(feature.getFeatureId()).second;
                String stringLevelGain = res.getString(R.string.product_feature_list_item_level_gain, newLevel - oldLevel);
                holder.textViewLevelGain.setText(stringLevelGain);
            }
        }

        @Override
        public int getItemCount() {
            return mProduct.getFeatures().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ProductFeature feature;
            TextView textViewName;
            TextView textViewLevel;
            TextView textViewLevelGain;

            ViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EditProductFeatureDialog(mProduct, feature, getActivity(),
                                new AppTycoonDialog.CustomCallback() {
                                    @Override
                                    public void callBack() {
                                        projectChanged();
                                    }
                        }).show();
                    }
                });
            }
        }
    }
}

