package com.iscreammedia.app.hiclassapkstore.util

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
fun setVisibilityView(view: View, visible: Boolean?) {
    view.isVisible = visible == true
}