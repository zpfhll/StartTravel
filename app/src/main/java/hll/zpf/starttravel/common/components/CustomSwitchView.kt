package hll.zpf.starttravel.common.components

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.R

class CustomSwitchView @JvmOverloads constructor(context: Context, attrs:  AttributeSet? = null, defAttrStyle: Int = 0) : ConstraintLayout(context, attrs,defAttrStyle) {

    private lateinit var mLeftTextView: TextView
    private lateinit var mRightTextView: TextView
    private lateinit var mSelectView:View
    private lateinit var mView:View
    private lateinit var mContext: Context

    private  var selectedTextColor:Int = 0
    private  var  unselectedTextColor:Int = 0

    var isLeft:Boolean = true

    var switchAction:((Boolean) -> Unit)? = null


    init {
        initAttrs(context,attrs,defAttrStyle)
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?, defAttrStyle: Int){
        mContext = context
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.custom_switch_layout, this, true)
        mLeftTextView = mView.findViewById(R.id.switch_left)
        mRightTextView = mView.findViewById(R.id.switch_right)
        mSelectView = mView.findViewById(R.id.switch_selected_view)

        val array = context.obtainStyledAttributes(attrs, R.styleable.Custom_Switch_View, defAttrStyle, 0)

        val leftText = array.getString(R.styleable.Custom_Switch_View_left_text)
        val rightText = array.getString(R.styleable.Custom_Switch_View_right_text)
        mLeftTextView.text = leftText
        mRightTextView.text = rightText

        val leftTextSize = array.getDimension(
            R.styleable.Custom_Switch_View_left_text_size,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics)
        )
        mLeftTextView.textSize = leftTextSize / resources.displayMetrics.density
        val rightTextSize = array.getDimension(
            R.styleable.Custom_Switch_View_right_text_size,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics)
        )
        mRightTextView.textSize = rightTextSize / resources.displayMetrics.density

        selectedTextColor = array.getColor(R.styleable.Custom_Switch_View_selected_text_color, Color.rgb(255, 255, 255))
        unselectedTextColor = array.getColor(R.styleable.Custom_Switch_View_unselected_text_color, Color.rgb(85, 200, 185))
        isLeft = array.getBoolean(R.styleable.Custom_Switch_View_switch_init_left,true)

        if (isLeft) {
            mLeftTextView.setTextColor(selectedTextColor)
            mRightTextView.setTextColor(unselectedTextColor)
        }else{
            val animator = ObjectAnimator.ofFloat(mSelectView, "translationX", 0f, mSelectView.width.toFloat())
            animator.duration = 0
            animator.start()
            mLeftTextView.setTextColor(unselectedTextColor)
            mRightTextView.setTextColor(selectedTextColor)
        }

        val switchBackgroundColor = array.getResourceId(R.styleable.Custom_Switch_View_switch_background_color,R.drawable.circle_white)
        mView.setBackgroundResource(switchBackgroundColor)

        val selectedViewColor = array.getResourceId(R.styleable.Custom_Switch_View_selected_view_color, R.drawable.button_background3)
        mSelectView.setBackgroundResource(selectedViewColor)


        mLeftTextView.setOnClickListener {
            clickAction(it)
        }
        mRightTextView.setOnClickListener {
            clickAction(it)
        }
    }
    private fun clickAction(view:View){
        when (view.id) {
            R.id.switch_left -> {
                selectLeft(true)
            }
            R.id.switch_right -> {
                selectRight(true)
            }

        }
    }


    /**
     * 设定按钮文本
     * leftText:左侧文本
     * rightText：右侧文本
     */
    fun setText(leftText:String,rightText:String){
        mLeftTextView.text = leftText
        mRightTextView.text = rightText
    }

    /**
     * 设定文本的文字字号（DP）
     * leftTextSize:左侧文本字号
     * rightTextSize：右侧文本字号
     */
    fun setTextSize(leftTextSize:Float,rightTextSize:Float){
        mLeftTextView.textSize =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftTextSize, resources.displayMetrics) / resources.displayMetrics.density
        mRightTextView.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightTextSize, resources.displayMetrics) / resources.displayMetrics.density
    }

    /**
     * 设定文本颜色
     * selectedColor：选中的文本颜色
     * unselectedColor：为选中的文本颜色
     */
    fun setTextColor(selectedColor:Int,unselectedColor:Int){
        selectedTextColor = selectedColor
        unselectedTextColor = unselectedColor
        mLeftTextView.setTextColor(selectedTextColor)
        mRightTextView.setTextColor(unselectedTextColor)
    }

    /**
     * 设定选中项目和控件的背景颜色
     * selectedColorId:选中项目的背景颜色
     * backgroundColorId:控件的背景颜色
     */
    fun setSelectedAndBackgroundColorResourceId(selectedColorId:Int?,backgroundColorId:Int?){
        selectedColorId?.let {
            mSelectView.setBackgroundResource(it)
        }

        backgroundColorId?.let {
            mView.setBackgroundResource(it)
        }
    }


    /**
     * 选择左侧
     * anim：是否要动画
     */
    fun selectLeft(anim:Boolean){
        if (isLeft) {
            return
        }
        val duration:Long = if (anim) 300 else 0
        val animator = ObjectAnimator.ofFloat(mSelectView, "translationX", mSelectView.translationX, 0f)
        animator.duration = duration
        animator.start()
        isLeft = true
        if (isLeft) {
            mLeftTextView.setTextColor(selectedTextColor)
            mRightTextView.setTextColor(unselectedTextColor)
        } else {
            mLeftTextView.setTextColor(unselectedTextColor)
            mRightTextView.setTextColor(selectedTextColor)
        }
        switchAction?.let {
            it(isLeft)
        }
    }

    /**
     * 选择右侧
     * anim：是否要动画
     */
    fun selectRight(anim:Boolean){
        if (!isLeft) {
            return
        }
        val duration:Long = if (anim) 300 else 0
        var animator = ObjectAnimator.ofFloat(mSelectView, "translationX", 0f, mSelectView.width.toFloat())
        animator.duration = duration
        animator.start()
        isLeft = false
        if (isLeft) {
            mLeftTextView.setTextColor(selectedTextColor)
            mRightTextView.setTextColor(unselectedTextColor)
        }else{
            mLeftTextView.setTextColor(unselectedTextColor)
            mRightTextView.setTextColor(selectedTextColor)
        }
        switchAction?.let {
            it(isLeft)
        }
    }


}