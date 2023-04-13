package com.example.loadingapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
    }

    private var widthSize = 0
    private var heightSize = 0
    private var downloadWidth = 0
    private var pCanvas: Canvas? = null
    private var f = false
    private var buttonText = "Download"
    private var background_color = 0
    private var textColor = 0
    private var loading_color = 0

    private val valueAnimator = ValueAnimator.ofInt(0, 360).apply {
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        duration = 1000
        addUpdateListener { animation ->
            downloadWidth = animation.animatedValue.toString().toInt()
            invalidate()
        }
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        downloadWidth = 0
        if (new == ButtonState.Completed) {
            buttonText = "download"
            valueAnimator.cancel()
        } else {
            buttonText = "downloading"
            valueAnimator.start()
        }
    }
    enum class ButtonState {
        Completed, Loading
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            background_color = getColor(R.styleable.LoadingButton_background_color, 0)
            loading_color = getColor(R.styleable.LoadingButton_loading_color, 0)
            textColor = getColor(R.styleable.LoadingButton_text_color, 0)
        }

        isClickable = true

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawLoading(canvas)
        drawText(canvas)
        drawArc(canvas)

        invalidate()
    }

    private fun drawBackground(canvas: Canvas?) {
        backgroundPaint.color = background_color
        canvas?.drawRect(RectF(0F, 0F, widthSize.toFloat(), heightSize.toFloat()), backgroundPaint)
    }

    private fun drawLoading(canvas: Canvas?) {
        loadingPaint.color = loading_color
        canvas?.drawRect(RectF(0F, 0F, (downloadWidth * widthSize) / 360F, heightSize.toFloat()), loadingPaint)
    }

    private fun drawText(canvas: Canvas?) {
        textPaint.color = textColor
        canvas?.drawText(buttonText, widthSize / 2F, heightSize / 2F, textPaint)
    }

    private fun drawArc(canvas: Canvas?) {
        canvas?.drawArc(RectF(480F, 40F, 520F, 80F), 0F, downloadWidth.toFloat(), true, arcPaint)
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw = paddingLeft + paddingRight + suggestedMinimumWidth
        val w =View.resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h = View.resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
        widthSize = w
        heightSize = h
    }
}
