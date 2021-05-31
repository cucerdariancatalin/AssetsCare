package com.gsabr.assetscare
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_revisao.*
import java.text.SimpleDateFormat
import java.util.*

class Run {

    fun changeView(views: Array<View>, visibility: Int){

        for(view in views){
            view.visibility = visibility
        }
    }

    fun hideViewDelay(view: View, duration: Long){

        view.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = View.VISIBLE
        }, duration)
    }

    fun setViewText(textViews: Array<TextView>, values: Array<String>){

        for(textView in textViews){
            textView.text = values[textViews.indexOf(textView)].uppercase(Locale.getDefault())
        }
    }

    fun formatDate(date: Calendar): String {

        val formatter = "dd/MM/yyy"
        val format = SimpleDateFormat(formatter)
        return format.format(date.time)
    }

    fun isLoading(answer: Boolean, arrayViewHide: Array<View>, arrayViewShow: Array<View>){
        if(answer){
            for(view in arrayViewHide){
                view.visibility = View.GONE
            }
            for(view in arrayViewShow){
                view.visibility = View.VISIBLE
            }
        }else{
            Handler(Looper.getMainLooper()).postDelayed({

                for(view in arrayViewHide){
                    view.visibility = View.VISIBLE
                }
                for(view in arrayViewShow){
                    view.visibility = View.GONE
                }
            }, 1000)
        }
    }
}