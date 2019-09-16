package hll.zpf.starttravel.common.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import hll.zpf.starttravel.R
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.common.database.entity.Member


class MemberInfoDialog(context: Context,memberType: TravelTypeEnum) : Dialog(context),View.OnClickListener{


    var member: Member? = null
    var callBack:((View,Member?) -> Unit)? = null

    private var mMemberName = ""
    private var mMemberMoney = ""
    private lateinit var memberName:EditText
    private lateinit var memberMoney:EditText
    private lateinit var commitBtn:Button
    private var mMemberType:TravelTypeEnum = memberType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = View.inflate(context, R.layout.member_info_dialog_layout,null)
        val memberImageView:CRImageView = rootView.findViewById(R.id.member_image)
        memberName = rootView.findViewById(R.id.member_name)
        memberMoney = rootView.findViewById(R.id.member_money)
        memberMoney.visibility = when(mMemberType){
            TravelTypeEnum.MONEY_TRAVEL -> View.VISIBLE
            TravelTypeEnum.FREE_TRAVEL -> View.GONE
        }
        rootView.findViewById<TextView>(R.id.money_info).visibility =  memberMoney.visibility
        if(memberMoney.visibility == View.GONE){
            var layoutParams = memberName.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomMargin = Utils.instance().DPToPX(32f).toInt()
            memberName.layoutParams = layoutParams
        }
        member?.let {
        it.getImageBitmap()?.let {bitmap ->
            memberImageView.setImageBitmap(bitmap)
        }
        it.name?.let {nameStr ->
            mMemberName = nameStr
            memberName.setText(nameStr)
        }
        it.money?.let {moneyStr ->
            if(moneyStr > 0) {
                mMemberMoney = Utils.instance().transMoneyToString(moneyStr).replace(",","")
                memberMoney.setText(mMemberMoney)
            }
        }
        }
        commitBtn = rootView.findViewById(R.id.commit_button)
        commitBtn.isEnabled = false
        commitBtn.setOnClickListener(this)
        rootView.findViewById<Button>(R.id.close_button).setOnClickListener(this)
        memberMoney.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                checkInput()
            }
        })
        memberName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                    checkInput()
            }
        })
        setContentView(rootView)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val winPa = window.attributes
        window.attributes = winPa
    }

    private fun checkInput(){
        val moneyCheck = memberMoney.text.isNullOrEmpty() || !Utils.instance().checkMoney(memberMoney.text.toString())
        val nameCheck = memberName.text.isNullOrEmpty()
        val moneyNotChange =  memberMoney.text.toString().equals(mMemberMoney)
        val nameNotChange = memberName.text.toString().equals(mMemberName)
        when(mMemberType){
            TravelTypeEnum.MONEY_TRAVEL -> {
                commitBtn.isEnabled =  !(moneyCheck || nameCheck || (moneyNotChange && nameNotChange))
            }
            TravelTypeEnum.FREE_TRAVEL -> {
                commitBtn.isEnabled =  !( nameCheck || nameNotChange)
            }
        }

    }



    override fun onClick(v: View?) {
       when(v?.id){
           R.id.close_button -> {
               callBack?.let {
                   it(v,null)
               }
               this.dismiss()
           }
           R.id.commit_button -> {
               member?.let {
                   if (mMemberType == TravelTypeEnum.MONEY_TRAVEL) {
                       it.money = memberMoney.text.toString().toFloat()
                   }
                   it.name = memberName.text.toString()
               }
               callBack?.let {
                   it(v,member)
               }
               this.dismiss()
           }
       }
    }





}