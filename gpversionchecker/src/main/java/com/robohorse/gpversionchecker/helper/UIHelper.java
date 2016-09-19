package com.robohorse.gpversionchecker.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.robohorse.gpversionchecker.R;
import com.robohorse.gpversionchecker.domain.Version;

/**
 * Created by robohorse on 06.03.16.
 */
public class UIHelper {

    public void showInfoView(final Activity activity, final Version version) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialogOnUIThread(activity, version);
            }
        });
    }

    private void showDialogOnUIThread(final Context context, Version version) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gpvch_layout_dialog, null);
        bindVersionData(view, version, context);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.gpvch_header)
                .setView(view)
                .setPositiveButton(R.string.gpvch_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openGooglePlay(context);
                    }
                })
                .setNegativeButton(R.string.gpvch_button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }

    private void bindVersionData(View view, Version version, Context context) {
        TextView tvVersion = (TextView) view.findViewById(R.id.tvVersionCode);
        tvVersion.setText(context.getString(R.string.app_name) + ": " + version.getNewVersionCode());

        TextView tvNews = (TextView) view.findViewById(R.id.tvChanges);
        String lastChanges = version.getChanges();

        if (!TextUtils.isEmpty(lastChanges)) {
            tvNews.setText(lastChanges);
        } else {
            view.findViewById(R.id.lnChangesInfo).setVisibility(View.GONE);
        }
    }

    private void openGooglePlay(Context context) {
        final String packageName = context.getApplicationContext().getPackageName();
        final String url = context.getString(R.string.gpvch_google_play_url) + packageName;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
