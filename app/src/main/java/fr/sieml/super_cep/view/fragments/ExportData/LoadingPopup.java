package fr.sieml.super_cep.view.fragments.ExportData;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.databinding.PopupExportDataBinding;

public class LoadingPopup extends View {

    public static LoadingPopup create(Context context) {
        return new LoadingPopup(context);
    }
    private final PopupExportDataBinding binding;
    private final PopupWindow pw;
    private LoadingPopup(Context context) {
        super(context);
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_export_data, null);
        binding = PopupExportDataBinding.bind(popupView);

        pw = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        pw.setAnimationStyle(R.style.Animation);
    }

    public void terminer(){
        binding.textViewPopUpExportDataExportEnCours.setText("Export terminé");
        binding.getRoot().removeView(binding.progressBar);
        pw.setFocusable(true);
        pw.update();
    }

    public void erreur(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Erreur lors de l'export");
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        builder.show();
        pw.dismiss();
    }
}
