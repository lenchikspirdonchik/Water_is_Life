package spiridonov.water.`is`.life

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var dperiodit = 0
    private var dwaterit = 0
    private var drbphit = 1
    private var dayStart = 0
    private var waterProgress = 0
    private var realpic = 0
    private var lastdrink = 0
    private var lastaction = 0
    private var dayStartTwo = 0
    private lateinit var txtdate: TextView
    private lateinit var txtgoal: TextView
    private lateinit var txtrealWater: TextView
    private lateinit var pbwater: ProgressBar
    private lateinit var linhome: LinearLayout
    private var firststart = true
    private var date = Date()
    private var realhour = Date()
    private lateinit var msp: SharedPreferences
    private val LOG_TAG = "myLogs"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        txtdate = root.findViewById(R.id.txtday)
        msp = this.requireActivity().getSharedPreferences("HomeFragment", Context.MODE_PRIVATE)
        txtgoal = root.findViewById(R.id.txt_goalWater)
        txtrealWater = root.findViewById(R.id.txt_realWater)
        pbwater = root.findViewById(R.id.pdwater)
        linhome = root.findViewById(R.id.linearhome)
        val linhomemain = root.findViewById<LinearLayout>(R.id.linearhomemain)
        val glass_50 = root.findViewById<ImageView>(R.id.glass_50)
        val glass_100 = root.findViewById<ImageView>(R.id.glass_100)
        val glass_200 = root.findViewById<ImageView>(R.id.glass_200)
        val glass_250 = root.findViewById<ImageView>(R.id.glass_250)
        val glass_500 = root.findViewById<ImageView>(R.id.glass_500)
        linhome.visibility = View.INVISIBLE
        readdata()
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("D")
        val strDate = formatter.format(date).toInt()
        if (strDate == dayStartTwo + dperiodit && !firststart) endgame(resources.getString(R.string.grown))
        val lang = Locale.getDefault().language
        if (lang.contentEquals("ru")) {
            try {
                linhomemain.background = resources.getDrawable(R.drawable.start_logo_ru)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        } else {
            linhomemain.background = resources.getDrawable(R.drawable.start_logo_eng)
        }


        // works only on the first start
        if (firststart && dperiodit > 0) {
            linhome.visibility = View.VISIBLE
            val mName = "plant" + drbphit + "_0"
            val resID = resources.getIdentifier(mName, "drawable", MainActivity.PACKAGE_NAME)
            linhome.setBackgroundResource(resID)
            dayStart = formatter.format(date).toInt()
            dayStartTwo = formatter.format(date).toInt()
            firststart = false
            Log.d(LOG_TAG, "firststart = $firststart")
            lastdrink = formatter.format(date).toInt()
            create()
        }
        if (dperiodit != 0 && dwaterit != 0 && drbphit != 0 && !firststart) {
            create()
        } else {
            waterProgress = 0
        }


        val undoDrink = View.OnClickListener {
            waterProgress -= lastaction
            pbwater.progress = waterProgress
            txtrealWater.text = resources.getString(R.string.today_water) + " " + waterProgress
        }

        val drinkOnClickListener = View.OnClickListener { v ->
            val image = v as ImageView
            when (image.id) {
                R.id.glass_50 -> {
                    waterProgress += 50
                    lastaction = 50
                }
                R.id.glass_100 -> {
                    waterProgress += 100
                    lastaction = 100
                }
                R.id.glass_200 -> {
                    waterProgress += 200
                    lastaction = 200
                }
                R.id.glass_250 -> {
                    waterProgress += 250
                    lastaction = 250
                }
                R.id.glass_500 -> {
                    waterProgress += 500
                    lastaction = 500
                }
                else -> Log.d(LOG_TAG, "glass_NO")
            }
            Snackbar.make(requireActivity().findViewById(android.R.id.content), resources.getString(R.string.uDrank)+lastaction, Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.undo), undoDrink)
                    .setActionTextColor(Color.RED)
                    .show()
            if (realhour.hours >= 15 && waterProgress <= dwaterit / 2) {
                linhome.background = resources.getDrawable(R.drawable.drought)
            } else {
                val mName = "plant" + drbphit + "_" + realpic
                Log.d(LOG_TAG, mName)
                val resID = resources.getIdentifier(mName, "drawable", MainActivity.PACKAGE_NAME)
                linhome.setBackgroundResource(resID)
            }
            if (waterProgress > dwaterit * 10) {
                endgame(resources.getString(R.string.overflowed))
            }
            pbwater.progress = waterProgress
            txtrealWater.text = resources.getString(R.string.today_water) + " " + waterProgress
            lastdrink = formatter.format(date).toInt()
            savedata()
        }
        glass_50.setOnClickListener(drinkOnClickListener)
        glass_100.setOnClickListener(drinkOnClickListener)
        glass_200.setOnClickListener(drinkOnClickListener)
        glass_250.setOnClickListener(drinkOnClickListener)
        glass_500.setOnClickListener(drinkOnClickListener)
        savedata()
        return root
    }

    private fun create() {
        linhome.visibility = View.VISIBLE
        val months = arrayOf(resources.getString(R.string.january), resources.getString(R.string.february), resources.getString(R.string.march), resources.getString(R.string.april),
            resources.getString(R.string.may), resources.getString(R.string.june), resources.getString(R.string.july), resources.getString(R.string.august), resources.getString(R.string.september),
            resources.getString(R.string.october), resources.getString(R.string.november), resources.getString(R.string.december))
        txtdate.text = "${resources.getString(R.string.today)} ${date.date} ${months[date.month]}"
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("D")
        val strDate = formatter.format(date).toInt()
        if (lastdrink != strDate) {
            if (waterProgress <= dwaterit / 4) linhome.background = resources.getDrawable(R.drawable.drought)
            if (waterProgress == 0) endgame(resources.getString(R.string.died))
            waterProgress = 0
            Log.d(LOG_TAG, "waterProgress = $waterProgress")
        }
        txtrealWater.text = resources.getString(R.string.today_water) + " " + waterProgress
        pbwater.max = dwaterit
        pbwater.progress = waterProgress
        txtgoal.text = resources.getString(R.string.today_goal)
        var buff = 0
        for (i in 0..2) {
            buff += dperiodit / 3
            if (dayStart + buff == strDate) {
                dayStart++
                realpic += 1
                if (realpic > 3) realpic = 3
                savedata()
                val mName = "plant" + drbphit + "_" + realpic
                val resID = resources.getIdentifier(mName, "drawable", MainActivity.PACKAGE_NAME)
                try {
                    linhome.setBackgroundResource(resID)
                } catch (e: Exception) {
                    linhome.background = resources.getDrawable(R.drawable.plant1_3)
                }
            }
        }
        val mName = "plant" + drbphit + "_" + realpic
        val resID = resources.getIdentifier(mName, "drawable", MainActivity.PACKAGE_NAME)
        try {
            linhome.setBackgroundResource(resID)
        } catch (e: Exception) {
            linhome.background = resources.getDrawable(R.drawable.plant1_3)
        }
        Log.d(LOG_TAG, "dperiodit = $dperiodit")
        Log.d(LOG_TAG, "day + buff = " + (dayStart + buff))
        Log.d(LOG_TAG, "day = $dayStart")
        Log.d(LOG_TAG, "buff = $buff")
        Log.d(LOG_TAG, "strDate = $strDate")
        if (realhour.hours >= 15 && waterProgress <= dwaterit / 2) {
            linhome.background = resources.getDrawable(R.drawable.drought)
        }
        savedata()
    }



    private fun endgame(message: String) {
        firststart = true
        dperiodit = 0
        dwaterit = 0
        drbphit = 0
        waterProgress = 0
        dayStart = 0
        realpic = 0
        linhome.visibility = View.INVISIBLE
        savedata()
        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle(resources.getString(R.string.confirm))
                .setMessage(message)
                .setIcon(R.drawable.warning)
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> }
        val malert = mBuilder.create()
        malert.show()
    }

    private fun savedata() {
        val editor = msp.edit()
        editor.putInt(KEY_PERIOD, dperiodit)
        editor.putInt(KEY_WATER, dwaterit)
        editor.putInt(KEY_PHOTO, drbphit)
        editor.putInt(KEY_DAYSTART, dayStart)
        editor.putInt(KEY_WATERPROG, waterProgress)
        editor.putInt(KEY_REALPIC, realpic)
        editor.putInt(KEY_LASTDRINK, lastdrink)
        editor.putInt(KEY_DAYSTARTTWO, dayStartTwo)
        editor.putBoolean(KEY_FIRSTSTART, firststart)
        editor.apply()
    }

    private fun readdata() {
        if (msp.contains(KEY_PERIOD)) dperiodit = msp.getInt(KEY_PERIOD, 0)
        if (msp.contains(KEY_WATER)) dwaterit = msp.getInt(KEY_WATER, 0)
        if (msp.contains(KEY_PHOTO)) drbphit = msp.getInt(KEY_PHOTO, 0)
        if (msp.contains(KEY_DAYSTART)) dayStart = msp.getInt(KEY_DAYSTART, 0)
        if (msp.contains(KEY_WATERPROG)) waterProgress = msp.getInt(KEY_WATERPROG, 0)
        if (msp.contains(KEY_REALPIC)) realpic = msp.getInt(KEY_REALPIC, 0)
        if (msp.contains(KEY_LASTDRINK)) lastdrink = msp.getInt(KEY_LASTDRINK, 0)
        if (msp.contains(KEY_DAYSTARTTWO)) dayStartTwo = msp.getInt(KEY_DAYSTARTTWO, 0)
        if (msp.contains(KEY_FIRSTSTART)) firststart = msp.getBoolean(KEY_FIRSTSTART, false)
    }

    override fun onPause() {
        super.onPause()
        savedata()
    }

    companion object {
        private const val KEY_PERIOD = "period"
        private const val KEY_WATER = "water"
        private const val KEY_PHOTO = "photo"
        private const val KEY_DAYSTARTTWO = "dayStartTwo"
        private const val KEY_DAYSTART = "dayStart"
        private const val KEY_LASTDRINK = "lastdrink"
        private const val KEY_FIRSTSTART = "firststart"
        private const val KEY_WATERPROG = "waterprogress"
        private const val KEY_REALPIC = "realpic"
    }
}