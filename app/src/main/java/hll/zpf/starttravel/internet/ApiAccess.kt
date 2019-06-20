package hll.zpf.starttravel.internet

import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.internet.bean.LoginBean
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiAccess {

    var retrofit:Retrofit? = null
    val TIME_OUT:Long = 5

    init {
        if (retrofit == null) {
            val builder:OkHttpClient.Builder = OkHttpClient.Builder()

            if (!BuildConfig.BUILD_TYPE.equals("release")) {
                builder.hostnameVerifier {s, ssl ->
                    true
                }
            }

            builder.connectTimeout(TIME_OUT,TimeUnit.SECONDS)
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_DOMAIN)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    fun signIn(userName:String,password:String,block:(isSuccess:Boolean,response: Response<LoginBean>?,errorMessage:String) -> Unit){
        val apiService = retrofit?.create(ApiService::class.java)
        var parameter:HashMap<String,String> = HashMap()
        parameter.set("userName",userName)
        parameter.set("pw",password)

        val callback:Callback<LoginBean> = object:Callback<LoginBean>{
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                    HLogger.instance().e("onFailure", t.localizedMessage)
                block(false,null,t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                block(true,response,"")
            }
        }
        apiService?.signIn(Utils.instance().getUUID(),parameter)?.enqueue(callback)
    }


    fun login(mail:String,password: String,passwordSure: String,block:(isSuccess:Boolean,response: Response<LoginBean>?,errorMessage:String) -> Unit){
        val apiService = retrofit?.create(ApiService::class.java)
        var parameter:HashMap<String,String> = HashMap()
        parameter.set("mail",mail)
        parameter.set("pw",password)
        parameter.set("pws",passwordSure)
        val callback:Callback<LoginBean> = object:Callback<LoginBean>{
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                HLogger.instance().e("onFailure", t.localizedMessage)
                block(false,null,t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                block(true,response,"")
            }
        }
        apiService?.login(Utils.instance().getUUID(),parameter)?.enqueue(callback)
    }




}

