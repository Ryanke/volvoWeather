package com.vovol.weather

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.vovol.weather.databinding.ActivityFullscreenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)
    private var datas: List<String> =
        listOf("110000", "310000", "440100", "440300", "320500", "210100")

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }

            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)
        for (d in datas) {
        }
        for (i in 0 until datas.size) {
            getData(datas[i], i)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    private fun getData(code: String, index: Int) {
        val mGson = GsonBuilder().setLenient() // 设置GSON的非严格模式setLenient()
            .create()
        Retrofit.Builder().baseUrl("https://restapi.amap.com/")
            .addConverterFactory(GsonConverterFactory.create(mGson)).build()
            .create(RequestManager::class.java)
            .getWeather(code, "8afdf809584a93bd17cce4577945f9d3", "all")
            .enqueue(object : Callback<WeatherInfo?> {
                override fun onResponse(
                    call: Call<WeatherInfo?>, response: Response<WeatherInfo?>
                ) {
                    //请求成功走这里
                    val body: WeatherInfo? = response.body()
                    Log.e("getData", "onResponse: $body")
                    body?.let { addWeatherData(it, index) };
                }

                override fun onFailure(call: Call<WeatherInfo?>, t: Throwable) {
                    //请求失败走这里
                    Log.e("getData", "onFailure: $t")
                }
            })
    }

    var weatherInfo: WeatherInfo = WeatherInfo(null, null, null, null, null);
    var weatherInfoList = mutableListOf(weatherInfo)
    var cDatas: List<String> = listOf("北京", "上海", "广州", "深圳", "苏州", "沈阳")

    private fun addWeatherData(weatherInfo: WeatherInfo, index: Int) {
        if (index == 0) {
            weatherInfoList[index] = weatherInfo
        } else {
            weatherInfoList.add(weatherInfo)
        }
        if (weatherInfoList.size == 6) {
            initWeather()
        }
    }

    private fun initWeather() {
        val baseAdapter = BaseAdapter(weatherInfoList)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        var vpBase = findViewById<ViewPager2>(R.id.demo_view_pager)
        with(vpBase) {
            adapter = baseAdapter
            isUserInputEnabled = true // 禁止滚动true为可以滑动false为禁止
            orientation = ViewPager2.ORIENTATION_HORIZONTAL // 设置垂直滚动ORIENTATION_VERTICAL
            setCurrentItem(0, false) //切换到指定页，是否展示过渡中间页
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    Log.e(
                        "TAG",
                        "onPageScrolled: $position--->$positionOffset--->$positionOffsetPixels"
                    )
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.e("TAG", "onPageSelected: $position")

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    Log.e("TAG", "onPageScrollStateChanged: $state")
                }
            }) // 监听滑动
        }
        TabLayoutMediator(tabLayout, vpBase) { tab, position ->
            tab.text = cDatas[position]
        }.attach()

    }

}