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
import java.lang.Thread.sleep
import java.util.*

class MainActivity : AppCompatActivity() {
    var random: Random = Random()
    val MAX_ITEMS = 20
    val SORT_TEXT = "Sort"
    val SORTED_TEXT = "Sorted"
    val SORTING_TEXT = "Sorting"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var data = initRandomArray(MAX_ITEMS, MAX_ITEMS)
        random.setSeed(Math.random().toLong())
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)

        var mRecyclerView = setupRecyclerView(data, R.id.recyclerView)
        setupBtns(data, mRecyclerView, shakeAnim)
    }

    private val btnSorting: Button
        get() {
            var btnSort = findViewById(R.id.btnSorting) as Button
            return btnSort
        }

    private fun setupBtns(array: ArrayList<Int>, mRecyclerView: RecyclerView, shakeAnim: Animation?) {
        var randomData = array
        var btnRandom = findViewById(R.id.btnRandom) as Button
        var btnSort = findViewById(R.id.btnSorting) as Button


        btnRandom.setOnClickListener {
            randomData = initRandomArray(MAX_ITEMS, MAX_ITEMS)
            mRecyclerView.adapter = SortAdapter(randomData, this)
            btnSort.text = SORT_TEXT
        }
        btnSort.text = SORT_TEXT
        btnSort.startAnimation(shakeAnim)
        btnSort.setOnClickListener {
            it as Button
            it.text = SORTED_TEXT
            System.out.println("Before: " + randomData)
            bubbleSort(randomData, mRecyclerView)
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
        val handler = Handler()
        mRecyclerView.adapter = SortAdapter(mItems, this)
        var i = 0
        var k = 0
        val delay = 200
        btnSorting.text=SORTING_TEXT
        val thread = Thread {
            while (i < mItems.size) {
                k = 0
                while (k < mItems.size - 1) {
                    Log.d("chkDelay", "k=" + k)
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        runOnUiThread {
                            //todo measure and add more delay for ui to render the screen
                            mRecyclerView.adapter.notifyItemChanged(k)
                            mRecyclerView.adapter.notifyItemChanged(k + 1)
                        }
                        sleep(delay.toLong())
                    }
                    k++
                }
                i++
                Log.d("chkDelay", "i=" + i)
            }
            /**
             * on sorted
             * need to update the ui the the latest state since if the delay is too fast. The device cannot render ui in time.
             */
            runOnUiThread {
                mRecyclerView.adapter.notifyDataSetChanged()
           btnSorting.text=SORTED_TEXT
            }
        }
        thread.start()

        return mItems.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
    }
}
