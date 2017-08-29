package jaakko.jaaska.apptycoon.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.core.GameState;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.project.ContractingProject;
import jaakko.jaaska.apptycoon.engine.project.ProductDevelopmentProject;
import jaakko.jaaska.apptycoon.engine.project.Project;
import jaakko.jaaska.apptycoon.engine.project.ProjectSlot;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.UiUpdater;
import jaakko.jaaska.apptycoon.ui.dialog.ActionSelectDialogBuilder;
import jaakko.jaaska.apptycoon.ui.dialog.AppTycoonDialog;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Fragment for displaying the status of ongoing projects.
 * Also, new projects are assigned to project slots in this fragment.
 */

public class ProjectsFragment extends Fragment {
    private static final String TAG = "ProjectsFragment";

    private ProjectsRecyclerViewAdapter mRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        RecyclerView recyclerViewProjects = (RecyclerView) view.findViewById(R.id.recyclerViewProjects);
        mRecyclerViewAdapter = new ProjectsRecyclerViewAdapter();

        recyclerViewProjects.setHasFixedSize(true); // performance boost when content does not change the size of RecyclerView
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProjects.setAdapter(mRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume() called");

        UiUpdateHandler.getInstance().registerUpdater(mRecyclerViewAdapter, UiUpdateHandler.ACTION_REFRESH_UI);

        mRecyclerViewAdapter.resetOnClickListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        UiUpdateHandler.getInstance().unRegisterUpdater(mRecyclerViewAdapter);
    }

    /**
     * Builds and shows a dialog for selecting the type of a new project when an empty
     * project slot is clicked.
     *
     * @param slotIndex The index number of the ProjectSlot that was clicked.
     */
    private void showNewProjectTypeSelectionDialog(final int slotIndex) {
        ActionSelectDialogBuilder builder = new ActionSelectDialogBuilder(getContext());
        builder.addCustomActionEntry("Product Development Project", new ActionSelectDialogBuilder.Action() {
            @Override
            public void doAction() {
                // Show project selection dialog with the list of defined
                // product development projects.
                final AppTycoonDialog dialog = new AppTycoonDialog(getActivity(),
                        R.layout.dialog_select_product_dev_project,
                        "Select Project");

                dialog.setCancelAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerViewProjects);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                GameState gameState = GameEngine.getInstance().getGameState();
                ProjectSlot slot = gameState.getCompany().getProjectSlots().get(slotIndex);

                ProdDevProjectsRecyclerViewAdapter adapter = new ProdDevProjectsRecyclerViewAdapter(dialog, slot);
                recyclerView.setAdapter(adapter);

                dialog.show();
            }
        });


