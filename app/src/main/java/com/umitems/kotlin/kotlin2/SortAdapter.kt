package com.umitems.kotlin.kotlin2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import java.util.*


/**
 * Created by umitems on 10/14/16.
 */
class SortAdapter(items: ArrayList<Int>, context: Context?) : RecyclerView.Adapter<SortAdapter.ViewHolder>() {
    val mContext = context
    private var lastPosition = -1
    private var mItems: ArrayList<Int>? = items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bar: ImageView
        internal var rootView: View

        init {
            bar = itemView.findViewById(R.id.barBg) as ImageView
            rootView = itemView
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.bar.layoutParams.height = mItems!![position] * 60
        holder!!.bar.layoutParams.width = 36
        holder!!.rootView.layoutParams.width = 36
        //setAnimation(holder.bar, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        //http://stackoverflow.com/questions/26724964/how-to-animate-recyclerview-items-when-they-appear
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.mother_bar, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mItems!!.size
    }

    fun onMove(recyclerView: RecyclerView, firstPos: Int, secondPos: Int) {
        /*Do your stuff what you want
          Notify your adapter about change in positions using notifyItemMoved method
          Shift element e.g. insertion sort*/
        // setAnimation(recyclerView.findViewHolderForAdapterPosition(firstPos).itemView,secondPos)
    }

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /*Do your stuff what you want
          Swap element e.g. bubbleSort*/
    }
}