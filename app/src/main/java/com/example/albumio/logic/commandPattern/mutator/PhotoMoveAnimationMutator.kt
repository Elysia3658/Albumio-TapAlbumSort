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
     * TODO: 动画很粗糙，后续需进行更改
     */

    override fun uiUndo(): PhotoUiState {
        // 撤回动画：从 endView 位置/中心（小尺寸）移动并缩放回 startView 的位置与尺寸
        // snapshot 已在 uiExecute 中保存
        val animView = ImageView(parentView.context)
        animView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(parentView.context).load(imageUri).into(animView)

        // 获取 end（动画起始位置，当前位于 endView）和 start（动画目标位置，原始 startView）的位置
        val fromLocation = IntArray(2)
        endView.getLocationInWindow(fromLocation)
        val toLocation = IntArray(2)
        startView.getLocationInWindow(toLocation)

        // 使用 endView 的左上角和真实尺寸，避免 1px 纯色问题
        val fromX = fromLocation[0].toFloat()
        val fromY = fromLocation[1].toFloat()
        val fromWidth = endView.width.toFloat().coerceAtLeast(1f)
        val fromHeight = endView.height.toFloat().coerceAtLeast(1f)

        // 目标尺寸只放大到中间值
        val targetWidth = fromWidth + (startView.width.toFloat().coerceAtLeast(1f) - fromWidth) * 0.5f
        val targetHeight = fromHeight + (startView.height.toFloat().coerceAtLeast(1f) - fromHeight) * 0.5f

        // 为中间尺寸居中定位到 startView
        val toX = toLocation[0].toFloat() + (startView.width - targetWidth) / 2f
        val toY = toLocation[1].toFloat() + (startView.height - targetHeight) / 2f

        animView.x = fromX
        animView.y = fromY
        val params = ViewGroup.LayoutParams(fromWidth.toInt(), fromHeight.toInt())
        animView.layoutParams = params
        parentView.addView(animView)
        animView.bringToFront()
        animView.requestLayout()

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            animView.x = fromX + (toX - fromX) * fraction
            animView.y = fromY + (toY - fromY) * fraction
            animView.layoutParams.width = (fromWidth + (targetWidth - fromWidth) * fraction).toInt().coerceAtLeast(1)
            animView.layoutParams.height = (fromHeight + (targetHeight - fromHeight) * fraction).toInt().coerceAtLeast(1)
            animView.requestLayout()
        }

        animator.doOnEnd {
            parentView.removeView(animView)
        }
        animator.start()

        return snapshot
    }
}
