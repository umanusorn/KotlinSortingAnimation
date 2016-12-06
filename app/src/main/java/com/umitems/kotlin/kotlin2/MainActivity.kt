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
    val MAX_ITEMS = 100

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
            handler1.postDelayed({
                while (k < mItems.size - 1) {
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        /*mRecyclerView.adapter=null
                        mRecyclerView.adapter = SortAdapter(mItems, this)
                        mRecyclerView.adapter.notifyItemChanged(k)
                        mRecyclerView.adapter.notifyItemChanged(k+1)*/
                    }
                    k++
                }
                mRecyclerView.adapter=null
                mRecyclerView.adapter = SortAdapter(mItems, this)
                mRecyclerView.adapter.notifyItemChanged(k)
                mRecyclerView.adapter.notifyItemChanged(k+1)

            }, (500).toLong())

            //mRecyclerView.adapter = SortAdapter(mItems, this)
            i++
        }
        //mRecyclerView.adapter = SortAdapter(mItems, this)
        return mItems.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
    }

    fun <T : Comparable<T>> shellSort(a: Array<T>) {
        var h = 1
        while (h * 3 + 1 < a.size)
            h = 3 * h + 1
        while (h > 0) {
            for (i in h - 1..a.size - 1) {
                val s = a[i]
                var j = i
                j = i
                while (j >= h && a[j - h].compareTo(s) > 0) {
                    a[j] = a[j - h]
                    j -= h
                }
                a[j] = s
            }
            h /= 3
        }
    }


    fun sort(array: IntArray) {

        var inner: Int
        var outer: Int
        var temp: Int

        var h = 1
        while (h <= array.size / 3) {
            h = h * 3 + 1
        }
        while (h > 0) {
            outer = h
            while (outer < array.size) {
                temp = array[outer]
                inner = outer

                while (inner > h - 1 && array[inner - h] >= temp) {
                    array[inner] = array[inner - h]
                    inner -= h
                    System.out.println("After:  " + Arrays.toString(array) + ":" + temp)
                }
                array[inner] = temp
                outer++
            }
            h = (h - 1) / 3
        }
    }


}
