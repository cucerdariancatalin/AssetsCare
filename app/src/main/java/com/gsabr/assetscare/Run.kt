package com.gsabr.assetscare
import android.os.Handler
import android.os.Looper
import android.view.View


class Run {


    enum class Visibility{
        VISIBLE, INVISIBLE, GONE
    }

    // visible, gone, e invisible
    fun changeView(views: Array<View>, value: Visibility){

        when(value){
            Visibility.GONE -> for(view in views){ view.visibility = View.GONE }
            Visibility.INVISIBLE -> for(view in views){ view.visibility = View.INVISIBLE }
            Visibility.VISIBLE -> for(view in views){ view.visibility = View.VISIBLE }
        }
    }

    fun hideViewDelay(view: View, duration: Long){
        view.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = View.VISIBLE
        }, duration)
    }

    fun retornaInt():Int{
        return 1
    }

}