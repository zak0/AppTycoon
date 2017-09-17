package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.asset.PremisesAsset;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * The parent fragment for managing company's assets.
 */

public class AssetsFragment extends AppTycoonFragment {
    private static final String TAG = "AssetsFragment";

    private View mView;
    private Company mCompany;

    public AssetsFragment() {
        super("Assets", R.layout.fragment_assets);
    }

    @Override
    protected void onContentCreateView(View view) {
        mView = view;
        mCompany = GameEngine.getInstance().getGameState().getCompany();

        populatePremisesCard();

    }

    private void populatePremisesCard() {
        CardView cardView = (CardView) mView.findViewById(R.id.cardViewPremisesAsset);
        TextView textViewHeadCount = (TextView) mView.findViewById(R.id.textViewPremisesHeadCount);
        TextView textViewCosts = (TextView) mView.findViewById(R.id.textViewPremisesCosts);
        TextView textViewName = (TextView) mView.findViewById(R.id.textViewPremisesName);

        PremisesAsset premises = mCompany.getPremises();
        Resources res = AppTycoonApp.getContext().getResources();
        String strHeadCount = res.getString(R.string.premises_asset_people_count,
                mCompany.getEmployeeCount(), premises.getMaximumHeadCount());
        String strCosts = res.getString(R.string.premises_asset_costs,
                Utils.largeNumberToNiceString(premises.getCostPerSecond(), 2));

        textViewHeadCount.setText(strHeadCount);
        textViewCosts.setText(strCosts);
        textViewName.setText(premises.getName());

        // Clicking on the premises card opens a PremisesFragment.
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PREMISES).sendToTarget();
            }
        });
    }
}
