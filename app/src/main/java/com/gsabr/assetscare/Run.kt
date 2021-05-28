package com.gsabr.assetscare
import android.os.Handler
import android.os.Looper
import android.view.View



class Run {

    val VISIBLE = 0
    val INVISIBLE = 4
    val GONE = 8

//    enum class Visibility{
//        VISIBLE, INVISIBLE, GONE
//    }


    // visible, gone, e invisible
    fun changeView(views: Array<View>, visibility: Int){

        when(visibility){
            GONE -> for(view in views){ view.visibility = GONE }
            INVISIBLE -> for(view in views){ view.visibility = INVISIBLE }
            VISIBLE -> for(view in views){ view.visibility = VISIBLE }
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