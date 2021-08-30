package mx.com.nut.basecleanarquitecture.presentation.viewCustom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.RelativeLayout

class MyRelativeLayout(context: Context,attrs: AttributeSet ): RelativeLayout(context,attrs){
    private var scale = 1.0f

    fun setScaleBoth(scale: Float) {
        this.scale = scale
        this.invalidate()    // If you want to see the scale every time you set
        // scale you need to have this line here,
        // invalidate() function will call onDraw(Canvas)
        // to redraw the view for you
    }

    override fun onDraw(canvas: Canvas) {
        // The main mechanism to display scale animation, you can customize it
        // as your needs
        val w = this.getWidth()
        val h = this.getHeight()
        canvas.scale(scale, scale, (w / 2).toFloat(), (h / 2).toFloat())

        super.onDraw(canvas)
    }
}