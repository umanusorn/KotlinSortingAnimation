package com.umitems.kotlin.sorting

import android.graphics.Color
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
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep
import java.util.*

class MainActivity : AppCompatActivity() {
    var delay = 0L
    var dataCount = 20
    val SORT_TEXT = "Sort"
    val SORTED_TEXT = "Sorted"
    val SORTING_TEXT = "Sorting"
    var random: Random = Random()
    var randomData = initRandomArray(dataCount, dataCount)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)
        setupBtns(mRecyclerView, shakeAnim)
        delay = getDelayFromSeekBar() // initSeekBar and set delay
        tvData.text = getDataCount()

        seekBarQuantity.progress = dataCount
        seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                delay = getDelayFromSeekBar()
            }
        })
        seekBarQuantity.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                tvData.text = getDataCount()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                tvData.text = getDataCount()
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress > 0) {
                    tvData.text = getDataCount()
                    initDataNResetUi(mRecyclerView)
                    dataCount = progress
                }
            }
        })
    }

    private fun getDataCount(): String {
        var data = dataCount.toString()
        if (dataCount < 10)
            data = "0" + data
        return "Data: " + data

    }

    //todo may need to hide seekBarQuantity when sorting. Its gonna be hard/fun to sorting and adjusting quantity of data
    private val mRecyclerView: RecyclerView
        get() {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager
            var sortAdapter: SortAdapter
            val arrayList: ArrayList<Int> = randomData.let { intList ->
                ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
            }
            sortAdapter = SortAdapter(arrayList, this)
            recyclerView.adapter = sortAdapter

            val callback = RecyclerItemTouchHelper(sortAdapter)
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(recyclerView)
            return recyclerView
        }

    private fun getDelayFromSeekBar() = Math.abs(1000 - (seekBarSpeed.progress * 9.9)).toLong()

    private fun setupBtns(mRecyclerView: RecyclerView, shakeAnim: Animation?) {
        btnRandom.setOnClickListener {
            initDataNResetUi(mRecyclerView)
        }
        btnSort.text = SORT_TEXT
        btnSort.startAnimation(shakeAnim)
        btnSort.setOnClickListener {
            it as Button
            it.text = SORTED_TEXT
            it.isEnabled = false
            System.out.println("Before: " + randomData)
            bubbleSort(randomData, mRecyclerView)
        }

        tvStep.setOnClickListener {
            System.out.println("Before: " + randomData)
            bubbleSortStep(randomData, mRecyclerView)
        }
    }

    private fun initDataNResetUi(mRecyclerView: RecyclerView) {
        randomData = initRandomArray(dataCount, dataCount)
        mRecyclerView.adapter = SortAdapter(randomData, this)
        tvUiPing.setTextColor(Color.BLACK)
        btnSort.text = SORT_TEXT
        btnSort.isEnabled = true
        tvUiPing.text = "0"
        tvTotal.text = "0"
        tvSwap.text = "0"
        tvBigO.text = "0"
        tvMem.text = "0"
        tvCmp.text = "0"
    }

    private fun initRandomArray(max: Int, RANGE: Int): ArrayList<Int> {
        var range = RANGE
        if (range < 2) {
            Log.e("range < 0", "range=$range range<2 may cause div by zero")
            range = 2
        }
        var i = 0
        val array: ArrayList<Int>
        array = ArrayList()

        while (i < max) {
            array.add(random.nextInt() % (range / 2) + (range / 2 + 1))
            i++
        }
        Log.d("New dataset:", array.toString())

        return array
    }


    fun bubbleSortStep(mItems: ArrayList<Int>, mRecyclerView: RecyclerView) {
        //todo it seem that this func not work as I expected since it run through every loop in on click
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
    }

    fun bubbleSort(mItems: ArrayList<Int>, mRecyclerView: RecyclerView) {
        mRecyclerView.adapter = SortAdapter(mItems, this)
        btnSort.text = SORTING_TEXT
        var uiPing = 0L
        tvBigO.text = "n^2"
        var swapCount = 0
        var cmpCount = 0
        var i = 0
        var k = 0

        //todo How to bring back step while sorting? Or just let user choose to auto sort or steping sort
        //todo add specific color the swap,access,mem

        val thread = Thread {
            while (i < mItems.size) {
                k = 0
                while (k < mItems.size - 1) {
                    cmpCount += 2
                    runOnUiThread {
                        tvTotal.text = (cmpCount + swapCount).toString()
                        tvCmp.text = cmpCount.toString()
                        tvMem.text = "0"
                    }
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        swapCount++
                        val actionRunnable = BlockingOnUIRunnable(this, {
                                //todo measure and add more delay for ui to render the screen
                                var start = System.currentTimeMillis()
                                mRecyclerView.adapter.notifyItemChanged(k)
                                mRecyclerView.adapter.notifyItemChanged(k + 1)
                                tvSwap.text = swapCount.toString()
                                tvMem.text = "1"
                                var timeDiff = System.currentTimeMillis() - start
                                if (timeDiff > 0) {
                                    uiPing += timeDiff
                                    tvUiPing.text = uiPing.toString()
                                    tvUiPing.setTextColor(Color.RED)
                                }
                        })
                        actionRunnable.startOnUiAndWait()
                    } else {
                        // Ensure we take the same amount of time whetehr we swap or not
                        BlockingOnUIRunnable(this, {}).startOnUiAndWait()
                    }
                    sleep(delay)
                    k++
                }
                i++
            }
            /**
             * on sorted
             * need to update the ui the the latest state since if the delay is too fast. The device cannot render ui in time.
             */
            runOnUiThread {
                tvTotal.text = (cmpCount + swapCount).toString()
                mRecyclerView.adapter.notifyDataSetChanged()
                tvSwap.text = swapCount.toString()
                tvUiPing.text = uiPing.toString()
                tvCmp.text = cmpCount.toString()
                btnSort.text = SORTED_TEXT
                btnSort.isEnabled = false
                tvMem.text = "0"
            }
        }
        thread.start()
    }
}
