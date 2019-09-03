package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.entity.Travel
import hll.zpf.starttravel.common.model.TravelModel

class TravelItemFragment : Fragment() {

    var travelModel:TravelModel? = null

    var initDate: Travel? = null

    /**
     * 回调函数
     * TravelModel：旅途的情报
     * Int：动作 ⇨　0：启程 1：标记 2：编辑 3：详细
     */
    var callback:((TravelModel,Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_travel_item, container, false)

        travelModel = ViewModelProviders.of(this).get(TravelModel::class.java)

        travelModel?.getTravelData()?.value = initDate

        travelModel?.getTravelData()?.observe(this, Observer {
            when(it.type) {
                0 -> {
                    view.findViewById<View>(R.id.travel_none_background).visibility = View.GONE
                    view.findViewById<ImageView>(R.id.travel_none_background_image).visibility = View.GONE
                    view.findViewById<TextView>(R.id.travel_name_tv).text = it.name
                    it.startDate?.let { startDateS ->
                        view.findViewById<TextView>(R.id.travel_start_date).text =
                            Utils.instance().getDateStringByFormatAndDateString("yyyy年MM月dd日 hh:mm", startDateS)
                    }
                    it.memo?.let { memoS ->
                        view.findViewById<TextView>(R.id.travel_memo_tv).text = memoS
                    }
                    view.findViewById<TextView>(R.id.travel_person_number_tv).text = "${it.memberList.size}"

                    var detailTotalMoney = 0f;
                    it.detailList?.let { details ->
                        for (detail in details) {
                            detailTotalMoney += detail.money
                        }
                    }
                    view.findViewById<TextView>(R.id.travel_money_tv).text =
                        Utils.instance().transMoneyToString(detailTotalMoney)
                }
                1 -> {
                    view.findViewById<View>(R.id.travel_none_background).visibility = View.VISIBLE
                    view.findViewById<ImageView>(R.id.travel_none_background_image).visibility = View.VISIBLE
                }
            }
        })
        view.findViewById<Button>(R.id.travel_action_bt).setOnClickListener {
            callback?.let {
                it(travelModel!!,0)
            }
        }

        view.findViewById<Button>(R.id.travel_item_flag).setOnClickListener {
            callback?.let {
                it(travelModel!!,1)
            }
        }

        view.findViewById<Button>(R.id.travel_item_edit).setOnClickListener {
            callback?.let {
                it(travelModel!!,2)
            }
        }

        view.findViewById<Button>(R.id.travel_item_detail).setOnClickListener {
            callback?.let {
                it(travelModel!!,3)
            }
        }
        return view


    }


}
