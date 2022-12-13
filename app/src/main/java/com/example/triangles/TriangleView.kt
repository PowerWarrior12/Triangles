package com.example.triangles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.applyCanvas


private const val MEASURED_ERROR = "Error with measured"
private const val STROKE_SIZE = 10f

@SuppressLint("CustomViewStyleable")
class TriangleView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    @ColorInt
    private var mainColor: Int = Color.RED

    @ColorInt
    private var textColor: Int = Color.RED

    private var gravity: Gravity = Gravity.CENTER

    private val mainPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val calculateViewHeight
        get() = (400 + textHeight * 4).toInt()

    private val calculateViewWidth
        get() = 400

    private val rectView by lazy { Rect(0, 0, width, height) }

    private val mainBitmap: Bitmap by lazy { Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8) }
    private val pointsBitmap: Bitmap by lazy { Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8) }

    private var textUpper = ""
    private var textLeftLower = ""
    private var textRightLower = ""
    private var textInnerLower = ""
    private var textInnerLeftUpper = ""
    private var textInnerRightUpper = ""

    init {
        context.obtainStyledAttributes(attrs, R.styleable.triangle).apply {
            try {
                mainColor = getColor(R.styleable.triangle_main_color, Color.RED)
                textColor = getColor(R.styleable.triangle_text_color, Color.BLACK)
                gravity = when(getInteger(R.styleable.triangle_placement, 3)) {
                    1 -> Gravity.LEFT
                    2 -> Gravity.RIGHT
                    3 -> Gravity.CENTER
                    else -> Gravity.CENTER
                }

            } finally {
                recycle()
            }
        }

        mainPaint.apply {
            color = mainColor
            strokeWidth = STROKE_SIZE
            style = Paint.Style.STROKE
        }
        textPaint.apply {
            color = textColor
            textSize = 25f
        }
    }

    private val textHeight = calculateTextHeight(textPaint, "A")

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = calculateDefaultSize(calculateViewWidth, widthMeasureSpec)
        val height = calculateDefaultSize(calculateViewHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        prepareBitmapTriangle()
        prepareBitmapPoints()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawBitmap(mainBitmap, null, rectView, mainPaint)
            it.drawBitmap(pointsBitmap, null, rectView, textPaint)
        }
    }

    fun setUpperText(text: String) {
        textUpper = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    fun setLeftLowerText(text: String) {
        textLeftLower = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    fun setRightLowerText(text: String) {
        textRightLower = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    fun setInnerLowerText(text: String) {
        textInnerLower = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    fun setInnerLeftUpperText(text: String) {
        textInnerLeftUpper = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    fun setInnerRightUpperText(text: String) {
        textInnerRightUpper = text
        requestLayout()
        prepareBitmapPoints()
        invalidate()
    }

    private fun calculateDefaultSize(calculatingSize: Int, measureSpec: Int): Int {
        val measuredSize = MeasureSpec.getSize(measureSpec)

        val size = when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> calculatingSize
            MeasureSpec.EXACTLY -> measuredSize
            MeasureSpec.AT_MOST -> calculatingSize
            else -> error(MEASURED_ERROR)
        }

        return size
    }

    private fun prepareBitmapTriangle() {
        mainBitmap.eraseColor(Color.TRANSPARENT)
        mainBitmap.applyCanvas {
            drawTriangle(this)
        }
    }

    private fun prepareBitmapPoints() {
        pointsBitmap.eraseColor(Color.TRANSPARENT)
        pointsBitmap.applyCanvas {
            drawPoints(this)
        }
    }

    private fun drawTriangle(canvas: Canvas) {
        val path = Path()
        val STEP = STROKE_SIZE/5f
        //Main triangle
        path.moveTo(STEP, height.toFloat() - textHeight * 2)
        path.lineTo(width / 2f, textHeight * 2)
        path.lineTo(width.toFloat() - STEP, height.toFloat() - textHeight * 2)
        path.lineTo(STEP, height.toFloat() - textHeight * 2)
        //Inner triangle
        path.moveTo(width / 4f + STEP, height / 2f)
        path.lineTo(width * 3 / 4f - STEP, height / 2f)
        path.lineTo(width / 2f, height.toFloat() - textHeight * 2f)
        path.lineTo(width / 4f + STEP, height / 2f)

        canvas.drawPath(path, mainPaint)

        path.reset()

        val circleSize = STROKE_SIZE
        path.addCircle(width / 4f + STEP, height / 2f, circleSize, Path.Direction.CCW)
        path.addCircle(
            width * 3 / 4f - STEP,
            height / 2f,
            circleSize,
            Path.Direction.CCW
        )
        path.addCircle(width / 2f, height.toFloat() - textHeight * 2f, circleSize, Path.Direction.CCW)

        mainPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(path, mainPaint)
        mainPaint.style = Paint.Style.STROKE
    }

    private fun drawPoints(canvas: Canvas) {
        canvas.apply {
            var textWidth = calculateTextWidth(textPaint, textUpper)
            drawText(textUpper, width / 2f - textWidth / 2, textHeight, textPaint)

            textWidth = calculateTextWidth(textPaint, textLeftLower)
            if (gravity == Gravity.RIGHT || gravity == Gravity.CENTER) drawText(textLeftLower, 0f - textWidth/2, height.toFloat(), textPaint)
            else drawText(textLeftLower, 0f, height.toFloat(), textPaint)

            textWidth = calculateTextWidth(textPaint, textRightLower)
            if (gravity == Gravity.LEFT || gravity == Gravity.CENTER) drawText(textRightLower, width.toFloat() - textWidth/2, height.toFloat(), textPaint)
            else drawText(textLeftLower, width.toFloat() - textWidth, height.toFloat(), textPaint)

            textWidth = calculateTextWidth(textPaint, textInnerLower)
            drawText(textInnerLower, width / 2f - textWidth / 2, height.toFloat(), textPaint)

            textWidth = calculateTextWidth(textPaint, textInnerLeftUpper)
            drawText(textInnerLeftUpper, width / 4f - textWidth * 2f, height / 2f, textPaint)

            textWidth = calculateTextWidth(textPaint, textInnerRightUpper)
            drawText(textInnerRightUpper, width * 3 / 4f + textWidth, height / 2f, textPaint)
        }
    }

    private fun calculateTextWidth(paint: Paint, text: String): Float = paint.measureText(text)

    private fun calculateTextHeight(paint: Paint, text: String): Float {
        val mTextBoundRect = Rect()
        paint.getTextBounds(text, 0, text.length, mTextBoundRect)
        return mTextBoundRect.height().toFloat()
    }

    private enum class Gravity {
        LEFT, RIGHT, CENTER
    }
}