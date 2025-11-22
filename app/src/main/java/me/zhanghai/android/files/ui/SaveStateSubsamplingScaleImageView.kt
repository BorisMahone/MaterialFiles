/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.ui

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.parcelize.Parcelize
import me.zhanghai.android.files.util.ParcelableState

class SaveStateSubsamplingScaleImageView : SubsamplingScaleImageView {
    private var pendingState: ImageViewState? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet?) : super(context, attr)

    fun setImageRestoringSavedState(imageSource: ImageSource) {
        setImage(imageSource, pendingState)
        pendingState = null
    }

    override fun onSaveInstanceState(): Parcelable = State(super.onSaveInstanceState(), state)

    override fun onRestoreInstanceState(state: Parcelable) {
        state as State
        super.onRestoreInstanceState(state.superState)
        pendingState = state.state
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                if (canPanHorizontally()) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
        }
        return super.onTouchEvent(event)
    }

    private fun canPanHorizontally(): Boolean {
        if (!isReady) {
            return false
        }
        val viewWidth = width - paddingLeft - paddingRight
        if (viewWidth <= 0) {
            return false
        }
        val orientation = appliedOrientation
        val rotated90Or270 =
            orientation == ORIENTATION_90 || orientation == ORIENTATION_270
        val imageWidth = if (rotated90Or270) sHeight else sWidth
        val scaledImageWidth = imageWidth * scale
        if (scaledImageWidth <= viewWidth) {
            return false
        }
        val center = center ?: return false
        val halfVisibleWidthInSource = viewWidth / (2f * scale)
        val left = center.x - halfVisibleWidthInSource
        val right = center.x + halfVisibleWidthInSource
        return left > 0f || right < imageWidth
    }

    @Parcelize
    private class State(val superState: Parcelable?, val state: ImageViewState?) : ParcelableState
}
