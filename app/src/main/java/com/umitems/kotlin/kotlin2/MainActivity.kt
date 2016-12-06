package com.umitems.kotlin.kotlin2

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {
    var random: Random = Random()
    val MAX_ITEMS = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var data = initRandomArray(MAX_ITEMS, MAX_ITEMS)
        random.setSeed(Math.random().toLong())
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)

        var mRecyclerView = setupRecyclerView(data, R.id.recyclerView)
        setupBtns(data, mRecyclerView, shakeAnim)
    }

    private fun setupBtns(array: ArrayList<Int>, mRecyclerView: RecyclerView, shakeAnim: Animation?) {
        var array1 = array
        var btnRandom = findViewById(R.id.btnRandom) as Button
        var btnSort = findViewById(R.id.btnSorting) as Button
        val SORT_TEXT = "Sort"
        val SORTED_TEXT = "Sorted"

        btnRandom.setOnClickListener {
            array1 = initRandomArray(MAX_ITEMS, MAX_ITEMS)
            mRecyclerView.adapter = SortAdapter(array1, this)
            btnSort.text = SORT_TEXT
        }

        btnSort.text = SORT_TEXT
        btnSort.startAnimation(shakeAnim)

        btnSort.setOnClickListener {
            it as Button
            it.text = SORTED_TEXT

            System.out.println("Before: " + array1)
            bubbleSort(array1, mRecyclerView)
        }
    }

    private fun setupRecyclerView(array: ArrayList<Int>, recyclerViewId: Int): RecyclerView {
        var recyclerView = findViewById(recyclerViewId) as RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        var sortAdapter: SortAdapter
        val arrayList: ArrayList<Int> = array.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
        sortAdapter = SortAdapter(arrayList, this)
        recyclerView.adapter = sortAdapter

        val callback = RecyclerItemTouchHelper(sortAdapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
        return recyclerView
    }

    private fun initRandomArray(max: Int, range: Int): ArrayList<Int> {
        var i = 0
        var array: ArrayList<Int>
        array = ArrayList()

        while (i < max) {
            array.add(random.nextInt() % (range / 2) + (range / 2 + 1))
            i++
        }
        Log.d("New dataset:", array.toString())
        return array
    }

    fun bubbleSort(mItems: ArrayList<Int>, mRecyclerView: RecyclerView): ArrayList<Int> {
        val handler1 = Handler()
        mRecyclerView.adapter = SortAdapter(mItems, this)
        var i = 0
        var k = 0
        while (i < mItems.size) {
            k = 0
            val delay = 1000
            handler1.postDelayed({
                while (k < mItems.size - 1) {
                    Log.d("chkDelay", "k=" + k)
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        mRecyclerView.adapter.notifyItemChanged(k)
                        mRecyclerView.adapter.notifyItemChanged(k + 1)
                    }
                    k++
                }

            }, delay * i.toLong())
            i++
            Log.d("chkDelay", "i=" + i)
        }
        //mRecyclerView.adapter = SortAdapter(mItems, this)
        return mItems.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
    }
}
