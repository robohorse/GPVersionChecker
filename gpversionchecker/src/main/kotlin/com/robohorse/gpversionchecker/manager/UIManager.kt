package com.robohorse.gpversionchecker.manager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.robohorse.gpversionchecker.R
import com.robohorse.gpversionchecker.domain.Version

class UIManager {
    fun showInfoView(activity: Activity, version: Version) {
        activity.runOnUiThread { showDialogOnUIThread(activity, version) }
    }

    private fun showDialogOnUIThread(context: Context, version: Version) {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.gpvch_layout_dialog, null)
        bindVersionData(view, version, context)
        val dialog: AlertDialog = AlertDialog.Builder(context)
                .setTitle(R.string.gpvch_header)
                .setView(view)
                .setPositiveButton(R.string.gpvch_button_positive, { _, _ -> openGooglePlay(context) })
                .setNegativeButton(R.string.gpvch_button_negative, null)
                .create()
        dialog.show()
    }

    private fun bindVersionData(view: View, version: Version, context: Context) {
        val tvVersion = view.findViewById<View>(R.id.tvVersionCode) as TextView
        tvVersion.text = "${context.getString(R.string.app_name)}:${version.newVersionCode}"
        val tvNews = view.findViewById<View>(R.id.tvChanges) as TextView
        val lastChanges = version.changes
        if (!TextUtils.isEmpty(lastChanges)) {
            tvNews.text = lastChanges
        } else {
            view.findViewById<View>(R.id.lnChangesInfo).visibility = View.GONE
        }
    }

    private fun openGooglePlay(context: Context) {
        val packageName = context.applicationContext.packageName
        val url = context.getString(R.string.gpvch_google_play_url) + packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
