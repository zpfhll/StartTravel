package hll.zpf.starttravel.page

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.enums.ActivityMoveEnum


class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun clickAction(view:View){
        super.baseClickAction(view)
        when (view.id){
            R.id.login_bt -> moveToLogin()
            R.id.sign_in_bt -> moveToHome()
        }
    }

    private fun moveToHome(){
        val  intent = Intent(this,HomeActivity::class.java)
        baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
    }

    private fun moveToLogin(){
        val  intent = Intent(this,LoginActivity::class.java)
        baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
    }


}
