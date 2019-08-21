package hll.zpf.starttravel.page

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.view.View
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.database.entity.User
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
            R.id.skip_bt -> skipToHome()
        }
    }

    private fun moveToSignUp(){
        val intent = Intent(this,SignUpActivity::class.java)
        baseStartActivity(intent, ActivityMoveEnum.BACK_FROM_LEFT)
    }
}
