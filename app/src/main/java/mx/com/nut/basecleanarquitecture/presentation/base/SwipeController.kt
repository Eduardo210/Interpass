package mx.com.nut.basecleanarquitecture.presentation.base

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent

internal enum class ButtonsState {
    GONE,
    VISIBLE
}

internal class SwipeController(buttonsActions: SwipeControllerActions) : ItemTouchHelper.Callback() {

    private var swipeBack = false

    private var buttonsShowedState = ButtonsState.GONE

    private var buttonEditInstance: RectF? = null
    private var buttonDeleteInstance: RectF? = null

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    private var buttonsActions: SwipeControllerActions? = null

    init {
        this.buttonsActions = buttonsActions
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return ItemTouchHelper.Callback.makeMovementFlags(0, LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonsShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var dX = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonsShowedState != ButtonsState.GONE) {
                if (buttonsShowedState == ButtonsState.VISIBLE) dX = Math.min(dX, -buttonsWidth)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        if (buttonsShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) {
                    buttonsShowedState = ButtonsState.VISIBLE
                }
                if (buttonsShowedState != ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@SwipeController.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                buttonsActions?.let { actions ->
                    if(buttonDeleteInstance?.contains(event.x, event.y)==true) {
                        actions.onDeleteClicked(viewHolder.adapterPosition)
                    }
                    else if(buttonEditInstance?.contains(event.x, event.y)==true) {
                        actions.onEditClicked(viewHolder.adapterPosition)
                    }
                }
                buttonsShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 16f

        val itemView = viewHolder.itemView
        val p = Paint()

        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat() - 45,
            itemView.bottom.toFloat() - 30
        )
        p.color = Color.parseColor("#ff524e")
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("Eliminar", c, rightButton, p)

        val leftButton = RectF(
            rightButton.left - buttonWidthWithoutPadding,
            rightButton.top,
            rightButton.left - 45,
            rightButton.bottom
        )
        p.color = Color.parseColor("#0084d8")
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("Editar", c, leftButton, p)

        buttonDeleteInstance = null
        buttonEditInstance = null
        if (buttonsShowedState == ButtonsState.VISIBLE) {
            buttonEditInstance = leftButton
            buttonDeleteInstance = rightButton
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 40f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize

        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
    }

    fun onDraw(c: Canvas) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder!!)
        }
    }

    companion object {

        private val buttonWidth = 300f
        private val buttonsWidth = 560f
    }
}

