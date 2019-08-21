package hll.zpf.starttravel.internet

import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.internet.bean.InternetResponse
import hll.zpf.starttravel.internet.bean.LoginBean
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ApiAccess {

    var retrofit:Retrofit? = null
    private val TIME_OUT:Long = 15

    init {
        if (retrofit == null) {
            val builder:OkHttpClient.Builder = OkHttpClient.Builder()

            if (!BuildConfig.BUILD_TYPE.equals("release")) {

                builder.hostnameVerifier(){s, sslSession -> Boolean
                 true
                }
                //创建管理器
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf<X509Certificate>()
                    }

                    override fun checkClientTrusted(
                        x509Certificates: Array<java.security.cert.X509Certificate>,
                        s: String
                    ) {
                    }
                    override fun checkServerTrusted(
                        x509Certificates: Array<java.security.cert.X509Certificate>,
                        s: String
                    ) {
                    }
                })
                try {
                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                    //为OkHttpClient设置sslSocketFactory
                    builder.sslSocketFactory(sslContext.socketFactory)

                } catch (e: Exception) {
                    e.printStackTrace()
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

    fun signIn(userName:String,password:String,block:(isSuccess:Boolean,response: InternetResponse) -> Unit){
        val apiService = retrofit?.create(ApiService::class.java)
        var internetResponse = InternetResponse()
        var parameter:HashMap<String,String> = HashMap()
        parameter.set("userName",userName)
        parameter.set("pw",password)
        val callback:Callback<LoginBean> = object:Callback<LoginBean>{
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                HLogger.instance().e("onFailure", t.localizedMessage)
                internetResponse.errorCode = "E00001"
                internetResponse.errorMessage = "${t.localizedMessage}"
                block(false,internetResponse)
            }
            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                if (response != null){
                    if(response.code() == 200){
                        internetResponse.internetBean = response.body()
                        if(!Utils.instance().reflectObject(internetResponse.internetBean)){
                            internetResponse.errorCode = "E00003"
                            internetResponse.errorMessage = "必须值缺失！！"
                        }
                    }else{
                        internetResponse.errorCode = "E00002"
                        internetResponse.errorMessage = "${response.message()}:${response.code()}"
                    }
                }else{
                    internetResponse.errorCode = "E00002"
                    internetResponse.errorMessage = "response is null!"
                }

                HLogger.instance().e("onResponse", internetResponse.toString())
                block(internetResponse.errorCode == null,internetResponse)
            }
        }
        apiService?.signIn(Utils.instance().getUUID(),parameter)?.enqueue(callback)
    }


    fun login(mail:String,password: String,passwordSure: String,block:(isSuccess:Boolean,response: InternetResponse) -> Unit){
        val apiService = retrofit?.create(ApiService::class.java)
        var internetResponse = InternetResponse()
        var parameter:HashMap<String,String> = HashMap()
        parameter.set("mail",mail)
        parameter.set("pw",password)
        parameter.set("pws",passwordSure)
        val callback:Callback<LoginBean> = object:Callback<LoginBean>{
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                HLogger.instance().e("onFailure", t.localizedMessage)
                internetResponse.errorCode = "E00001"
                internetResponse.errorMessage = "${t.localizedMessage}"
                block(false,internetResponse)
            }
            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                if (response != null){
                    if(response.code() == 200){
                        internetResponse.internetBean = response.body()
                        if(!Utils.instance().reflectObject(internetResponse.internetBean)){
                            internetResponse.errorCode = "E00003"
                            internetResponse.errorMessage = "必须值缺失！！"
                        }
                    }else{
                        internetResponse.errorCode = "E00002"
                        internetResponse.errorMessage = "${response.message()}:${response.code()}"
                    }
                }else{
                    internetResponse.errorCode = "E00002"
                    internetResponse.errorMessage = "response is null!"
                }

                HLogger.instance().e("onResponse", internetResponse.toString())
                block(internetResponse.errorCode == null,internetResponse)
            }
        }
        apiService?.login(Utils.instance().getUUID(),parameter)?.enqueue(callback)
    }

    fun signIn(userName:String,password:String) : InternetResponse{
        val apiService = retrofit?.create(ApiService::class.java)
        var internetResponse = InternetResponse()
        var parameter:HashMap<String,String> = HashMap()
        parameter.set("userName",userName)
        parameter.set("pw",password)

        var response:Response<LoginBean>? = null
        try {
            response = apiService?.signIn(Utils.instance().getUUID(),parameter)?.execute()
            if (response != null){
                if(response.code() == 200){
                    internetResponse.internetBean = response.body()
                    if(!Utils.instance().reflectObject(internetResponse.internetBean)){
                        internetResponse.errorCode = "E00003"
                        internetResponse.errorMessage = "必须值缺失！！"
                    }
                }else{
                    internetResponse.errorCode = "E00002"
                    internetResponse.errorMessage = "${response.message()}:${response.code()}"
                }
            }else{
                internetResponse.errorCode = "E00002"
                internetResponse.errorMessage = "response is null!"
            }

        }catch (e:java.lang.Exception){
            internetResponse.errorCode = "E00001"
            internetResponse.errorMessage = e.message
        }

        return internetResponse
    }






}

