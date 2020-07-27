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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawTriPath(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sfi : Float = scale.divideScale(i, parts)
    val sfi1 : Float = scale.divideScale(0, divs)
    val sfi2 : Float = scale.divideScale(1, divs)
    val k : Int = (i + 1) / 2
    val l : Int = i / 2
    val y : Float = (h / (1 + k)) * (2 * k - 1)
    val y1 : Float = y * sfi1
    val y2 : Float = y * sfi2
    val dx : Float = (1 - 2 * l) * (w / 2) * k
    val x1 : Float = l * w / 2 + dx * sfi1
    val x2 : Float = l * w / 2 + dx * sfi2
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawMirrorTriPath(scale : Float, w : Float, h : Float, paint : Paint) {
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f)
        translate(-w / 2, - h / 2)
        drawTriPath(j, scale, w, h, paint)
        restore()
    }
}

fun Canvas.drawMTPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(w / 2, h / 2)
    drawMirrorTriPath(scale, w, h, paint)
    restore()
}
