package hll.zpf.starttravel.page

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.internet.ApiAccess
import hll.zpf.starttravel.internet.bean.LoginBean
import kotlinx.android.synthetic.main.activity_sign_up.*
import android.os.Handler
import android.os.Message
import hll.zpf.starttravel.common.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import hll.zpf.starttravel.common.EventBusMessage


class SignUpActivity : BaseActivity() {

    private lateinit var closeLoadingHandler:Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        EventBus.getDefault().register(this)

        closeLoadingHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1) {
                    loading.visibility = View.GONE
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMessage(message: EventBusMessage) {
        if (message.message.equals(PAGE_TYPE_NO_SKIP)){
            login_bt.visibility = View.GONE
            skip_bt.visibility = View.GONE
        }
    }

    fun clickAction(view:View){
        super.baseClickAction(view)
        when (view.id){
            R.id.login_bt -> {
                moveToLogin()
            }
            R.id.sign_in_bt -> moveToHome()
            R.id.skip_bt -> skipToHome()
        }
    }



    private fun moveToHome(){
        loading.visibility = View.VISIBLE
        val apiAccess = ApiAccess()
        apiAccess.signIn("user1@qq.com","password"){isSuccess, response ->
            loading.visibility = View.GONE
            if (isSuccess) {
                val  intent = Intent(this,HomeActivity::class.java)
                baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
                HLogger.instance().e("moveToHome:",(response.internetBean as LoginBean).userCode)
            }else{
                response.errorMessage?.let {
                    HLogger.instance().e("moveToHome:",it)
                }
                showMessageAlertDialog("",Utils.instance().getMessageByCode(response.errorCode))
            }
        }
    }


    private fun moveToLogin(){
        val  intent = Intent(this,LoginActivity::class.java)
        baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}
