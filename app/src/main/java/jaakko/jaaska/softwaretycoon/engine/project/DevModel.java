package jaakko.jaaska.softwaretycoon.engine.project;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for data related to a development model.
 *
 * Also a container for static data for all models.
 *
 * TODO: Support for models with iterative phases.
 *
 * Created by jaakko on 15.4.2017.
 */

public class DevModel {

    private static final String TAG = "DevModel";

    private String mName;
    private String mDescription;

    /** Container for all phases of a project with this model.
     * This is a list containing all phases even though each DevPhase
     * knows the next phase in the chain. */
    private List<DevPhase> mPhases;

    private DevModel(String name, String description) {
        mName = name;
        mDescription = description;
        mPhases = new ArrayList<>();
    }



    //
    //
    // Static Data
    private static List<DevModel> sModels;

    private static void initStaticData() {
        // Init only once.
        if (sModels != null) {
            Log.e(TAG, "Tried to init statics again.");
            return;
        }

        sModels = new ArrayList<>();

        //
        // Waterfall
        DevModel waterfall = new DevModel("Waterfall", "A sequential development process that flows forward as smoothly as a waterfall.");
        DevPhase requirements = new DevPhase(waterfall, "Requirements analysis", 1).setSkillEffectRatios(3, 6, 5, 4);
        DevPhase design = new DevPhase(waterfall, "Design", 1).setSkillEffectRatios(10, 20, 1, 1);
        DevPhase implementation = new DevPhase(waterfall, "Implementation", 2).setSkillEffectRatios(10, 5, 1, 2);
        DevPhase verification = new DevPhase(waterfall, "Verification", 1).setSkillEffectRatios(2, 3, 1, 1);
        sModels.add(waterfall);
    }

    public static List<DevModel> getAllModels() {
        initStaticData();
        return sModels;
    }

    /**
     * Constructs phases for a project based on the selected development model.
     * @param project Project to get the phases for.
     * @param model The model that is used for the project.
     * @return The first phase of the project.
     */
    public ProjectPhase getPhasesForProject(Project project, DevModel model) {
        // TODO
        return null;

    }

}
