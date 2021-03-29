package com.emmanuelkehinde.twittasave.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
