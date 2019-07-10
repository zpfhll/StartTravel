package hll.zpf.starttravel.common.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import hll.zpf.starttravel.R

/**
 * 圆角的图片view
 */
class CRImageView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defAttrStyle: Int = 0) : ImageView(context, attributeSet, defAttrStyle)  {

    private val defaultRadius = 0
    private var radius = 0
    private var leftTopRadius = 0
    private var rightTopRadius = 0
    private var rightBottomRadius = 0
    private var leftBottomRadius = 0
    init {
        // 读取配置
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.Circle_Rectangle_Image_View)
        radius = array.getDimensionPixelOffset(R.styleable.Circle_Rectangle_Image_View_radius, defaultRadius)
        leftTopRadius = array.getDimensionPixelOffset(R.styleable.Circle_Rectangle_Image_View_left_top_radius, defaultRadius)
        rightTopRadius = array.getDimensionPixelOffset(R.styleable.Circle_Rectangle_Image_View_right_top_radius, defaultRadius)
        rightBottomRadius = array.getDimensionPixelOffset(R.styleable.Circle_Rectangle_Image_View_right_bottom_radius, defaultRadius)
        leftBottomRadius = array.getDimensionPixelOffset(R.styleable.Circle_Rectangle_Image_View_left_bottom_radius, defaultRadius)

        //如果四个角的值没有设置，那么就使用通用的radius的值。
        if (defaultRadius == leftTopRadius) {
            leftTopRadius = radius
        }
        if (defaultRadius == rightTopRadius) {
            rightTopRadius = radius
        }
        if (defaultRadius == rightBottomRadius) {
            rightBottomRadius = radius
        }
        if (defaultRadius == leftBottomRadius) {
            leftBottomRadius = radius
        }
        array.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val path = Path()
        //四个角：右上，右下，左下，左上
        path.moveTo(leftTopRadius.toFloat(), 0f)
        path.lineTo((width - rightTopRadius).toFloat(), 0f)
        path.quadTo(width.toFloat(), 0f, width.toFloat(), rightTopRadius.toFloat())

        path.lineTo(width.toFloat(), (height - rightBottomRadius).toFloat())
        path.quadTo(width.toFloat(), height.toFloat(), (width - rightBottomRadius).toFloat(), height.toFloat())

        path.lineTo(leftBottomRadius.toFloat(), height.toFloat())
        path.quadTo(0f, height.toFloat(), 0f, (height - leftBottomRadius).toFloat())

        path.lineTo(0f, leftTopRadius.toFloat())
        path.quadTo(0f, 0f, leftTopRadius.toFloat(), 0f)

        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}
