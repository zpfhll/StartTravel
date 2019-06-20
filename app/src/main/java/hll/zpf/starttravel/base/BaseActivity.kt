package hll.zpf.starttravel.base

import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import hll.zpf.starttravel.R
import android.os.Bundle
import android.view.WindowManager
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hll.zpf.starttravel.common.enums.ActivityMoveEnum


open class BaseActivity: AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.baseColor)
        setupWindowAnimations()
    }

    /**
     *　クリックの挙動
     */
    fun baseClickAction(view: View){

    }

    /**
     * 画面遷移のアニメーション設定
     */
    open fun setupWindowAnimations() {

    }

        /**
     * 修改状态栏颜色
     * @param colorId
     */
    fun setStatusBarColor(colorId: Int) {
        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this,colorId)
    }

    /**
     * 画面の遷移
     * intent：遷移の情報
     * options:アニメーションやシェアのコンポネートなどの設定
     */
    fun baseStartActivity(intent: Intent,options: ActivityOptions){
            intent.flags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
            startActivity(intent, options.toBundle())
    }

    /**
     * 画面の遷移
     * intent：遷移の情報
     * options:アニメーションのタイプ
     */
    fun baseStartActivity(intent: Intent,anim:ActivityMoveEnum){
        if (intent != null) {
            intent?.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
            startActivity(intent)
        }else{
            finish()
        }
        when (anim){
            null -> overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left)
            ActivityMoveEnum.START_FROM_RIGHT -> overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left)
            ActivityMoveEnum.START_FROM_BOTTOM -> overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top)
            ActivityMoveEnum.BACK_FROM_LEFT -> overridePendingTransition(R.anim.out_to_right,R.anim.in_from_left)
            ActivityMoveEnum.BACK_FROM_TOP -> overridePendingTransition(R.anim.out_to_bottom,R.anim.in_from_top)
        }
    }

    /**
     * 端末のボタンの挙動
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyCodeBackListener()
            return true
        }
        return if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            false
        } else super.onKeyDown(keyCode, event)
    }

    /**
     * 端末の戻るボタンの挙動
     */
    open fun onKeyCodeBackListener(){

    }

}