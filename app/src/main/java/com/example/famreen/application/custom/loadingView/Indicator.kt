package com.example.famreen.application.custom.loadingView

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import java.util.*

abstract class Indicator : Drawable(),Animatable{
    private val mUpdateListeners = HashMap<ValueAnimator, AnimatorUpdateListener>()
    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var mAlpha = 255
    private var mDrawBounds =  Rect()

    private var mHasAnimators = false

    private val mPaint by lazy {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        return@lazy paint
    }

    protected abstract fun draw(canvas: Canvas?, paint: Paint)

    protected abstract fun onCreateAnimators(): ArrayList<ValueAnimator>?

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
    }

    override fun getAlpha(): Int {
        return mAlpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun draw(canvas: Canvas?) {
        draw(canvas, mPaint)
    }

    override fun start() {
        ensureAnimators()
        if (mAnimators == null) return
        if (isStarted()) return
        startAnimators()
        invalidateSelf()
    }
    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        setDrawBounds(bounds)
    }
    override fun isRunning(): Boolean {
        for (animator in mAnimators!!) {
            return animator.isRunning
        }
        return false
    }
    override fun stop() {
        stopAnimators()
    }

    private fun startAnimators() {
        mAnimators?.let {
            for (i in it.indices) {
                val animator = it[i]
                val updateListener = mUpdateListeners[animator]
                if (updateListener != null) {
                    animator.addUpdateListener(updateListener)
                }
                animator.start()
            }
        }
    }

    private fun stopAnimators() {
        mAnimators?.let {
            for (animator in it) {
                if (animator.isStarted) {
                    animator.removeAllUpdateListeners()
                    animator.end()
                }
            }
        }
    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    private fun isStarted(): Boolean {
        mAnimators?.let {
            for (animator in it) {
                return animator.isStarted
            }
        }
        return false
    }

    private fun setDrawBounds(drawBounds: Rect) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom)
    }

    private fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        mDrawBounds = Rect(left, top, right, bottom)
    }
    /**
     * */
    fun addUpdateListener(animator: ValueAnimator, updateListener: AnimatorUpdateListener) {
        mUpdateListeners[animator] = updateListener
    }

    fun postInvalidate() {
        invalidateSelf()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    fun getWidth(): Int {
        return mDrawBounds.width()
    }

    fun getHeight(): Int {
        return mDrawBounds.height()
    }

}