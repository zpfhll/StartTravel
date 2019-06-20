package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.HLogger

class MeFragment : Fragment(),View.OnClickListener {


    private lateinit var editNameButton: Button
    private lateinit var editMemoButton: Button
    private lateinit var helpButton:View
    private lateinit var aboutButton:View
    private lateinit var logoutButton:View

    private lateinit var userNameTv: TextView
    private lateinit var userMemoTv: TextView
    private lateinit var userMailTv: TextView

    private lateinit var userImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_me, container, false)

        editNameButton = rootView.findViewById(R.id.edit_name_btn)
        editMemoButton = rootView.findViewById(R.id.edit_memo_btn)
        helpButton = rootView.findViewById(R.id.help_view)
        aboutButton = rootView.findViewById(R.id.about_view)
        logoutButton = rootView.findViewById(R.id.logout_view)
        userNameTv = rootView.findViewById(R.id.user_name_tv)
        userMemoTv = rootView.findViewById(R.id.user_memo_tv)
        userMailTv = rootView.findViewById(R.id.user_mail_tv)
        userImageView = rootView.findViewById(R.id.user_img)

        editNameButton.setOnClickListener(this)
        editMemoButton.setOnClickListener(this)
        helpButton.setOnClickListener(this)
        aboutButton.setOnClickListener(this)
        logoutButton.setOnClickListener(this)
        userImageView.setOnClickListener(this)


        return rootView
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.edit_name_btn -> HLogger.instance().e("MeFragment", "name")
            R.id.edit_memo_btn -> HLogger.instance().e("MeFragment", "memo")
            R.id.help_view -> HLogger.instance().e("MeFragment", "help")
            R.id.about_view -> HLogger.instance().e("MeFragment", "about")
            R.id.logout_view -> HLogger.instance().e("MeFragment", "logout")
            R.id.user_img -> HLogger.instance().e("MeFragment", "img")
        }
    }



}
