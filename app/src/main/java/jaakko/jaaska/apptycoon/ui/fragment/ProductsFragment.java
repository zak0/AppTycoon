package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.core.GameState;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductType;
import jaakko.jaaska.apptycoon.engine.project.Project;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.UiUpdater;
import jaakko.jaaska.apptycoon.ui.dialog.ActionSelectDialogBuilder;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Created by jaakko on 10.8.2017.
 */

public class ProductsFragment extends Fragment {

    private ProductRecyclerViewAdapter mRecyclerAdapter;
    private GameState mGameState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        mGameState = GameEngine.getInstance().getGameState();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ProductRecyclerViewAdapter(mGameState.getCompany().getProducts());
        recyclerView.setAdapter(mRecyclerAdapter);

        // New product button
        TextView newProductTextView = (TextView) view.findViewById(R.id.textViewAction);
        newProductTextView.setOnTouchListener(new TextViewChangeColourOnTouchListener(
                Color.BLACK,
                newProductTextView.getCurrentTextColor()));
        newProductTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First select the type of the new product.
                ActionSelectDialogBuilder builder = new ActionSelectDialogBuilder(getContext());

                for (final ProductType type : ProductType.getAllTypes()) {
                    builder.addCustomActionEntry(type.getName(), new ActionSelectDialogBuilder.Action() {
                        @Override
                        public void doAction() {
                            // Then when selected, go to configuring it.
                            Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_NEW_PRODUCT);
                            Bundle args = msg.getData();
                            args.putInt(UiUpdateHandler.ARG_NEW_PRODUCT_TYPE, type.getType());
                            msg.setData(args);
                            msg.sendToTarget();
                        }
                    });
                }

                builder.show();
            }
        });

        return view;
    }

    private class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

        private List<Product> mProducts;
        private GameState mGameState;

        private ProductRecyclerViewAdapter(List<Product> products) {
            mProducts = products;
            mGameState = GameEngine.getInstance().getGameState();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.containerView = view;

            viewHolder.textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            viewHolder.textViewProductType = (TextView) view.findViewById(R.id.textViewProductType);
            viewHolder.textViewProductComplexity = (TextView) view.findViewById(R.id.textViewProductComplexity);
            viewHolder.textViewProductQuality = (TextView) view.findViewById(R.id.textViewProductQuality);
            viewHolder.textViewProductVersion = (TextView) view.findViewById(R.id.textViewProductVersion);
            viewHolder.textViewProductBugs = (TextView) view.findViewById(R.id.textViewProductBugs);
            viewHolder.textViewProductNextVersion = (TextView) view.findViewById(R.id.textViewProductNextRelease);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Product product = mProducts.get(position);

            Resources res = AppTycoonApp.getContext().getResources();

            String stringLastVersion = "";
            if (product.getReleaseCount() > 0) {
                stringLastVersion = res.getString(R.string.product_list_item_version_released,
                        product.getReleaseCount(),
                        "n/a"); // TODO: A string with time passed since release. E.g. "1d 12h 43m".
            } else {
                stringLastVersion = res.getString(R.string.product_list_item_version_not_yet_released);
            }

            // Show the "Next version defined" text if a project for the next version has been
            // specified.
            Project project = product.getDevelopmentProject();
            holder.textViewProductNextVersion.setVisibility(
                    project == null ? View.INVISIBLE : View.VISIBLE);

            // Set contents of dynamic views here
            holder.textViewProductName.setText(product.getName());
            holder.textViewProductType.setText(product.getType().getName());
            holder.textViewProductComplexity.setText(Utils.largeNumberToNiceString(product.getComplexity(), 2));
            holder.textViewProductQuality.setText(Utils.largeNumberToNiceString(product.getQuality(), 2));
            holder.textViewProductVersion.setText(stringLastVersion);

            if (project != null) {
                String stringNextVersion = res.getString(R.string.product_list_item_next_version_defined,
                        Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
                holder.textViewProductNextVersion.setText(stringNextVersion);
            }
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View containerView;

            TextView textViewProductName;
            TextView textViewProductType;
            TextView textViewProductComplexity;
            TextView textViewProductQuality;
            TextView textViewProductVersion;
            TextView textViewProductBugs;
            TextView textViewProductNextVersion;

            private ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
