package com.example.famreen.application.custom.loadingView

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.util.*

class CirclePulseIndicator : Indicator() {
    private var mScaleFloat = 1f
    private var mDegrees = 0f
    override fun draw(canvas: Canvas?, paint: Paint) {
        canvas?.let {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f

            val circleSpacing = 12f
            val x = getWidth() / 2.toFloat()
            val y = getHeight() / 2.toFloat()
            it.translate(x, y)
            it.scale(mScaleFloat, mScaleFloat)
            it.rotate(mDegrees)
            val rectF = RectF(
                -x + circleSpacing,
                -y + circleSpacing,
                0 + x - circleSpacing,
                0 + y - circleSpacing
            )
            it.drawArc(rectF, -45f, 270f, false, paint)
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val scaleAnim = ValueAnimator.ofFloat(1f, 0.95f, 0.85f, 1f)
        scaleAnim.duration = 2000
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim) {
            mScaleFloat = it.animatedValue as Float
            postInvalidate()
        }
        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        rotateAnim.duration = 2000
        rotateAnim.repeatCount = -1
        addUpdateListener(rotateAnim) {
            mDegrees = it.animatedValue as Float
            postInvalidate()
        }
        animators.add(scaleAnim)
        animators.add(rotateAnim)
        return animators
    }

}