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
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.common.model.MemberModel


class MemberInfoDialog(context: Context,memberType: TravelTypeEnum) : Dialog(context),View.OnClickListener{


    var member: MemberModel? = null
    var callBack:((View,Boolean) -> Unit)? = null

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
        memberMoney.visibility = if (mMemberType == TravelTypeEnum.MONEY_TRAVEL) View.VISIBLE else View.GONE
        member?.getMemberData()?.value?.let {
            it.imageBitmap?.let {bitmap ->
                memberImageView.setImageBitmap(bitmap)
            }
            it.name?.let {nameStr ->
                mMemberName = nameStr
                memberName.setText(nameStr)
            }
            it.money?.let {moneyStr ->
                mMemberMoney = "${moneyStr}"
                memberMoney.setText(mMemberMoney)
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
        commitBtn.isEnabled = !(memberName.text.isNullOrEmpty() ||
                memberMoney.text.isNullOrEmpty() ||
                !Utils.instance().checkMoney(memberMoney.text.toString()) ||
                (memberName.text.equals(mMemberName) && memberMoney.text.equals(mMemberMoney)))
    }



    override fun onClick(v: View?) {
       when(v?.id){
           R.id.close_button -> {
               callBack?.let {
                   it(v,false)
               }
               this.dismiss()
           }
           R.id.commit_button -> {
               member?.let {
                   val newMember = it.getMemberData().value!!
                   if (mMemberType == TravelTypeEnum.MONEY_TRAVEL) {
                       newMember.money = memberMoney.text.toString().toFloat()
                   }
                   newMember.name = memberName.text.toString()
                   it.getMemberData().value = newMember
               }
               callBack?.let {
                   it(v,true)
               }
               this.dismiss()
           }
       }
    }





}