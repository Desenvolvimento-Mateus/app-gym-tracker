package com.mateusavila.gymtracker.ui

import androidx.annotation.StringRes
import com.mateusavila.gymtracker.databinding.ViewStatTileBinding

/** Fills the reusable stat tile component (view_stat_tile.xml). */
fun ViewStatTileBinding.bind(value: String, @StringRes labelRes: Int) {
    statValue.text = value
    statLabel.setText(labelRes)
}
