package pe.edu.upc.wallpapeer.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import pe.edu.upc.wallpapeer.R;

public class LayersDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione su opción")
                .setItems(R.array.layers_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

//                        pSubOption = which;
//                        sendSelectedOption(pSelectedOption, pSubOption);
                    }
                });
        return builder.create();
    }
}
