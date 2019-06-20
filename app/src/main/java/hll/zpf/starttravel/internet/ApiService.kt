package hll.zpf.starttravel.internet

import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.internet.bean.LoginBean
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST(BuildConfig.LOGIN_PATH)
    fun login(@Header("X-Device-Id") deviceID:String, @FieldMap parameter:Map<String,String>): Call<LoginBean>

    @FormUrlEncoded
    @POST(BuildConfig.SIGN_IN_PATH)
    fun signIn(@Header("X-Device-Id") deviceID:String, @FieldMap parameter:Map<String,String>): Call<LoginBean>
}