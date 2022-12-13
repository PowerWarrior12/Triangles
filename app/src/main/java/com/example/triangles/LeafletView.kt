package com.example.triangles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.applyCanvas
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

private const val MEASURED_ERROR = "Error with measured"

@SuppressLint("CustomViewStyleable")
class LeafletView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    @ColorInt
    private var mainColor: Int = Color.RED

    @ColorInt
    private var textColor: Int = Color.RED

    private val mainPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var leafHeight: Int = 400
    private val leafWidth: Int
        get() = (leafHeight / 3f + textWidth*2 + textHeight).toInt()

    private val calculateViewHeight: Int
        get() = (leafHeight + leafWidth / 2 + textHeight + 400).toInt()

    private val calculateViewWidth: Int
        get() = (leafHeight * 2 + textWidth * 2).toInt()

    private var strokeSize = 10f

    private val rectView by lazy { Rect(0, 0, width, height) }

    private val leafBitmap: Bitmap?

    private lateinit var leaf: List<Leaf>

    init {
        context.obtainStyledAttributes(attrs, R.styleable.triangle).apply {
            try {
                mainColor = getColor(R.styleable.triangle_main_color, Color.RED)
                textColor = getColor(R.styleable.triangle_text_color, Color.BLACK)
            } finally {
                recycle()
            }
        }

        mainPaint.apply {
            color = mainColor
            style = Paint.Style.STROKE
        }
        textPaint.apply {
            color = textColor
            textSize = 25f
        }
        leafBitmap = ContextCompat.getDrawable(context, R.drawable.leaf)?.toBitmap()
    }

    private val textHeight = calculateTextHeight(textPaint, "A")
    private val textWidth = calculateTextWidth(textPaint, "AA")

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = calculateDefaultSize(calculateViewHeight, heightMeasureSpec)
        leafHeight = (height - textHeight * 4).toInt()
        val width = calculateDefaultSize(calculateViewWidth, calculateViewWidth)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        prepareLeafBitmaps()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            leaf.forEach { leaf ->
                leaf.draw(it)
            }
        }
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

    private fun prepareLeafBitmaps() {
        leaf = listOf(
            //Leaf(leafHeight, leafWidth, 90),
            //Leaf(leafHeight, leafWidth, -45),
            //Leaf(leafHeight, leafWidth, -90),
            //Leaf(leafHeight, leafWidth, 45),
            Leaf(leafHeight, leafWidth, 0),
        )
        strokeSize = height/80f
        mainPaint.apply {
            strokeWidth = strokeSize
        }
        leaf.forEach { leaf ->
            leaf.prepare()
        }
    }

    private inner class Leaf(
        private val leafHeight: Int,
        private val leafWidth: Int,
        private val angle: Int
    ) {
        private val leafResultBitmap: Bitmap by lazy {
            Bitmap.createBitmap(
                leafWidth,
                leafHeight,
                Bitmap.Config.ALPHA_8
            )
        }
        private val textBitmap: Bitmap by lazy { Bitmap.createBitmap(leafWidth, leafHeight, Bitmap.Config.ALPHA_8) }
        private val rect = RectF(
            width / 2f - leafWidth / 2, 0f, width / 2f + leafWidth / 2,
            height - leafWidth / 2f + textHeight + 400
        )

        private var textUpper = ""
        private var textLeftLower = ""
        private var textRightLower = ""
        private var textInnerLower = ""
        private var textInnerLeftUpper = ""
        private var textInnerRightUpper = ""

        fun prepare() {
            prepareLeafBitmap()
            prepareTextBitmap()
        }

        fun draw(canvas: Canvas) {
            canvas.apply {
                drawRect(rect, Paint().apply {
                    color = Color.BLACK
                    style = Paint.Style.STROKE
                    strokeWidth = 5f
                })
                save()
                rotate(
                    angle.toFloat(),
                    width / 2f,
                    height - leafWidth/2f
                )
                drawBitmap(leafResultBitmap, null, rect, mainPaint)
                drawBitmap(textBitmap, null, rect, textPaint)
                restore()
            }
        }

        fun setUpperText(text: String) {
            textUpper = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        fun setLeftLowerText(text: String) {
            textLeftLower = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        fun setRightLowerText(text: String) {
            textRightLower = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        fun setInnerLowerText(text: String) {
            textInnerLower = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        fun setInnerLeftUpperText(text: String) {
            textInnerLeftUpper = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        fun setInnerRightUpperText(text: String) {
            textInnerRightUpper = text
            requestLayout()
            prepareTextBitmap()
            invalidate()
        }

        private fun prepareLeafBitmap() {
            leafResultBitmap.eraseColor(Color.TRANSPARENT)
            leafResultBitmap.applyCanvas {
                save()
                drawLeaf(this)
                rotate(
                    90f,
                    width / 2f,
                    height - textHeight * 2
                )
            }
        }

        private fun prepareTextBitmap() {
            textBitmap.eraseColor(Color.TRANSPARENT)
            textBitmap.applyCanvas {
                drawPoints(this)
            }
        }

        private fun drawLeaf(canvas: Canvas) {
            val circleSize = textWidth/4

            val rect = RectF(
                circleSize + textWidth,
                circleSize + textHeight,
                circleSize + textWidth + leafHeight/3f,
                leafHeight - circleSize
            )
            if (leafBitmap != null) canvas.drawBitmap(leafBitmap, null, rect, mainPaint)
            val path = Path()
            path.addCircle(leafWidth / 2f, circleSize * 1.5f + textHeight, circleSize, Path.Direction.CCW)
            path.addCircle(leafWidth / 2f, leafHeight - circleSize, circleSize, Path.Direction.CCW)
            path.addCircle(circleSize + textWidth, leafHeight / 2f + textHeight*2, circleSize, Path.Direction.CCW)
            path.addCircle(leafWidth.toFloat() - circleSize - textWidth, leafHeight / 2f, circleSize, Path.Direction.CCW)
            path.addCircle(leafWidth / 2f, leafHeight / 2f + textHeight*2, circleSize, Path.Direction.CCW)

            path.addCircle(leafWidth / 2f * 0.24f + textWidth, leafHeight / 4f + textHeight, circleSize, Path.Direction.CCW)
            path.addCircle(leafWidth - leafWidth / 2f * 0.24f - textWidth, leafHeight / 4f + textHeight, circleSize, Path.Direction.CCW)
            mainPaint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawPath(path, mainPaint)
            mainPaint.style = Paint.Style.STROKE
        }

        private fun drawPoints(canvas: Canvas) {
            canvas.apply {
                var textWidth = calculateTextWidth(textPaint, textUpper)
                drawText(textUpper, width / 2f - textWidth / 2, textHeight, textPaint)

                textWidth = calculateTextWidth(textPaint, textLeftLower)
                drawText(textLeftLower, 0f, height.toFloat(), textPaint)

                textWidth = calculateTextWidth(textPaint, textRightLower)
                drawText(textLeftLower, width.toFloat() - textWidth, height.toFloat(), textPaint)

                textWidth = calculateTextWidth(textPaint, textInnerLower)
                drawText(textInnerLower, width / 2f - textWidth / 2, height.toFloat(), textPaint)

                textWidth = calculateTextWidth(textPaint, textInnerLeftUpper)
                drawText(textInnerLeftUpper, width / 4f - textWidth * 2f, height / 2f, textPaint)

                textWidth = calculateTextWidth(textPaint, textInnerRightUpper)
                drawText(textInnerRightUpper, width * 3 / 4f + textWidth, height / 2f, textPaint)
            }
        }
    }
}