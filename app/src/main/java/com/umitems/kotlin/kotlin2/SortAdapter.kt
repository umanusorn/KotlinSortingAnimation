package com.umitems.kotlin.kotlin2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.util.*

/**
 * Created by umitems on 10/14/16.
 */
class SortAdapter (items: ArrayList<Int>, context: Context?): RecyclerView.Adapter<SortAdapter.ViewHolder>() {

    private var mItems: ArrayList<Int>? =items
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var bar: ImageView
        internal var rootView: View
        init {
           bar = itemView.findViewById(R.id.barBg) as ImageView
            rootView = itemView
        }
    }
    //class ViewHolder(var mTextView: TextView) : RecyclerView.ViewHolder(mTextView)
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.bar.layoutParams.height= mItems!![position]
        holder!!.bar.layoutParams.width= 8
        holder!!.rootView.layoutParams.width=8

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        //Log.d("gotData", mItems.toString())
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
        var tmp = mItems!![firstPos]
    }

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /*Do your stuff what you want
          Swap element e.g. bubbleSort*/
    }
}