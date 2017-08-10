package jaakko.jaaska.apptycoon.ui.fragment;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.project.ContractingProject;
import jaakko.jaaska.apptycoon.engine.project.Project;
import jaakko.jaaska.apptycoon.engine.project.ProjectSlot;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.UiUpdater;
import jaakko.jaaska.apptycoon.ui.dialog.ActionSelectDialogBuilder;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Created by jaakko on 16.4.2017.
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

    public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsRecyclerViewAdapter.ViewHolder> implements UiUpdater{

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
            viewHolder.textViewProjectTitle = (TextView) view.findViewById(R.id.textViewProjectTitle);
            viewHolder.textViewProjectInfo = (TextView) view.findViewById(R.id.textViewProjectInfo);
            viewHolder.textViewProjectProgress = (TextView) view.findViewById(R.id.textViewProjectProgress);
            viewHolder.textViewProjectBugs = (TextView) view.findViewById(R.id.textViewProjectNewBugs);
            viewHolder.textViewProjectQuality = (TextView) view.findViewById(R.id.textViewProjectQuality);

            return viewHolder;
        }

        public void resetOnClickListeners() {
            Log.d(TAG, "resetOnClickListeners()");
            int position = 0;
            for (Pair<ViewHolder, ProjectSlot> pair : mViewHolderSlots) {
                setOnClickListenerForSlot(pair.first.layoutParent, pair.second, position++);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ProjectSlot projectSlot = mSlots.get(position);

            // Set on click listener based on what is currently in the project slot.
            // Or if it's empty.
            setOnClickListenerForSlot(holder.layoutParent, projectSlot, position);

            // Hide / display data based on the current slot state.
            setupViews(holder, projectSlot);

            // Store the ViewHolder and ProjectSlot to allow smooth UI updates.
            mViewHolderSlots.add(new Pair(holder, projectSlot));
        }

        private void setOnClickListenerForSlot(View slotView, final ProjectSlot projectSlot, final int position) {
            // If slot is empty, launch new project setup.
            if (projectSlot.isFree()) {
                slotView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_NEW_PROJECT);
                        msg.getData().putInt(UiUpdateHandler.ARG_PROJECT_SLOT_INDEX, position);
                        msg.sendToTarget();
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
                        AlertDialog dlg = alertDlgBuilder.show();

                        // Set font family to monospace to make the dialog appear similar to rest of the UI.
                        ((TextView) dlg.findViewById(android.R.id.message)).setTypeface(Typeface.MONOSPACE);
                        ((Button) dlg.findViewById(android.R.id.button1)).setTypeface(Typeface.MONOSPACE);
                        ((Button) dlg.findViewById(android.R.id.button2)).setTypeface(Typeface.MONOSPACE);


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
                holder.textViewProjectTitle.setText(R.string.project_slot_empty_slot);
                holder.textViewProjectInfo.setText(R.string.project_slot_tap_to_start);
            }

            else if (slot.getProject() instanceof ContractingProject) {
                ContractingProject project = (ContractingProject) slot.getProject();

                Resources res = AppTycoonApp.getContext().getResources();
                String strProjectInfo = res.getString(R.string.project_slot_contracting_project_info,
                                                    project.getCustomer());
                String strProjectProgress = res.getString(R.string.project_slot_progress,
                                                    Utils.largeNumberToNiceString(project.getWorkProgress(), 2),
                                                    Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
                String strProjectQuality = res.getString(R.string.project_slot_contracting_project_quality,
                                                    Utils.largeNumberToNiceString(project.getQuality(), 2),
                                                    Utils.largeNumberToNiceString(project.getRequiredQuality(), 2));
                String strReadyToDeliver = res.getString(R.string.project_slot_ready_to_deliver);
                String strFailedToDeliver = res.getString(R.string.project_slot_failed_to_deliver);

                holder.textViewProjectTitle.setText(project.getName());
                holder.textViewProjectInfo.setText(strProjectInfo);
                holder.textViewProjectTime.setText(Utils.millisecondsToTimeString(project.getTimeLeft()));
                holder.textViewProjectProgress.setText(strProjectProgress);
                holder.textViewProjectQuality.setText(strProjectQuality);

                // Color text views red/green when the project is finished according whether the requirements
                // were met or not.
                if (project.isReady() || project.isSuccessful()) {
                    int colorRed = ContextCompat.getColor(AppTycoonApp.getContext(), R.color.red);
                    int colorGreen = ContextCompat.getColor(AppTycoonApp.getContext(), R.color.green);

                    //holder.textViewProjectTime.setText(project.isFinished() ? strReadyToDeliver : strFailedToDeliver);
                    //holder.textViewProjectTime.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    //holder.textViewProjectTitle.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    holder.textViewProjectProgress.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    holder.textViewProjectQuality.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                }
            }
        }

        public ProjectsRecyclerViewAdapter() {
            mSlots = GameEngine.getInstance().getGameState().getCompany().getProjectSlots();
            mViewHolderSlots = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return mSlots.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            /** The parent of the entire card */
            View layoutParent;

            /** Layout containing the progress views */
            View layoutProgress;

            /** Project deadline or elapsed time */
            TextView textViewProjectTime;

            TextView textViewProjectStatus;

            TextView textViewProjectTitle;
            TextView textViewProjectInfo;

            TextView textViewProjectProgress;
            TextView textViewProjectBugs;
            TextView textViewProjectQuality;

            public ViewHolder(View v) {
                super(v);
            }
        }
    }
}
