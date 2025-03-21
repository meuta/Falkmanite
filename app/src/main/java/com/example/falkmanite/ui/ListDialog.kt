package com.example.falkmanite.ui

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import com.example.falkmanite.R
import com.example.falkmanite.domain.Playlist

class ListDialog(
    private val title: String,
    private val items: List<Playlist>,
    private val onConfirm: (Playlist) -> Unit = {}
) {

    fun show(context: Context) {
        AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle(title)
            .setItems(items.map { it.title }.toTypedArray()) { _, i -> onConfirm(items[i]) }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