        builder.addCustomActionEntry("Contracting Project", new ActionSelectDialogBuilder.Action() {
            @Override
            public void doAction() {
                Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_NEW_PROJECT);
                msg.getData().putInt(UiUpdateHandler.ARG_PROJECT_SLOT_INDEX, slotIndex);
                msg.sendToTarget();
            }
        });

        builder.show();
    }


    /**
     * RecyclerViewAdapter for the project slots in the ProjectsFragment.
     *
     *
     *
     */
    class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsRecyclerViewAdapter.ViewHolder> implements UiUpdater {

        List<ProjectSlot> mSlots;
        List<Pair<ViewHolder, ProjectSlot>> mViewHolderSlots;

        @Override
        public void updateUi(int action, Bundle args) {
            // Do the UI update here so that we can only update the views that need updating.
            for (Pair<ViewHolder, ProjectSlot> pair : mViewHolderSlots) {
                ViewHolder vh = pair.first;
                ProjectSlot ps = pair.second;
                setupViews(vh, ps);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_slot, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.layoutParent = view;
            viewHolder.layoutProgress = view.findViewById(R.id.layoutProjectProgress);
            viewHolder.textViewProjectTime = (TextView) view.findViewById(R.id.textViewProjectTime);
            viewHolder.textViewProjectName = (TextView) view.findViewById(R.id.textViewProjectTitle);
            viewHolder.textViewProjectDescription = (TextView) view.findViewById(R.id.textViewProjectInfo);
            viewHolder.textViewProjectWork = (TextView) view.findViewById(R.id.textViewProjectWork);
            viewHolder.textViewProjectQuality = (TextView) view.findViewById(R.id.textViewProjectQuality);

            return viewHolder;
        }

        void resetOnClickListeners() {
            Log.d(TAG, "resetOnClickListeners()");
            int position = 0;
            for (Pair<ViewHolder, ProjectSlot> pair : mViewHolderSlots) {
                setOnClickListenerForSlot(pair.first.layoutParent, pair.second, position++);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ProjectSlot slot = mSlots.get(position);

            // Set on click listener based on what is currently in the project slot.
            // Or if it's empty.
            setOnClickListenerForSlot(holder.layoutParent, slot, position);

            // Hide / display data based on the current slot state.
            setupViews(holder, slot);

            // Store the ViewHolder and ProjectSlot to allow smooth UI updates.
            mViewHolderSlots.add(new Pair<>(holder, slot));
        }

        private void setOnClickListenerForSlot(View slotView, final ProjectSlot projectSlot, final int position) {
            // If slot is empty, show a new project type selector dialog.
            if (projectSlot.isFree()) {
                slotView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNewProjectTypeSelectionDialog(position);
                    }
                });
            }

            // If there is a project in the slot, display an action dialog when the project is clicked.
            else {
                final ActionSelectDialogBuilder builder = new ActionSelectDialogBuilder(getContext());
                builder.addCustomActionEntry("Deliver project", new ActionSelectDialogBuilder.Action() {
                    @Override
                    public void doAction() {
                        Project project = projectSlot.getProject();

                        boolean projectIsDeliverable = false;

                        if (project instanceof ContractingProject) {
                            projectIsDeliverable = ((ContractingProject) project).isSuccessful();
                        } else if (project instanceof ProductDevelopmentProject) {
                            projectIsDeliverable = project.isReady();
                        }

                        if (projectIsDeliverable) {
                            projectSlot.getProject().deliver();
                            projectSlot.setProject(null);
                            Toast.makeText(getContext(), R.string.message_project_delivered, Toast.LENGTH_LONG).show();
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), R.string.message_project_not_ready_for_delivery, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.addCustomActionEntry("Cancel project", new ActionSelectDialogBuilder.Action() {
                    @Override
                    public void doAction() {

                        // TODO: Implement own "AlertDialog" to remove the need to customize inbuilt AlertDialogs.
                        AlertDialog.Builder alertDlgBuilder =  new AlertDialog.Builder(getContext());
                        alertDlgBuilder.setMessage(R.string.dialog_confirm_cancel_project);
                        alertDlgBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                projectSlot.setProject(null);
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                        alertDlgBuilder.setNegativeButton(android.R.string.no, null);
                        alertDlgBuilder.show();

                        /*
                        AlertDialog dlg = alertDlgBuilder.show();

                        // Set font family to monospace to make the dialog appear similar to rest of the UI.

                        ((TextView) dlg.findViewById(android.R.id.message)).setTypeface(Typeface.MONOSPACE);
                        ((Button) dlg.findViewById(android.R.id.button1)).setTypeface(Typeface.MONOSPACE);
                        ((Button) dlg.findViewById(android.R.id.button2)).setTypeface(Typeface.MONOSPACE);
                        */


                    }
                });

                slotView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.show();
                    }
                });
            }
        }

        private void setupViews(ViewHolder holder, ProjectSlot slot) {
            if (slot.isFree()) {
                // Reset the views to "defaults"
                holder.layoutProgress.setVisibility(View.INVISIBLE);
                holder.textViewProjectTime.setVisibility(View.INVISIBLE);
                holder.textViewProjectName.setText(R.string.project_slot_empty_slot);
                holder.textViewProjectDescription.setText(R.string.project_slot_tap_to_start);
            } else {
                // Setup views that are common to all not-free slots.
                Project project = slot.getProject();

                holder.layoutProgress.setVisibility(View.VISIBLE);
                holder.textViewProjectTime.setVisibility(View.VISIBLE);

                double workPerSec = slot.getWorkFraction()
                        * (double) GameEngine.getInstance().getGameState().getCompany().getCps();
                long timeLeft =  (long) (1000.0d * (double) project.getWorkLeft () / workPerSec);

                holder.textViewProjectTime.setText(Utils.millisecondsToTimeString(timeLeft));
                holder.textViewProjectName.setText(project.getName());
                holder.textViewProjectDescription.setText(project.getDescription());
            }

            // Then setup the project type specific views.
            if (slot.getProject() instanceof ContractingProject) {
                ContractingProject project = (ContractingProject) slot.getProject();

                Resources res = AppTycoonApp.getContext().getResources();

                // Contracting projects have a custom description.
                String strProjectDescription = res.getString(R.string.project_slot_contracting_project_info,
                                                    project.getCustomer());
                String strProjectProgress = res.getString(R.string.project_slot_progress,
                                                    Utils.largeNumberToNiceString(project.getWorkProgress(), 2),
                                                    Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
                String strProjectQuality = res.getString(R.string.project_slot_contracting_project_quality,
                                                    Utils.largeNumberToNiceString(project.getQuality(), 2),
                                                    Utils.largeNumberToNiceString(project.getRequiredQuality(), 2));

                holder.textViewProjectWork.setText(strProjectProgress);
                holder.textViewProjectQuality.setText(strProjectQuality);
                holder.textViewProjectDescription.setText(strProjectDescription);

                // Color text views red/green when the project is finished according whether the requirements
                // were met or not.
                if (project.isReady() || project.isSuccessful()) {
                    int colorRed = ContextCompat.getColor(AppTycoonApp.getContext(), R.color.red);
                    int colorGreen = ContextCompat.getColor(AppTycoonApp.getContext(), R.color.green);

                    holder.textViewProjectWork.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    holder.textViewProjectQuality.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                }
            } else if (slot.getProject() instanceof ProductDevelopmentProject) {
                ProductDevelopmentProject project = (ProductDevelopmentProject) slot.getProject();

                Resources res = AppTycoonApp.getContext().getResources();
                String strProjectProgress = res.getString(R.string.project_slot_progress,
                        Utils.largeNumberToNiceString(project.getWorkProgress(), 2),
                        Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
                String strProjectQuality = res.getString(R.string.project_slot_product_dev_project_quality,
                        Utils.largeNumberToNiceString(project.getQualityGain(), 2), // Quality added
                        0); // Number of new bugs

                holder.textViewProjectWork.setText(strProjectProgress);
                holder.textViewProjectQuality.setText(strProjectQuality);
            }
        }

        ProjectsRecyclerViewAdapter() {
            mSlots = GameEngine.getInstance().getGameState().getCompany().getProjectSlots();
            mViewHolderSlots = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return mSlots.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            /** The parent of the entire card */
            View layoutParent;

            /** Layout containing the progress views */
            View layoutProgress;

            /** Time it takes to finish the project in the given ProjectSlot. */
            TextView textViewProjectTime;

            TextView textViewProjectName;
            TextView textViewProjectDescription;
            TextView textViewProjectWork;
            TextView textViewProjectQuality;

            ViewHolder(View v) {
                super(v);
            }
        }
    }





    /**
     * RecyclerViewAdapter for the project slots in the ProjectsFragment.
     *
     *
     *
     */
    class ProdDevProjectsRecyclerViewAdapter extends
            RecyclerView.Adapter<ProdDevProjectsRecyclerViewAdapter.ViewHolder> {

        private List<ProductDevelopmentProject> mProjects;

        /** The slot into which the started project will go. */
        private ProjectSlot mProjectSlot;

        /** The dialog this adapter is used in. */
        private Dialog mDialog;

        ProdDevProjectsRecyclerViewAdapter(Dialog dialog, ProjectSlot slot) {
            mProjectSlot = slot;
            mDialog = dialog;

            // Go through all the products and see if they have development projects
            // defined.
            //
            // Append all development projects onto the list.
            mProjects = new ArrayList<>();

            final List<Product> products = GameEngine.getInstance().getGameState().getCompany().getProducts();

            for (Product product : products) {
                ProductDevelopmentProject project = product.getDevelopmentProject();
                if (project != null) {
                    // TODO: Exclude possible already started projects.
                    mProjects.add(project);
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_development_project, parent, false);

            ViewHolder holder = new ViewHolder(view);
            holder.textViewProjectName = (TextView) view.findViewById(R.id.textViewProjectName);
            holder.textViewProjectDescription = (TextView) view.findViewById(R.id.textViewProjectDescription);
            holder.textViewProjectEta = (TextView) view.findViewById(R.id.textViewProjectETA);
            holder.textViewProjectWorkAmount = (TextView) view.findViewById(R.id.textViewProjectWork);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ProductDevelopmentProject project = mProjects.get(position);

            holder.textViewProjectName.setText(project.getName());
            holder.textViewProjectDescription.setText(project.getDescription());
            holder.textViewProjectWorkAmount.setText(Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
            holder.textViewProjectEta.setText("n/a");

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the project.
                    mProjectSlot.setProject(project);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mProjects.size();
        }

        class ViewHolder extends  RecyclerView.ViewHolder {

            /** The parent of the views of the item in the RecyclerView. */
            View root;

            TextView textViewProjectName;
            TextView textViewProjectDescription;
            TextView textViewProjectEta;
            TextView textViewProjectWorkAmount;

            ViewHolder(View v) {
                super(v);

                root = v;
            }
        }
    }
}
