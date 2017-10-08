package jaakko.jaaska.apptycoon.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductFeature;

/**
 * Dialog for editing the level of a product feature.
 */

public class EditProductFeatureDialog extends AppTycoonDialog {

    /**
     * @param product Product that the feature belongs to
     * @param feature Feature that's being edited
     * @param context Activity context
     * @param onFeatureChanged Callback that's called when the feature is changed
     */
    public EditProductFeatureDialog(final Product product,
                                    final ProductFeature feature,
                                    Context context,
                                    final CustomCallback onFeatureChanged) {
        super(context,
                R.layout.dialog_edit_product_feature,
                "Edit feature");

        final EditText editTextLevel = (EditText) findViewById(R.id.editTextFeatureLevel);
        String stringCurrentLevel = "" + product.getLevelOfAFeature(feature);
        editTextLevel.setText(stringCurrentLevel);

        TextView textView = (TextView) findViewById(R.id.textViewFeatureName);
        textView.setText(feature.getName());

        setOkAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.addFeature(feature, Integer.parseInt(editTextLevel.getText().toString()));
                onFeatureChanged.callBack();
                dismiss();
            }
        });
    }
}
