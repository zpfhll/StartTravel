package hll.zpf.starttravel.page.fragment


import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import hll.zpf.starttravel.common.components.CRImageView
import hll.zpf.starttravel.common.model.TravelModel
import hll.zpf.starttravel.common.database.entity.Travel

class TravelItemFragment : Fragment() {

    var travelModel: TravelModel? = null

    var initDate: Travel? = null

    /**
     * 回调函数
     * TravelModel：旅途的情报
     * Int：动作 ⇨　0：启程 1：标记 2：编辑 3：详细 4:结束 5:图片
     */
    var callback: ((TravelModel, Int) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_travel_item, container, false)

        travelModel = ViewModelProviders.of(this).get(TravelModel::class.java)

        travelModel?.getTravelData()?.value = initDate

        val travelAction: Button = view.findViewById(R.id.travel_action_bt)
        val travelFlag: Button = view.findViewById(R.id.travel_item_flag)
        val travelEdit: Button = view.findViewById(R.id.travel_item_edit)
        val travelDetail: Button = view.findViewById(R.id.travel_item_detail)

        val travelImage: CRImageView = view.findViewById(R.id.travel_image)

        travelModel?.getTravelData()?.observe(this, Observer {
            when (it.type) {
                0,1 -> {
                    view.findViewById<View>(R.id.travel_none_background).visibility = View.GONE
                    view.findViewById<View>(R.id.travel_none_background_shadow).visibility =
                        View.GONE
                    view.findViewById<ImageView>(R.id.travel_none_background_image).visibility =
                        View.GONE
                    view.findViewById<View>(R.id.travel_background_shadow).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.travel_name_tv).text = it.name

                    //开始日期的显示
                    it.startDate?.let { startDateS ->
                        view.findViewById<TextView>(R.id.travel_start_date).text =
                            Utils.instance()
                                .getDateStringByFormatAndDateString(startDateS, "yyyy年MM月dd日 hh:mm")
                    }
                    //人数显示
                    view.findViewById<TextView>(R.id.travel_person_number_tv).visibility =
                        View.VISIBLE
                    view.findViewById<TextView>(R.id.travel_person_number_tv).text =
                        "${it.memberCount}${getString(R.string.travel_006)}"

                    //钱的显示
                    if (it.type == 0) {
                        view.findViewById<TextView>(R.id.travel_money_tv).visibility = View.VISIBLE
                        view.findViewById<TextView>(R.id.travel_money_label_tv).visibility =
                            View.VISIBLE
                        view.findViewById<TextView>(R.id.travel_balance_money_tv).visibility =
                            View.VISIBLE
                        view.findViewById<TextView>(R.id.travel_balance_money_label_tv).visibility =
                            View.VISIBLE
                        view.findViewById<TextView>(R.id.travel_money_tv).text =
                            Utils.instance().transMoneyToString(it.outMoney ?: 0f)
                        view.findViewById<TextView>(R.id.travel_balance_money_tv).text =
                            Utils.instance()
                                .transMoneyToString((it.inMoney ?: 0f) - (it.outMoney ?: 0f))
                        travelDetail.isEnabled = true
                    } else {
                        view.findViewById<TextView>(R.id.travel_money_tv).visibility = View.GONE
                        view.findViewById<TextView>(R.id.travel_money_label_tv).visibility =
                            View.GONE
                        view.findViewById<TextView>(R.id.travel_balance_money_tv).visibility =
                            View.GONE
                        view.findViewById<TextView>(R.id.travel_balance_money_label_tv).visibility =
                            View.GONE
                        travelDetail.isEnabled = false
                        travelDetail.setBackgroundResource(R.mipmap.detail_pressed)
                    }

                    //图片设定
                    it.getImageBitmap()?.let { bitmap ->
                        travelImage.setImageBitmap(bitmap)
                        activity?.let {tempContext ->
                            view.findViewById<CRImageView>(R.id.travel_item_background).setImageBitmap(Utils.instance().blurBitmap(tempContext,bitmap,25f,0.4f))
                        }

                    }


                    //按钮的显示
                    if (it.state == 0) {
                        travelFlag.visibility = View.GONE
                        travelEdit.visibility = View.GONE
                        travelDetail.visibility = View.GONE
                        travelAction.tag = "start"
                    } else {
                        val animator = ObjectAnimator.ofFloat(
                            travelAction,
                            "translationY",
                            travelAction.translationY,
                            Utils.instance().DPToPX(32f + 25f - 8f)
                        )
                        animator.duration = 0
                        animator.start()
                        travelAction.background =
                            resources.getDrawable(R.drawable.button_background7, null)
                        travelAction.setTextColor(resources.getColor(R.color.white, null))
                        travelAction.setText(R.string.travel_002)
                        travelAction.tag = "end"
                    }
                }
                2 -> {
                    travelAction.visibility = View.GONE
                    view.findViewById<View>(R.id.travel_none_background).visibility = View.VISIBLE
                    view.findViewById<View>(R.id.travel_none_background_shadow).visibility =
                        View.VISIBLE
                    view.findViewById<ImageView>(R.id.travel_none_background_image).visibility =
                        View.VISIBLE
                    view.findViewById<View>(R.id.travel_background_shadow).visibility = View.GONE
                }
            }
        })
        travelAction.setOnClickListener {
            if (it.tag.toString().equals("start")) {
                val animatorFlag = ObjectAnimator.ofFloat(
                    travelFlag,
                    "translationX",
                    Utils.instance().DPToPX(60f + 34f),
                    travelFlag.translationX
                )
                animatorFlag.duration = 300

                val animatorDetail = ObjectAnimator.ofFloat(
                    travelDetail,
                    "translationX",
                    -Utils.instance().DPToPX(60f + 34f),
                    travelDetail.translationX
                )
                animatorDetail.duration = 300

                val animatorAction = ObjectAnimator.ofFloat(
                    travelAction,
                    "translationY",
                    travelAction.translationY,
                    Utils.instance().DPToPX(32f + 25f - 8f)
                )
                animatorAction.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        travelAction.background =
                            resources.getDrawable(R.drawable.button_background7, null)
                        travelAction.setTextColor(resources.getColor(R.color.white, null))
                        travelAction.setText(R.string.travel_002)
                        travelAction.tag = "end"
                    }
                })

                travelEdit.alpha = 0f
                travelFlag.alpha = 0f
                travelDetail.alpha = 0f
                val animatorEditAlpha =
                    ObjectAnimator.ofFloat(travelEdit, "alpha", travelEdit.alpha, 1f)
                animatorEditAlpha.duration = 100
                val animatorFlagAlpha =
                    ObjectAnimator.ofFloat(travelFlag, "alpha", travelFlag.alpha, 1f)
                animatorFlagAlpha.duration = 100
                val animatorDetailAlpha =
                    ObjectAnimator.ofFloat(travelDetail, "alpha", travelDetail.alpha, 1f)
                animatorDetailAlpha.duration = 100


                travelEdit.visibility = View.VISIBLE
                travelFlag.visibility = View.VISIBLE
                travelDetail.visibility = View.VISIBLE
                animatorAction.duration = 300
                animatorAction.start()
                animatorFlag.start()
                animatorDetail.start()
                animatorEditAlpha.start()
                animatorFlagAlpha.start()
                animatorDetailAlpha.start()

                callback?.let {
                    it(travelModel!!, 0)
                }
            } else if (it.tag.toString().equals("end")) {
                callback?.let {
                    it(travelModel!!, 4)
                }
            }

        }

        travelFlag.setOnClickListener {
            callback?.let {
                it(travelModel!!, 1)
            }
        }

        travelEdit.setOnClickListener {
            callback?.let {
                it(travelModel!!, 2)
            }
        }

        travelDetail.setOnClickListener {
            callback?.let {
                it(travelModel!!, 3)
            }
        }

        travelImage.setOnClickListener {
            callback?.let {
                it(travelModel!!, 5)
            }
        }

        return view


    }


}
