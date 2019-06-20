package hll.zpf.starttravel.common.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RelativeLayout
import hll.zpf.starttravel.R
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView


/**
 * 上图片下文字的view
 */
class ITButton @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defAttrStyle: Int = 0) : RelativeLayout(context, attributeSet, defAttrStyle) {

    private lateinit var mContext:Context
    private lateinit var mTextView:TextView
    private lateinit var mImageView:ImageView
    private var imageNotSelectId:Int = 0
    private var imageId:Int = 0
    private var textColor:Int = 0
    private var textNotSelectColor:Int = 0
    private var mSelect:Boolean = false

    init {
        initAttrs(context,attributeSet,defAttrStyle)
    }



    private fun initAttrs(context: Context, attributeSet: AttributeSet?, defAttrStyle: Int) {

        mContext = context
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView = inflater.inflate(R.layout.text_image_button, this, true)
        mTextView = mView.findViewById(R.id.text)
        mImageView = mView.findViewById(R.id.image)


        val array = context.obtainStyledAttributes(attributeSet, R.styleable.ITButtonLayout, defAttrStyle, 0)
        imageId = array.getResourceId(R.styleable.ITButtonLayout_image, 0)
        imageNotSelectId = array.getResourceId(R.styleable.ITButtonLayout_image_not_select, 0)
        val text = array.getString(R.styleable.ITButtonLayout_text)
        val textSize = array.getDimension(
            R.styleable.ITButtonLayout_text_size,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
        )
        val imageWidth = array.getDimension( R.styleable.ITButtonLayout_image_width,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
        )
        val imageHeight = array.getDimension( R.styleable.ITButtonLayout_image_height,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
        )
        textColor = array.getColor(R.styleable.ITButtonLayout_text_color, Color.rgb(255, 255, 255))
        textNotSelectColor = array.getColor(R.styleable.ITButtonLayout_text_not_select_color, Color.rgb(255, 255, 255))

        setSelect(array.getBoolean(R.styleable.ITButtonLayout_selected,false))
        mTextView.text = text
        mTextView.textSize = textSize / context.resources.displayMetrics.density
        var layoutParams = mImageView.layoutParams
        layoutParams.height = imageHeight.toInt()
        layoutParams.width = imageWidth.toInt()
        mImageView.layoutParams = layoutParams

    }


    fun setSelect(isSelect:Boolean){
        mSelect = isSelect
        if(imageId == 0 || imageNotSelectId == 0){
            return
        }
        if (isSelect) {
            mImageView.setImageResource(imageId)
            mTextView.setTextColor(textColor)
        }else{
            mImageView.setImageResource(imageNotSelectId)
            mTextView.setTextColor(textNotSelectColor)
        }
    }

    fun isSelect():Boolean{
        return mSelect
    }


}