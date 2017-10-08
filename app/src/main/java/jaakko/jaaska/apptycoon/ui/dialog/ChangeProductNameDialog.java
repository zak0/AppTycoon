package jaakko.jaaska.apptycoon.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.product.Product;

/**
 * Dialog for changing or setting the name of a product.
 */

public class ChangeProductNameDialog extends AppTycoonDialog {

    /**
     * @param product Product that's being edited
     * @param context Activity context
     * @param onNameChanged Callback that's called after the name has been changed
     */
    public ChangeProductNameDialog(final Product product, Context context, final CustomCallback onNameChanged) {
        super(context,
                R.layout.dialog_change_product_name,
                "Set product name");
        final EditText editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.setText(product.getName());
        setOkAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setName(editTextName.getText().toString());
                onNameChanged.callBack();
                dismiss();
            }
        });
    }
}
