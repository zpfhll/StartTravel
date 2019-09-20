package hll.zpf.starttravel.common.components

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.R

class CustomCheckboxView @JvmOverloads constructor(context: Context, attrs:  AttributeSet? = null, defAttrStyle: Int = 0) : ConstraintLayout(context, attrs,defAttrStyle) {

    private lateinit var mCheckboxImage: TextView
    private lateinit var mCheckboxText: TextView
    private lateinit var mView:View
    private lateinit var mContext: Context

    var isChecked:Boolean = false

    var checkedImage:Int = 0
    var uncheckImage:Int = 0



    var checkCallback:((Boolean) -> Unit)? = null


    init {
        initAttrs(context,attrs,defAttrStyle)
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?, defAttrStyle: Int){
        mContext = context
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.custom_checkbox_layout, this, true)
        mCheckboxImage = mView.findViewById(R.id.checkbox_image)
        mCheckboxText = mView.findViewById(R.id.checkbox_text)
        val array = context.obtainStyledAttributes(attrs, R.styleable.Custom_checkbox_View, defAttrStyle, 0)


        val text = array.getString(R.styleable.Custom_checkbox_View_checkbox_text)
        val textColor = array.getColor(R.styleable.Custom_checkbox_View_checkbox_text_color,context.resources.getColor(R.color.text_color,null))
        val textSize = array.getDimension(R.styleable.Custom_checkbox_View_checkbox_text_size,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics))
        val textImagePadding = array.getDimension(R.styleable.Custom_checkbox_View_text_image_padding,0f)
        isChecked = array.getBoolean(R.styleable.Custom_checkbox_View_checked,false)
        checkedImage = array.getResourceId(R.styleable.Custom_checkbox_View_checked_image,R.mipmap.checkbox_checked_red)
        uncheckImage = array.getResourceId(R.styleable.Custom_checkbox_View_uncheck_image,R.mipmap.checkbox_red)
        val imageWidth = array.getDimension(R.styleable.Custom_checkbox_View_checkbox_image_width,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics))
        val imageHeight = array.getDimension(R.styleable.Custom_checkbox_View_checkbox_image_height,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics))

        val image = if (isChecked) checkedImage else uncheckImage
        mCheckboxImage.setBackgroundResource(image)
        mCheckboxText.text = text
        mCheckboxText.setTextColor(textColor)
        mCheckboxText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
        val textLayout = mCheckboxText.layoutParams as LayoutParams
        textLayout.leftMargin = textImagePadding.toInt()
        mCheckboxText.layoutParams = textLayout

        val checkboxLayout = mCheckboxImage.layoutParams as LayoutParams
        checkboxLayout.width = imageWidth.toInt()
        checkboxLayout.height = imageHeight.toInt()
        mCheckboxImage.layoutParams = checkboxLayout

        mView.setOnClickListener{_ ->
            isChecked = !isChecked
            mCheckboxImage.setBackgroundResource(if (isChecked) checkedImage else uncheckImage)
            checkCallback?.let {
                it(isChecked)
            }
        }

    }


    /**
     * 设定按钮文本
     * text:文本
     */
    fun setText(text:String){
        mCheckboxText.text = text
    }

    /**
     * 选择左侧
     * anim：是否要动画
     */
    fun check(checked:Boolean){
        isChecked = checked
        mCheckboxImage.setBackgroundResource(if (isChecked) checkedImage else uncheckImage)
        checkCallback?.let {
            it(checked)
        }
    }




}