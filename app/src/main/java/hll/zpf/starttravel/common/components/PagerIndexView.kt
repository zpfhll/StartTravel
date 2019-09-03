package hll.zpf.starttravel.common.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import hll.zpf.starttravel.R

class PagerIndexView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mNumber:Int = 0
    private var mCurrentIndex:Int = 0
    private var mSelectedImage:Int = 0
    private var mNotSelectImage:Int = 0
    private var mIndexWidth:Float = 0f
    private var mIndexHeight:Float = 0f
    private var mDefault:Float = 10f
    private var mPadding:Float = 0f

    var itemOnClick:((Int) -> Unit)? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.Pager_Index_View, defStyleAttr, 0)
        mNumber = array.getInt(R.styleable.Pager_Index_View_number,0)
        mCurrentIndex = array.getInt(R.styleable.Pager_Index_View_current_index,0)
        mSelectedImage = array.getResourceId(R.styleable.Pager_Index_View_index_selected_image,R.mipmap.index_default_selected)
        mNotSelectImage = array.getResourceId(R.styleable.Pager_Index_View_index_not_select_image,R.mipmap.index_default_not_select)
        mIndexWidth = array.getDimension(R.styleable.Pager_Index_View_index_item_width,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDefault, resources.displayMetrics))
        mIndexHeight = array.getDimension(R.styleable.Pager_Index_View_index_item_height,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDefault, resources.displayMetrics))
        mPadding = array.getDimension(R.styleable.Pager_Index_View_index_padding,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics))

        if(mNumber < 1){
            this.visibility = View.GONE
        }else{
            this.orientation = HORIZONTAL
            this.gravity = Gravity.CENTER
            val padding = mPadding.toInt() / 2
            for(i in 1..mNumber){
                val image = ImageView(context)
                if(i-1 == mCurrentIndex){
                    image.setImageResource(mSelectedImage)
                }else{
                    image.setImageResource(mNotSelectImage)
                }
                val layoutParams = LayoutParams(mIndexWidth.toInt(),mIndexHeight.toInt())
                layoutParams.marginEnd = padding
                layoutParams.marginStart = padding
                image.layoutParams = layoutParams
                image.tag = i - 1
                image.setOnClickListener {item ->
                    itemOnClick?.let {
                        it(item.tag as Int)
                    }
                }
                this.addView(image)
            }
        }
    }

    fun refresh(number:Int,currentItem:Int){
        this.removeAllViews()
        mNumber = number
        mCurrentIndex = currentItem
        if(mNumber < 1){
            this.visibility = View.GONE
        }else{
            this.orientation = HORIZONTAL
            this.gravity = Gravity.CENTER
            val padding = mPadding.toInt() / 2
            for(i in 1..mNumber){
                val image = ImageView(context)
                if(i-1 == mCurrentIndex){
                    image.setImageResource(mSelectedImage)
                }else{
                    image.setImageResource(mNotSelectImage)
                }
                val layoutParams = LayoutParams(mIndexWidth.toInt(),mIndexHeight.toInt())
                layoutParams.marginEnd = padding
                layoutParams.marginStart = padding
                image.layoutParams = layoutParams
                image.tag = i - 1
                image.setOnClickListener {item ->
                    itemOnClick?.let {
                        it(item.tag as Int)
                    }
                }
                this.addView(image)
            }
        }
    }

    fun changeState(currentItem:Int){
        mCurrentIndex = currentItem
        for (i in 0 until childCount){
            if(i == mCurrentIndex){
                (getChildAt(i) as ImageView).setImageResource(mSelectedImage)
            }else{
                (getChildAt(i) as ImageView).setImageResource(mNotSelectImage)
            }
        }
    }





}