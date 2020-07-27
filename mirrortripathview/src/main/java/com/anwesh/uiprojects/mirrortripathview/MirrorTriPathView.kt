package com.anwesh.uiprojects.mirrortripathview

/**
 * Created by anweshmishra on 28/07/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val colors : Array<String> = arrayOf("#3F51B5", "#03A9F4", "#F44336", "#FFEB3B", "#4CAF50")
val parts : Int = 3
val divs : Int = 2
val scGap : Float = 0.02f / (parts * divs)
val strokeFactor : Int = 90
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

