package hll.zpf.starttravel.common

import hll.zpf.starttravel.internet.bean.LoginBean
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class UtilsTest {

    private var uitls:Utils? = null

    @Before
    fun setUp() {
        uitls = Utils()
    }

    @Test
    fun transMoneyToString() {
//        val response = LoginBean()
//        response.accessToken = "1234"
//        val result = uitls?.reflectObject(response)
//        assertTrue(result!!)
        val result = Utils.instance().getDateStringByFormatAndDateString("yyyy年MM月dd日 hh:mm","20190213123312123")
        assertSame("2019年02月13日 12:33",result)
    }
}