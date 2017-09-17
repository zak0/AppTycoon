package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.asset.PremisesAsset;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * The fragment for managing current premises and acquiring and moving into new.
 */

public class PremisesFragment extends AppTycoonFragment {

    private View mView;

    public PremisesFragment() {
        super("Premises", R.layout.fragment_premises);
    }

    @Override
    protected void onContentCreateView(View view) {
        mView = view;

        setBackTargetFragment(MainActivity.FRAGMENT_ASSETS);

        populateCurrentPremisesViews();
        setupNewPremisesRecyclerView();
    }

    private void populateCurrentPremisesViews() {
        TextView textViewName = (TextView) mView.findViewById(R.id.textViewCurrentPremisesName);
        TextView textViewDescription = (TextView) mView.findViewById(R.id.textViewCurrentPremisesDescription);
        TextView textViewCosts = (TextView) mView.findViewById(R.id.textViewCurrentPremisesCost);
        TextView textViewSpace = (TextView) mView.findViewById(R.id.textViewCurrentPremisesSpace);

        Resources res = AppTycoonApp.getContext().getResources();
        Company company = GameEngine.getInstance().getGameState().getCompany();
        PremisesAsset premises = company.getPremises();

        String stringCosts = res.getString(R.string.premises_asset_costs,
                Utils.largeNumberToNiceString(premises.getCostPerSecond(), 2));
        String stringSpace = res.getString(R.string.premises_asset_people_count,
                company.getEmployeeCount(),
                premises.getMaximumHeadCount());

        textViewName.setText(premises.getName());
        textViewDescription.setText(premises.getDescription());
        textViewCosts.setText(stringCosts);
        textViewSpace.setText(stringSpace);
    }

    private void setupNewPremisesRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewMoveToPremises);
        NewPremisesRecyclerViewAdapter adapter = new NewPremisesRecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private class NewPremisesRecyclerViewAdapter extends RecyclerView.Adapter<NewPremisesRecyclerViewAdapter.ViewHolder> {

        private List<PremisesAsset> mAllPremises;

        private NewPremisesRecyclerViewAdapter() {
            mAllPremises = PremisesAsset.getAllPremisesAsList();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_new_premises, parent, false);
            ViewHolder vh = new ViewHolder(view);

            vh.textViewName = (TextView) view.findViewById(R.id.textViewPremisesName);
            vh.textViewOfficeSpace = (TextView) view.findViewById(R.id.textViewPremisesOfficeSpace);
            vh.textViewRunningCosts = (TextView) view.findViewById(R.id.textViewPremisesCosts);
            vh.textViewAcquisitionCost = (TextView) view.findViewById(R.id.textViewPremisesAcquisitionCost);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PremisesAsset premises = mAllPremises.get(position);
            holder.premises = premises;

            Resources res = getResources();
            String strRunningCosts = res.getString(R.string.generic_money_per_second,
                    Utils.largeNumberToNiceString(premises.getCostPerSecond(), 2));
            String strAcquisitionCost = res.getString(R.string.generic_large_money,
                    Utils.largeNumberToNiceString(premises.getAcquisitionCost(), 2));
            String strOfficeSpace = res.getString(R.string.generic_large_number,
                    Utils.largeNumberToNiceString(premises.getMaximumHeadCount(), 2));

            holder.textViewName.setText(premises.getName());
            holder.textViewOfficeSpace.setText(strOfficeSpace);
            holder.textViewRunningCosts.setText(strRunningCosts);
            holder.textViewAcquisitionCost.setText(strAcquisitionCost);
        }

        @Override
        public int getItemCount() {
            return mAllPremises.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName;
            TextView textViewOfficeSpace;
            TextView textViewRunningCosts;
            TextView textViewAcquisitionCost;

            PremisesAsset premises;

            private ViewHolder(View v) {
                super(v);

            }
        }
    }
}
