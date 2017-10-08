package jaakko.jaaska.apptycoon.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductFeature;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;

/**
 * A dialog for selecting a ProductFeature to add to a product.
 */

public class AddProductFeatureDialog extends AppTycoonDialog {

    private static final String TAG = "AddProductFeatureDialog";

    private Product mProduct;
    private CustomCallback mOnFeatureSelectedCallback;

    /**
     * @param product Product the feature is being added to
     * @param context Activity context
     * @param onFeatureSelected Callback that's called when a new feature is added
     */
    public AddProductFeatureDialog(Product product,
                                   Context context,
                                   CustomCallback onFeatureSelected) {
        super(context,
                R.layout.dialog_add_product_feature,
                "Add a feature");

        mProduct = product;
        mOnFeatureSelectedCallback = onFeatureSelected;

        setCancelAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAddableFeatures);
        List<ProductFeature> addableFeatures = mProduct.getType().getPossibleFeatures();
        AddableFeaturesRecyclerViewAdapter adapter = new AddableFeaturesRecyclerViewAdapter(addableFeatures, this);

        Log.d(TAG, "showAddFeatureDialog() - " + addableFeatures.size() + " features");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }


    /**
     * A RecyclerViewAdapter for the features that can be added to the product.
     */
    private class AddableFeaturesRecyclerViewAdapter extends RecyclerView.Adapter<AddableFeaturesRecyclerViewAdapter.ViewHolder> {
        private List<ProductFeature> mFeatures;
        private AppTycoonDialog mAddFeatureDialog;

        AddableFeaturesRecyclerViewAdapter(List<ProductFeature> features, AppTycoonDialog dialog) {
            mFeatures = features;
            mAddFeatureDialog = dialog;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_addable_product_feature, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.textViewFeatureName = (TextView) view.findViewById(R.id.textViewFeatureName);
            viewHolder.parent = view;

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ProductFeature feature = mFeatures.get(position);
            holder.textViewFeatureName.setText(feature.getName());

            // Clicking a feature adds the feature for the product.
            //
            // The feature is always with its level set to 1.
            holder.textViewFeatureName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProduct.addFeature(feature, 1);
                    mOnFeatureSelectedCallback.callBack();
                    mAddFeatureDialog.dismiss();
                }
            });

            holder.textViewFeatureName.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.GRAY,
                    holder.textViewFeatureName.getCurrentTextColor()));
        }

        @Override
        public int getItemCount() {
            return mFeatures.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View parent; // The top level layout view of the item.
            TextView textViewFeatureName;

            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
