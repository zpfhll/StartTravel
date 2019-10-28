package hll.zpf.starttravel.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.components.CRImageView
import hll.zpf.starttravel.common.database.entity.Step

class TimeLineAdapter(context: Context, stepData: List<Step>, callback: ((Int,Int) -> Unit)) :
    RecyclerView.Adapter<TimeLineAdapter.StepItemViewHandler>() {

    var mStepData:List<Step> = stepData
    var mContext:Context = context
    var mCallback:((Int,Int) -> Unit) = callback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepItemViewHandler {
        val view = LayoutInflater.from(mContext).inflate(R.layout.time_line_item,parent,false)
        return StepItemViewHandler(view)
    }

    override fun getItemCount(): Int {
        return mStepData.size
    }

    override fun onBindViewHolder(holder: StepItemViewHandler, position: Int) {
        setView(holder,position)
    }

    private fun setView(holder:StepItemViewHandler,index:Int){
            var themeId = R.drawable.circle_green
            var itemBackgroundThemeId = R.drawable.circle_round_edge_green_15
            var textColor = R.color.baseColor
            var isLeft = false

            if(index == mStepData.size - 1){
                holder.addTimeLineButton.visibility = View.VISIBLE
            }else{
                holder.addTimeLineButton.visibility = View.GONE
            }

            if(index != 0 ){
                holder.leftFootImage.visibility = View.GONE
                holder.rightFootImage.visibility = View.GONE
                holder.firstLine.visibility =View.GONE

                val layout = holder.mainLineCircle.layoutParams as ConstraintLayout.LayoutParams
                layout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layout.bottomMargin = 0
                holder.mainLineCircle.layoutParams = layout

            }else{
                holder.leftFootImage.visibility = View.VISIBLE
                holder.rightFootImage.visibility = View.VISIBLE
                holder.firstLine.visibility =View.VISIBLE
            }
            when (index % 4) {
                0 -> {
                    themeId = R.drawable.circle_green
                    itemBackgroundThemeId = R.drawable.circle_round_edge_green_15
                    textColor = R.color.baseColor
                    isLeft = true
                }
                1 -> {
                    themeId = R.drawable.circle_orange
                    itemBackgroundThemeId = R.drawable.circle_round_edge_orange_15
                    textColor = R.color.orange
                }
                2 -> {
                    themeId = R.drawable.circle_dark_gray
                    itemBackgroundThemeId = R.drawable.circle_round_edge_dark_gray_15
                    textColor = R.color.title_color
                    isLeft = true
                }
                3 ->{
                    themeId = R.drawable.circle_red
                    itemBackgroundThemeId = R.drawable.circle_round_edge_red_15
                    textColor = R.color.red
                }
            }

            var leftVisibility = if (isLeft) View.VISIBLE else View.GONE
            var rightVisibility = if (!isLeft) View.VISIBLE else View.GONE

            holder.leftTimeLine.visibility = leftVisibility
            holder.leftLineCircle.visibility = leftVisibility
            holder.leftTimeLineItemBackground.visibility = leftVisibility
            holder.leftTimeLineTime.visibility = leftVisibility
            holder.leftTimeLineImage.visibility = leftVisibility
            holder.leftTimeLineName.visibility = leftVisibility
            holder.leftTimeLineDetailButton.visibility = leftVisibility

            holder.rightTimeLine.visibility = rightVisibility
            holder.rightLineCircle.visibility = rightVisibility
            holder.rightTimeLineItemBackground.visibility = rightVisibility
            holder.rightTimeLineTime.visibility = rightVisibility
            holder.rightTimeLineImage.visibility = rightVisibility
            holder.rightTimeLineName.visibility = rightVisibility
            holder.rightTimeLineDetailButton.visibility = rightVisibility

            holder.mainLine.setBackgroundResource(themeId)
            holder.mainLineCircle.setBackgroundResource(themeId)
            if(leftVisibility == View.VISIBLE) {
                holder.leftTimeLine.setBackgroundResource(themeId)
                holder.leftLineCircle.setBackgroundResource(themeId)
                holder.leftTimeLineItemBackground.setBackgroundResource(itemBackgroundThemeId)
                holder.leftTimeLineTime.setTextColor(mContext.getColor(textColor))
            }
            if(rightVisibility == View.VISIBLE) {
                holder.rightTimeLine.setBackgroundResource(themeId)
                holder.rightLineCircle.setBackgroundResource(themeId)
                holder.rightTimeLineItemBackground.setBackgroundResource(itemBackgroundThemeId)
                holder.rightTimeLineTime.setTextColor(mContext.getColor(textColor))
            }


    }


    inner class StepItemViewHandler(itemView: View): RecyclerView.ViewHolder(itemView){
        var leftFootImage: ImageView = itemView.findViewById(R.id.foot_left_image)
        var rightFootImage: ImageView = itemView.findViewById(R.id.foot_right_image)
        var mainLine:View = itemView.findViewById(R.id.main_line_view)
        var firstLine:View = itemView.findViewById(R.id.first_line_view)
        var mainLineCircle:View = itemView.findViewById(R.id.main_line_circle_view)
        var addTimeLineButton:TextView = itemView.findViewById(R.id.add_time_line_button)
        var leftTimeLine:View = itemView.findViewById(R.id.left_time_line_view)
        var leftLineCircle:View = itemView.findViewById(R.id.left_time_line_circle_view)
        var rightTimeLine:View = itemView.findViewById(R.id.right_time_line_view)
        var rightLineCircle:View = itemView.findViewById(R.id.right_time_line_circle_view)
        var rightTimeLineItemBackground:View = itemView.findViewById(R.id.right_time_line_item_background)
        var leftTimeLineItemBackground:View = itemView.findViewById(R.id.left_time_line_item_background)
        var leftTimeLineTime:TextView = itemView.findViewById(R.id.left_time_line_time)
        var rightTimeLineTime:TextView = itemView.findViewById(R.id.right_time_line_time)
        var leftTimeLineImage:CRImageView = itemView.findViewById(R.id.left_time_line_image)
        var rightTimeLineImage:CRImageView = itemView.findViewById(R.id.right_time_line_image)
        var leftTimeLineName:TextView = itemView.findViewById(R.id.left_time_line_name)
        var leftTimeLineDetailButton:Button = itemView.findViewById(R.id.left_time_line_detail_button)
        var rightTimeLineName:TextView = itemView.findViewById(R.id.right_time_line_name)
        var rightTimeLineDetailButton:Button = itemView.findViewById(R.id.right_time_line_detail_button)
    }
}