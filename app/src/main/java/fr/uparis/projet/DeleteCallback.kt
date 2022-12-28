package fr.uparis.projet

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DeleteCallback(val context: Context, private val typeItem: String, private val model: MainViewModel): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    override fun onChildDraw(c: Canvas,
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        adapter = recyclerView.adapter

        val itemView = viewHolder.itemView
        val height = itemView.bottom-itemView.top

        val isCancelled = dX == 0f && !isCurrentlyActive
        if(isCancelled){ //clear canvas
            c.drawRect(itemView.right+dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
        }
        else if (dX<0){
            /** delete background **/
            val bg = ColorDrawable()
            bg.color = ContextCompat.getColor(context, R.color.salmonDarkest)
            bg.setBounds(itemView.right+dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            bg.draw(c)

            /** delete icon **/
            val icon = ContextCompat.getDrawable(context, R.drawable.icons8_remove_100)
            if(icon != null){
                val iconTop = itemView.top + (height - icon.intrinsicHeight)/2
                val iconMargin = (height - icon.intrinsicHeight)/2
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + icon.intrinsicHeight

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(c)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete this $typeItem ?")
            .setPositiveButton("Confirm" ){ _, _ ->
                if (typeItem == "dictionary") {
                    model.swipedDictionary =
                        (viewHolder as MyDictionaryRecyclerViewAdapter.VH).dictionary
                    model.deleteDictionary()
                } else { // "word"
                    model.swipedWord = (viewHolder as MyWordRecyclerViewAdapter.VH).word
                    model.deleteWord()
                }
                adapter?.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
                adapter?.notifyDataSetChanged()
            }
            .show()
    }

    /** unused **/
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
}