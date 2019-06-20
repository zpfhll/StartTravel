package hll.zpf.starttravel.common.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.Utils
import android.graphics.drawable.ColorDrawable
import android.graphics.Color


class ListDialog(context: Context) : Dialog(context) {

    private lateinit var cancelBtn: Button

    private lateinit var contentList:ListView

    private lateinit var titleTv:TextView

    var data:List<String> = ArrayList()

    /**
     * -1是点击了取消
    */
    var callback: ((Int) -> Unit)? = null

    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = View.inflate(context, R.layout.list_dialog_layout,null)

        cancelBtn = rootView.findViewById(R.id.list_dialog_cancel)
        cancelBtn.setOnClickListener{
            callback?.let {
                it(-1)
                dismiss()
            }
        }
        titleTv = rootView.findViewById(R.id.list_dialog_title)
        titleTv.text = title

        contentList = rootView.findViewById(R.id.content_list)
        contentList.adapter = object : BaseAdapter(){

            override fun getItem(position: Int): Any {
                return data[position]
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val itemView = View.inflate(context, R.layout.list_dialog_item,null)
                itemView.findViewById<TextView>(R.id.content).text = data[position]
                if (position == data.size - 1) {
                    itemView.findViewById<View>(R.id.bottom_line).visibility = View.INVISIBLE
                }
                return itemView
            }

            override fun getItemId(position: Int): Long {
                    return position.toLong()
            }

            override fun getCount(): Int {
                    return data.size
            }
        }
        contentList.onItemClickListener =
            AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                callback?.let {
                    it(i)
                    dismiss()
                }
            }
        setContentView(rootView)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val winPa = window.attributes
        window.attributes = winPa
    }


    private fun makeList() : List<Map<String,String>>{
        var list = ArrayList<Map<String,String>>()
        data?.let {
            for (item in it){
                var map = HashMap<String,String>()
                map.put("content",item)
                list.add(map)
            }
        }
        return list
    }



}