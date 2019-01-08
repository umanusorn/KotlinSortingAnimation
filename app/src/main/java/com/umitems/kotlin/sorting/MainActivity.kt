package com.umitems.kotlin.sorting

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mother_bar.view.*
import java.lang.Thread.sleep
import java.util.*


class MainActivity : AppCompatActivity() {
    var delay = 0L
    var dataCount = 20
    val SORT_TEXT = "Sort"
    val SORTED_TEXT = "Sorted"
    val SORTING_TEXT = "Sorting"
    var random: Random = Random()
    var mItems = initRandomArray(dataCount, dataCount)
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)
        mRecyclerView = initRecyclerView()
        setupBtns(shakeAnim)
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
    private fun initRecyclerView(): RecyclerView {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        var sortAdapter = SortAdapter(mItems, this)
        recyclerView.adapter = sortAdapter

        // TODO: What are these for?
//            val callback = RecyclerItemTouchHelper(sortAdapter)
//            val helper = ItemTouchHelper(callback)
//            helper.attachToRecyclerView(recyclerView)
        return recyclerView
    }

    private fun getDelayFromSeekBar() = Math.abs(1000 - (seekBarSpeed.progress * 9.9)).toLong()

    private fun setupBtns(shakeAnim: Animation?) {
        btnRandom.setOnClickListener {
            initDataNResetUi(mRecyclerView)
        }
        btnSort.text = SORT_TEXT
        btnSort.startAnimation(shakeAnim)
        btnSort.setOnClickListener {
            it as Button
            it.text = SORTED_TEXT
            it.isEnabled = false
            System.out.println("Before: " + mItems)
            bubbleSort()
        }

        tvStep.setOnClickListener {
            System.out.println("Before: " + mItems)
            bubbleSortStep()
        }
    }

    private fun initDataNResetUi(mRecyclerView: RecyclerView) {
        mItems = initRandomArray(dataCount, dataCount)
        mRecyclerView.adapter = SortAdapter(mItems, this)
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

    private fun initRandomArray(max: Int, RANGE: Int): MutableList<Int> {
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

        return Collections.synchronizedList(array)
    }


    fun bubbleSortStep() {
        //todo it seem that this func not work as I expected since it run through every loop in on click
        val handler1 = Handler()
        var i = 0
        while (i < mItems.size) {
            var k = 0
            val delay = 1000
            handler1.postDelayed({
                while (k < mItems.size - 1) {
                    Log.d("chkDelay", "k=" + k)
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        // TODO: Find out why notifyItemChanged results in incorrect animation
                        mRecyclerView.adapter.notifyDataSetChanged()
                    }
                    k++
                }

            }, delay * i.toLong())
            i++
            Log.d("chkDelay", "i=" + i)
        }
    }


    internal var k =0 // make it static since k in mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener is not equal k in thread
    internal var swapCount = 0
    internal var cmpCount = 0
    internal var i = 0
    fun bubbleSort() {
        btnSort.text = SORTING_TEXT
        var uiPing = 0L
        tvBigO.text = "n^2"
        //todo How to bring back step while sorting? Or just let user choose to auto sort or steping sort
        //todo add specific color the swap,access,mem
        i=0
        swapCount = 0
        cmpCount = 0
        val thread = Thread {
            while (i < mItems.size) {
                k=0
                while (k < mItems.size - 1) {
                    cmpCount += 2
                    val actionRunnable = BlockingOnUIRunnable(this, {
                        val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
                        val firstVisiblePos = layoutManager.findFirstCompletelyVisibleItemPosition()
                        val lastVisiblePos = layoutManager.findLastCompletelyVisibleItemPosition()
                        //todo find out why this log has been call
                        Log.d("chkPos", "k=$k firstVis=$firstVisiblePos last=$lastVisiblePos i=$i")

                        tvTotal.text = (cmpCount + swapCount).toString()
                        tvCmp.text = cmpCount.toString()
                        tvMem.text = "0"
                        //todo hi-light the accessing bar
                        Log.d("chkPos", "set red")
                        mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                            if (k >= firstVisiblePos && (k+1) <= lastVisiblePos) {
                                Log.d("chkPos", "in viewTreeObserver k=$k firstVis=$firstVisiblePos last=$lastVisiblePos i=$i")

                                try { //todo sometime app crash here getChildAt() return null
                                    mRecyclerView.getChildAt(k).barBg.setImageDrawable(getDrawable(R.drawable.red_bar))
                                    mRecyclerView.getChildAt(k + 1).barBg.setImageDrawable(getDrawable(R.drawable.red_bar))

                                } catch (e: NullPointerException) {
                                    Log.e("setColor","setRed Accessing items"+ e.toString())
                                }

                                try {
                                    mRecyclerView.getChildAt(k - 1).barBg.setImageDrawable(getDrawable(R.drawable.bar))
                                    mRecyclerView.getChildAt(k - 2).barBg.setImageDrawable(getDrawable(R.drawable.bar))
                                } catch (e: NullPointerException) {
                                    Log.e("setColor"," set back to normal color"+ e.toString())
                                }

                                if (k == firstVisiblePos || k+1==firstVisiblePos)
                                    try {
                                        mRecyclerView.getChildAt(lastVisiblePos).barBg.setImageDrawable(getDrawable(R.drawable.bar))
                                        mRecyclerView.getChildAt(lastVisiblePos - 1).barBg.setImageDrawable(getDrawable(R.drawable.bar))
                                    } catch (e: NullPointerException) {
                                        Log.e("setColor"," set back to normal color Case accessing First items"+ e.toString())
                                    }
                            }
                        }

                        if (mItems[k] < mItems[k + 1]) {
                            val tmp = mItems[k]
                            mItems[k] = mItems[k + 1]
                            mItems[k + 1] = tmp
                            swapCount++
                            // TODO: Find out why notifyItemChanged results in incorrect animation


                            mRecyclerView.adapter.notifyItemMoved(k, k + 1)
                            tvSwap.text = swapCount.toString()
                            tvMem.text = "1"
                            mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {

                            }
                        }
                    })
                    actionRunnable.startOnUiAndWait()
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
