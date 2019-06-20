package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import android.view.View
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.enums.ActivityMoveEnum

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun clickAction(view:View){
        super.baseClickAction(view)
        when (view.id){
            R.id.sign_in_bt -> moveToSignUp()
        }
    }

    private fun moveToSignUp(){
        val intent = Intent(this,SignUpActivity::class.java)
        baseStartActivity(intent, ActivityMoveEnum.BACK_FROM_LEFT)
    }
}
