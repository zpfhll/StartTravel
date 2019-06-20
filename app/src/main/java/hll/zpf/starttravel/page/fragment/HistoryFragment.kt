package hll.zpf.starttravel.page.fragment


import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.ListDialog
import hll.zpf.starttravel.common.bean.TravelBean
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HistoryFragment : Fragment(),View.OnClickListener {

    /**
     * 设定按钮
     */
    private lateinit var settingImage:ImageView
    /**
     * 设定区域
     */
    private lateinit var settingView: ConstraintLayout
    /**
     * 开始日区域
     */
    private lateinit var startView: ConstraintLayout

    /**
     * 结束日区域
     */
    private lateinit var endView: ConstraintLayout
    /**
     * 地点区域
     */
    private lateinit var locationView: ConstraintLayout

    /**
     *设定区域的显示状态
     */
    private var settingIsShowing = false
    /**
     *设定区域的高
     */
    private  var settingViewHeight = 0
    /**
     * 开始日期
     */
    private lateinit var startTv:TextView
    /**
     * 结束日期
     */
    private lateinit var endTv:TextView
    /**
     * 地点
     */
    private lateinit var locationTv:TextView

    private lateinit var historyListView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        settingImage = rootView.findViewById(R.id.setting_img)
        settingImage.setOnClickListener(this)
        settingView = rootView.findViewById(R.id.settings_layout)
        settingViewHeight = (200 * Utils.instance().getScreenDensity()).toInt()

        startTv = rootView.findViewById(R.id.start_day)
        endTv = rootView.findViewById(R.id.end_day)
        locationTv = rootView.findViewById(R.id.location)
        rootView.findViewById<ConstraintLayout>(R.id.start_day_layout).setOnClickListener(this)
        rootView.findViewById<ConstraintLayout>(R.id.end_day_layout).setOnClickListener(this)
        rootView.findViewById<ConstraintLayout>(R.id.location_layout).setOnClickListener(this)
        rootView.findViewById<Button>(R.id.setting_ok).setOnClickListener(this)

        historyListView = rootView.findViewById(R.id.travel_list_recycler)
        val adapter = HistoryAdapter()
        adapter.travelData = initData()
        adapter.mContext = context
        adapter.callback = {
            HLogger.instance().e("listDialog",adapter.travelData[it].travelName)
        }
        historyListView.adapter = adapter
        historyListView.layoutManager = LinearLayoutManager(context)

        return rootView
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.setting_img -> {
                settingImage.isEnabled = false
                //设定按钮的动画
                var start = if (settingIsShowing) -90f else 0f
                var end = if (settingIsShowing) 0f else -90f
                val animator = ObjectAnimator.ofFloat(settingImage, "rotation", start, end)
                animator.duration = 300

                //设定区域的动画
                var valueStart = if (settingIsShowing) settingViewHeight else 0
                var valueEnd = if (settingIsShowing) 0 else settingViewHeight
                val valueAnimator = ValueAnimator.ofInt(valueStart, valueEnd)
                valueAnimator.duration = 300
                valueAnimator.addUpdateListener { animation ->
                    val layoutPa = settingView.layoutParams
                    layoutPa.height = animation.animatedValue as Int
                    settingView.layoutParams = layoutPa
                }
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        settingIsShowing = !settingIsShowing
                        settingImage.isEnabled = true
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }
                })
                animator.start()
                valueAnimator.start()
            }
            R.id.start_day_layout -> {
                context?.let {
                    Utils.instance().showDatePickerView(it,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            setDate(startTv,year,month,dayOfMonth)
                        })
                }
            }
            R.id.end_day_layout -> {
                context?.let {
                    Utils.instance().showDatePickerView(it,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            setDate(endTv,year,month,dayOfMonth)
                        })
                }
            }
            R.id.location_layout -> {
                val data = ArrayList<String>()
                data.addAll(arrayOf("全世界","天津","西宁","成都","西安","武汉","横滨"))
                val listDialog = ListDialog(context!!)
                listDialog.title = getString(R.string.history_008)
                listDialog.data = data
                listDialog.callback = {
                    when(it){
                        -1 -> {
                            HLogger.instance().e("listDialog","close")
                        }
                        else -> {
                            locationTv.text = data[it]
                        }

                    }
                }
                listDialog.show()

            }
            R.id.setting_ok ->{

            }
        }

    }

    private fun initData() : List<TravelBean>{
        var datas = ArrayList<TravelBean>()
        for (i in 0..5){
             var info = TravelBean()
            info.travelPersonNumber = i + 2
            info.travelName = "旅行$i"
            info.travelDate = "2019年12月0${i}日"
            info.travelMemo = "メモ$i"
            info.travelMoney = i * 200f
            datas.add(info)
        }
        return datas
    }

    /**
     * 设定日期
     */
    private fun setDate(view:TextView,year:Int, month:Int, dayOfMonth:Int){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日")
        val dateStr = dateFormat.format(calendar.time)
        view.text = dateStr
    }


}
