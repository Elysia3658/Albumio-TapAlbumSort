package com.example.albumio.logic.commandPattern.mutator

import android.animation.ValueAnimator
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.bumptech.glide.Glide
import com.example.albumio.logic.commandPattern.UiMutator
import com.example.albumio.logic.data.PhotoUiState

class PhotoMoveAnimationMutator(
    private val startView: View,
    private val endView: View,
    private val parentView: ViewGroup,
    private val imageUri: Uri
) : UiMutator<PhotoUiState> {

    private lateinit var snapshot: PhotoUiState

    override fun uiExecute(oldState: PhotoUiState): PhotoUiState {

        snapshot = oldState

        val animView = ImageView(parentView.context)
        animView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(parentView.context).load(imageUri).into(animView)

        val startLocation = IntArray(2)
        startView.getLocationInWindow(startLocation)
        val endLocation = IntArray(2)
        endView.getLocationInWindow(endLocation)

        val startX = startLocation[0].toFloat()
        val startY = startLocation[1].toFloat()
        val startWidth = startView.width.toFloat()
        val startHeight = startView.height.toFloat()

        val endX = endLocation[0].toFloat() + endView.width / 2
        val endY = endLocation[1].toFloat() + endView.height / 2
        val endWidth = 0f
        val endHeight = 0f

        animView.x = startX
        animView.y = startY
        val params = ViewGroup.LayoutParams(startWidth.toInt(), startHeight.toInt())
        animView.layoutParams = params
        parentView.addView(animView)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300 // Animation duration in milliseconds

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            animView.x = startX + (endX - startX) * fraction
            animView.y = startY + (endY - startY) * fraction
            animView.layoutParams.width = (startWidth + (endWidth - startWidth) * fraction).toInt()
            animView.layoutParams.height = (startHeight + (endHeight - startHeight) * fraction).toInt()
            animView.requestLayout()
        }

        animator.doOnEnd {
            parentView.removeView(animView)
        }
        animator.start()

        return oldState.copy(currentPage = oldState.currentPage + 1)
    }

    /**
     * TODO: 这里的动画可能会有一些绝对定位的问题，以及其被cardView覆盖的情况可能需要调整动画的层级关系
     */

    override fun uiUndo(): PhotoUiState {
        return snapshot
    }
}
