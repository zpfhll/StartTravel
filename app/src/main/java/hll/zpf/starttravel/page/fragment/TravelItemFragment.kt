package hll.zpf.starttravel.page.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.bean.TravelBean
import hll.zpf.starttravel.common.enums.TravelStateEnum
import hll.zpf.starttravel.common.model.TravelModel

class TravelItemFragment : Fragment() {

    var travelModel:TravelModel? = null

    var initDate:TravelBean? = null

    /**
     * frameのタイプ　０：普通　１：情報なし
     */
    var type:Int = 0

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
        when(type){
            0 -> {
                travelModel = ViewModelProviders.of(this).get(TravelModel::class.java)
                travelModel?.getTravelData()?.value = initDate

                travelModel?.getTravelData()?.observe(this, Observer {
                    view.findViewById<TextView>(R.id.travel_name_tv).text = it.travelName
                    view.findViewById<TextView>(R.id.travel_start_date).text = it.travelDate
                    view.findViewById<TextView>(R.id.travel_memo_tv).text = it.travelMemo
                    view.findViewById<TextView>(R.id.travel_person_number_tv).text = "${it.travelPersonNumber}"
                    view.findViewById<TextView>(R.id.travel_money_tv).text = Utils.instance().transMoneyToString(it.travelMoney)
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
            1 -> {
                view =  inflater.inflate(R.layout.fragment_travel_item_none, container, false)
            }

        }

        return view

    }

}
