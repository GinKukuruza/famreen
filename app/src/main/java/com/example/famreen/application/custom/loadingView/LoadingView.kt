package com.example.famreen.application.custom.loadingView

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import com.example.famreen.R

class LoadingView : View {
    //Styles
    private val mLoadingIndicatorViewStyle = R.style.LoadingView
    private val mLoadingIndicatorViewStyleable = R.styleable.LoadingView
    //Values
    private var mMinHeight = 24
    private var mMinWidth = 24
    private var mMaxHeight = 48
    private var mMaxWidth = 48

    private var mShouldStartAnimationDrawable = false
    private var mIndicatorColor = Color.WHITE
    //Indicator
    private val mIndicator = CirclePulseIndicator() as Indicator

    constructor(context: Context?) : super(context) {
        init(context, null, 0, mLoadingIndicatorViewStyle)}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, mLoadingIndicatorViewStyle)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, mLoadingIndicatorViewStyle)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, mLoadingIndicatorViewStyle)}

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawTrack(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val d: Drawable = mIndicator
        var dw = mMinWidth.coerceAtLeast(mMaxWidth.coerceAtMost(d.intrinsicWidth))
        var dh = mMinHeight.coerceAtLeast(mMaxHeight.coerceAtMost(d.intrinsicHeight))

        updateDrawableState()

        dw += paddingLeft + paddingRight
        dh += paddingTop + paddingBottom

        val measuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0)
        val measuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawableState()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        mIndicator.setHotspot(x, y)
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }
    override fun verifyDrawable(who: Drawable): Boolean {
        return (who === mIndicator || super.verifyDrawable(who))
    }

    override fun setVisibility(v: Int) {
        if (visibility != v) {
            super.setVisibility(v)
            if (v == GONE || v == INVISIBLE) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun invalidateDrawable(dr: Drawable) {
        if (verifyDrawable(dr)) {
            val dirty = dr.bounds
            val scrollX = scrollX + paddingLeft
            val scrollY = scrollY + paddingTop
            invalidate(
                dirty.left + scrollX, dirty.top + scrollY,
                dirty.right + scrollX, dirty.bottom + scrollY
            )
        } else {
            super.invalidateDrawable(dr)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateDrawableBounds(w, h)
    }

    private fun init(
        context: Context?,
        attributeSet: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ){
        val a = context!!.obtainStyledAttributes(
            attributeSet,
            mLoadingIndicatorViewStyleable,
            defStyleAttr,
            defStyleRes
        )
        mMinWidth = a.getDimensionPixelSize(R.styleable.LoadingView_minWidth, mMinWidth)
        mMaxWidth = a.getDimensionPixelSize(R.styleable.LoadingView_maxWidth, mMaxWidth)
        mMinHeight = a.getDimensionPixelSize(R.styleable.LoadingView_minHeight, mMinHeight)
        mMaxHeight = a.getDimensionPixelSize(R.styleable.LoadingView_maxHeight, mMaxHeight)
        mIndicatorColor = a.getColor(R.styleable.LoadingView_indicatorColor, Color.WHITE)
        initIndicator()
        a.recycle()
    }

    private fun initIndicator(){
        setIndicatorColor(mIndicatorColor)
        mIndicator.callback = this
        postInvalidate()
    }

    private fun updateDrawableBounds(_w: Int, _h: Int) {
        var w = _w
        var h = _h
        w -= paddingRight + paddingLeft
        h -= paddingTop + paddingBottom
        var right = w
        var bottom = h
        var top = 0
        var left = 0
        val intrinsicWidth: Int = mIndicator.intrinsicWidth
        val intrinsicHeight: Int = mIndicator.intrinsicHeight
        val intrinsicAspect = intrinsicWidth.toFloat() / intrinsicHeight
        val boundAspect = w.toFloat() / h
        if (intrinsicAspect != boundAspect) {
            if (boundAspect > intrinsicAspect) {
                val width = (h * intrinsicAspect).toInt()
                left = (w - width) / 2
                right = left + width
            } else {
                val height = (w * (1 / intrinsicAspect)).toInt()
                top = (h - height) / 2
                bottom = top + height
            }
        }
        mIndicator.setBounds(left, top, right, bottom)
    }

    private fun updateDrawableState() {
        val state = drawableState
        if (mIndicator.isStateful) {
            mIndicator.state = state
        }
    }
    private fun drawTrack(canvas: Canvas) {
        val d: Drawable = mIndicator
        val saveCount = canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        d.draw(canvas)
        canvas.restoreToCount(saveCount)
        if (mShouldStartAnimationDrawable && d is Animatable) {
            (d as Animatable).start()
            mShouldStartAnimationDrawable = false
        }
    }
    private fun startAnimation() {
        if (visibility != VISIBLE) {
            return
        }
        mShouldStartAnimationDrawable = true
        postInvalidate()
    }

    private fun stopAnimation() {
        mIndicator.stop()
        mShouldStartAnimationDrawable = false
        postInvalidate()
    }
    private fun setIndicatorColor(color: Int) {
        mIndicatorColor = color
        mIndicator.setColor(color)
    }
    /**
     * Плавный показ view
     * **/
    fun smoothToShow() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        visibility = VISIBLE
    }
    /**
     * Плавное скрытие view
     * **/
    fun smoothToHide() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        visibility = GONE
    }
}