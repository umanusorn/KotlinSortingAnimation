package com.umitems.kotlin.kotlin2

import android.os.Bundle
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
import java.lang.Thread.sleep
import java.util.*

class MainActivity : AppCompatActivity() {
    var random: Random = Random()
    var maxItems = 29
    var randomData = initRandomArray(maxItems, maxItems)
    val SORT_TEXT = "Sort"
    val SORTED_TEXT = "Sorted"
    val SORTING_TEXT = "Sorting"
    var delay = 0.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        random.setSeed(Math.random().toLong())//change random seed?
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)

        setupBtns(mRecyclerView, shakeAnim)
        delay = getDelayFromSeekBar() // initSeekBar and set delay
        seekBarQuantity

    }

    //todo may need to hide seekBarQuantity when sorting. Its gonna be hard/fun to sorting and adjusting quantity of data
    //todo dataBinding with txtCount access,swap etc...

    private val mRecyclerView: RecyclerView
        get() {
            var mRecyclerView = setupRecyclerView(randomData, R.id.recyclerView)
            return mRecyclerView
        }

    private val seekBarQuantity: SeekBar
        get() {
            var seekBar = findViewById(R.id.seekBarQuantity) as SeekBar
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (progress > 0) {
                        randomDataNUpdateUi(mRecyclerView)
                        maxItems = progress
                    }

                }
            })
            return seekBar
        }

    private val seekBarSpeed: SeekBar
        get() {
            var seekBar = findViewById(R.id.seekBarSpeed) as SeekBar
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    delay = getDelayFromSeekBar()
                }
            })
            return seekBar
        }

    private fun getDelayFromSeekBar() = Math.abs(1000 - (seekBarSpeed.progress * 9.9)).toLong()

    private val btnSort: Button
        get() {
            var btnSort = findViewById(R.id.btnSort) as Button
            return btnSort
        }

    private fun setupBtns(mRecyclerView: RecyclerView, shakeAnim: Animation?) {
        var btnRandom = findViewById(R.id.btnRandom) as Button

        btnRandom.setOnClickListener {
            randomDataNUpdateUi(mRecyclerView)
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

    private fun randomDataNUpdateUi(mRecyclerView: RecyclerView) {

        randomData = initRandomArray(maxItems, maxItems)
        mRecyclerView.adapter = SortAdapter(maxItems, randomData, this)
        btnSort.text = SORT_TEXT
    }

    private fun setupRecyclerView(array: ArrayList<Int>, recyclerViewId: Int): RecyclerView {
        var recyclerView = findViewById(recyclerViewId) as RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        var sortAdapter: SortAdapter
        val arrayList: ArrayList<Int> = array.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
        sortAdapter = SortAdapter(maxItems, arrayList, this)
        recyclerView.adapter = sortAdapter

        val callback = RecyclerItemTouchHelper(sortAdapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
        return recyclerView
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

    fun bubbleSort(mItems: ArrayList<Int>, mRecyclerView: RecyclerView): ArrayList<Int> {
        mRecyclerView.adapter = SortAdapter(maxItems, mItems, this)
        var i = 0
        var k = 0
        //todo How to bring back step while sorting? Or just let user choose to auto sort or steping sort
        //todo add specific color the swap,access,mem
        btnSort.text = SORTING_TEXT
        val thread = Thread {
            while (i < mItems.size) {
                k = 0
                while (k < mItems.size - 1) {
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        var timeDiff=0.toLong()
                        runOnUiThread {
                            //todo measure and add more delay for ui to render the screen
                            var calendar = Calendar.getInstance()
                            mRecyclerView.adapter.notifyItemChanged(k)
                            mRecyclerView.adapter.notifyItemChanged(k + 1)
                            var calendar2 = Calendar.getInstance()
                            timeDiff = calendar2.timeInMillis-calendar.timeInMillis
                        }
                        sleep(delay+timeDiff)
                    }
                    k++
                }
                i++
            }
            /**
             * on sorted
             * need to update the ui the the latest state since if the delay is too fast. The device cannot render ui in time.
             */
            runOnUiThread {
                mRecyclerView.adapter.notifyDataSetChanged()
                btnSort.text = SORTED_TEXT
            }
        }
        thread.start()

        return mItems.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
    }
}
