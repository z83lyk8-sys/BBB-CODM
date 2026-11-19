package com.bbb.gaming

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var surfaceView: SurfaceView

    init {
            System.loadLibrary("abk")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
                
                    surfaceView = SurfaceView(this)
                        surfaceView.holder.addCallback(this)
                            setContentView(surfaceView)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
            initMenu(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    external fun initMenu(surface: android.view.Surface)
    external fun drawMenu()
}
    }
}