package hll.zpf.starttravel.base

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import hll.zpf.starttravel.R
import android.os.Bundle
import android.view.WindowManager
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.ybq.android.spinkit.style.DoubleBounce
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.database.entity.User
import hll.zpf.starttravel.page.HomeActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


open class BaseActivity: AppCompatActivity() {

    lateinit var context:BaseActivity

    /**
     * 登录页面的类型(EventBus 消息)
     */
    val PAGE_TYPE_NO_SKIP = "noSkip"

    /**
     * 添加旅行的类型(EventBus 消息)
     */
    val TRAVEL_TYPE = "travelType"

    /**
     * 修改旅行的类型(EventBus 消息)
     */
    val MODIFY_TRAVEL = "modifyTravel"

    /**
     * 刷新旅行一览页面(EventBus 消息)
     */
    val REFRESH_TRAVEL_DATA = "refreshTravel"

    /**
     * 旅行明细一览页面(EventBus 消息)
     */
    val TRAVEL_DETAIL = "travel_detail"

    /**
     * 刷新旅行明细一览页面(EventBus 消息)
     */
    val REFRESH_TRAVEL_DETAIL = "refresh_travel_detail"

    /**
     * 添加明细页面(EventBus 消息)
     */
    val ADD_DETAIL = "add_detail"

    /**
     * 查看明细页面(EventBus 消息)
     */
    val SHOW_DETAIL = "show_detail"

    /**
     * TimeLine一览页面(EventBus 消息)
     */
    val TIME_LINE = "time_line"

    /**
     * 刷新TimeLine一览页面(EventBus 消息)
     */
    val TIME_LINE_REFRESH = "time_line_refresh"

    val CODE_FOR_WRITE_PERMISSION = 1
    val CODE_FOR_LOCATION_PERMISSION = 2
    val CODE_FOR_READ_PHONE_STATE_PERMISSION = 3

    protected  var dataManager:DataManager? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setStatusBarColor(R.color.gray)
        setupWindowAnimations()
    }

    /**
     *　クリックの挙動
     */
    open fun baseClickAction(view: View){

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
    fun baseStartActivity(intent: Intent?,options: ActivityOptions){
            intent?.flags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
            startActivity(intent, options.toBundle())
    }

    /**
     * 画面の遷移
     * intent：遷移の情報
     * options:アニメーションのタイプ
     */
    fun baseStartActivity(intent: Intent?,anim:ActivityMoveEnum){
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
            startActivity(intent)
        }
        when (anim){
            ActivityMoveEnum.START_FROM_RIGHT -> overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left)
            ActivityMoveEnum.START_FROM_BOTTOM -> overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top)
            ActivityMoveEnum.BACK_FROM_LEFT -> overridePendingTransition(R.anim.out_to_right,R.anim.in_from_left)
            ActivityMoveEnum.BACK_FROM_TOP -> overridePendingTransition(R.anim.out_to_bottom,R.anim.in_from_top)
        }
    }

    /**
     * 画面结果の遷移
     * @param intent 遷移の情報
     * @param requestCode 请求CODE
     * @param options アニメーションのタイプ
     */
    fun baseStartActivityForResult(intent: Intent?,requestCode:Int,anim:ActivityMoveEnum){
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
            startActivityForResult(intent,requestCode)
        }
        when (anim){
            ActivityMoveEnum.START_FROM_RIGHT -> overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left)
            ActivityMoveEnum.START_FROM_BOTTOM -> overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top)
            ActivityMoveEnum.BACK_FROM_LEFT -> overridePendingTransition(R.anim.out_to_right,R.anim.in_from_left)
            ActivityMoveEnum.BACK_FROM_TOP -> overridePendingTransition(R.anim.out_to_bottom,R.anim.in_from_top)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        baseActivityResult(requestCode,resultCode,data)
    }
    /**
     * 画面结果返回の挙動
     */
    open fun baseActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

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

    /**
     * 设定标题
     *
     * @param title 标题文本
     * @param isShowRightButton 是否表示右侧按钮
     * @param rightButtonText 右侧按钮的文本
     * @param leftButtonBackground 左侧按钮的背景
     * @param clickBlock 按钮的点击事件
     */
    open fun setTitle(title:String,
                      isShowRightButton:Boolean,
                      rightButtonText:String? = null,
                      leftButtonBackground:Int? = null,
                      clickBlock:(View) -> Unit){
        val leftButtonItem:TextView = findViewById(R.id.left_button_item)
        val leftBt:LinearLayout = findViewById(R.id.left_button)
        val rightBt:TextView = findViewById(R.id.right_button)
        val titleText:TextView = findViewById(R.id.title_text)

        titleText.text = title

        if (!isShowRightButton) rightBt.visibility = View.GONE

        rightButtonText?.let {
            rightBt.text = it
        }
        leftButtonBackground?.let {
            leftButtonItem.setBackgroundResource(it)
        }

        leftBt.setOnClickListener {
            clickBlock(it)
        }
        rightBt.setOnClickListener {
            clickBlock(it)
        }

    }

    /**
     * 设定标题
     *
     * @param title 标题文本
     * @param isShowRightButton 是否表示右侧按钮
     * @param rightButtonText 右侧按钮的文本
     * @param leftButtonBackground 左侧按钮的背景
     * @param backgroundColor 背景颜色
     * @param titleColor 标题颜色
     * @param isShowBottomLine 是否显示下部线
     * @param clickBlock 按钮的点击事件
     */
    open fun setTitleWithColor(title:String,
                      isShowRightButton:Boolean,
                      rightButtonText:String? = null,
                      leftButtonBackground:Int? = null,
                      backgroundColor:Int,
                      titleColor:Int,
                       isShowBottomLine:Boolean,
                      clickBlock:(View) -> Unit){
        setTitle(title,isShowRightButton,rightButtonText,leftButtonBackground,clickBlock)

        val titleText:TextView = findViewById(R.id.title_text)
        titleText.setTextColor(getColor(titleColor))

        val titleBackground:View = findViewById(R.id.title_background)
        titleBackground.setBackgroundColor(getColor(backgroundColor))

        findViewById<View>(R.id.bottom_line).visibility = if (isShowBottomLine) View.VISIBLE else View.GONE
    }


    /**
     * メッセージを表示する
     */
    fun showMessageAlertDialog(title:String, message:String, onClick:((DialogInterface, Int)->Unit)? = null){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setNegativeButton(getString(R.string.alert_button_text)){dialog, index ->
            if(onClick == null){
                dialog.dismiss()
            }else{
                onClick(dialog,index)
            }
        }
        builder.show()
    }


    fun showProgressBar(progressBar : ProgressBar){
        var indeter = DoubleBounce()
        indeter.color = getColor(R.color.baseColor)
        progressBar.indeterminateDrawable = indeter
        progressBar.visibility = View.VISIBLE
    }

    fun closeProgressBar(progressBar : ProgressBar){
        progressBar.visibility = View.VISIBLE
    }

    fun skipToHome(){
        val user = User.createUser()
        user.isVisitor = true
        dataManager = DataManager()
        dataManager?.insertUser(user){
            if(it == BuildConfig.NORMAL_CODE){
                UserData.instance().saveLoginUser(user.id)
                val  intent = Intent(context, HomeActivity::class.java)
                baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($it)")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataManager?.cancel()
    }

}