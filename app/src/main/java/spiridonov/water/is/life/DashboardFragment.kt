package spiridonov.water.`is`.life
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import spiridonov.water.`is`.life.MainActivity
import spiridonov.water.`is`.life.R

class DashboardFragment : Fragment() {
    private var dperiodit = 7
    private var dwaterit = 2
    private var drbphit = 1
    private lateinit var ch1: RadioButton
    private lateinit var ch2: RadioButton
    private lateinit var dperiod: SeekBar
    private lateinit var dwater: SeekBar
    private lateinit var txtAmontDay: TextView
    private lateinit var txtAmontWater: TextView
    private lateinit var dashboard_start: Button
    private lateinit var dashboard_delete: Button
    private lateinit var rg: RadioGroup
    private var firststart = true
    private lateinit var msp: SharedPreferences
    private lateinit var mspColor: SharedPreferences
    private lateinit var layMain: ConstraintLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        dperiod = root.findViewById(R.id.dperiod)
        dwater = root.findViewById(R.id.dwater)
        txtAmontDay = root.findViewById(R.id.txtAmontDay)
        txtAmontWater = root.findViewById(R.id.txtAmontWater)
        dashboard_start = root.findViewById(R.id.dashboard_start)
        dashboard_delete = root.findViewById(R.id.dashboard_delete)
        layMain = root.findViewById(R.id.mainLayDash)
        ch1 = root.findViewById(R.id.rb1ph)
        ch2 = root.findViewById(R.id.rb2ph)
        rg = root.findViewById(R.id.rg)
        msp = this.requireActivity().getSharedPreferences("HomeFragment", Context.MODE_PRIVATE)
        mspColor = this.requireActivity().getSharedPreferences("SettingsFragment", Context.MODE_PRIVATE)
        setColor()
        //seekbars
        dperiod.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                dperiodit = seekBar.progress
                txtAmontDay.text = seekBar.progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                dperiodit = seekBar.progress
                txtAmontDay.text = seekBar.progress.toString()
            }
        })
        dwater.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                dwaterit = seekBar.progress
                txtAmontWater.text = seekBar.progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                dwaterit = seekBar.progress
                txtAmontWater.text = seekBar.progress.toString()
            }
        })

        //buttons
        dashboard_start.setOnClickListener {
            if (rg.checkedRadioButtonId == -1) {
                Toast.makeText(activity, resources.getString(R.string.dashboard_rb), Toast.LENGTH_SHORT).show()
            } else {
                drbphit = when (rg.checkedRadioButtonId) {
                    R.id.rb1ph -> 1
                    R.id.rb2ph -> 2
                    else -> throw IllegalStateException("Unexpected value: " + rg.checkedRadioButtonId)
                }
                dwaterit = dwater.progress * 1000
                dperiodit = dperiod.progress
                savedata()
                dashboard_delete.isEnabled = true
                dashboard_start.isEnabled = false
                dperiod.isEnabled = false
                dwater.isEnabled = false
                val mainIntent = Intent(activity, MainActivity::class.java)
                requireActivity().startActivity(mainIntent)
                requireActivity().finish()
            }
        }
        dashboard_delete.setOnClickListener {
            val mBuilder = AlertDialog.Builder(activity)
            mBuilder.setTitle(resources.getString(R.string.confirm))
                    .setMessage(resources.getString(R.string.removePlant))
                    .setIcon(R.drawable.warning)
                    .setCancelable(false)
                    .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        dperiod.progress = 7
                        dwater.progress = 2
                        txtAmontDay.text = "7"
                        txtAmontWater.text = "2"
                        ch1.isChecked = true
                        dashboard_start.isEnabled = true
                        dashboard_delete.isEnabled = false
                        dperiod.isEnabled = true
                        dwater.isEnabled = true
                        val editor = msp.edit()
                        editor.putInt(KEY_PERIOD, 0)
                        editor.putInt(KEY_WATER, 0)
                        editor.putInt(KEY_PHOTO, 0)
                        editor.putInt(KEY_LASTDRINK, 0)
                        editor.putInt(KEY_WATERPROG, 0)
                        editor.putInt(KEY_REALPIC, 0)
                        editor.putInt(KEY_REALPIC, 0)
                        editor.putInt(KEY_DAYSTART, 0)
                        editor.putBoolean(KEY_FIRSTSTART, true)
                        editor.apply()
                    }
            val malert = mBuilder.create()
            malert.show()
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        readdata()
        if (!firststart) {
            dperiod.progress = dperiodit
            dwater.progress = dwaterit / 1000
            txtAmontDay.text = dperiodit.toString()
            txtAmontWater.text = (dwaterit / 1000).toString()
            ch1.isChecked = true
            dashboard_start.isEnabled = false
            dashboard_delete.isEnabled = true
            dperiod.isEnabled = false
            dwater.isEnabled = false
        }
    }

    private fun savedata() {
        val editor = msp.edit()
        editor.putInt(KEY_PERIOD, dperiodit)
        editor.putInt(KEY_WATER, dwaterit)
        editor.putInt(KEY_PHOTO, drbphit)
        editor.putBoolean(KEY_FIRSTSTART, firststart)
        editor.apply()
    }

    private fun readdata() {
        if (msp.contains(KEY_PERIOD)) dperiodit = msp.getInt(KEY_PERIOD, 0)
        if (msp.contains(KEY_WATER)) dwaterit = msp.getInt(KEY_WATER, 0)
        if (msp.contains(KEY_PHOTO)) drbphit = msp.getInt(KEY_PHOTO, 0)
        if (msp.contains(KEY_FIRSTSTART)) firststart = msp.getBoolean(KEY_FIRSTSTART, false)
    }

    private fun setColor() {
        var myColor = 0
        if (mspColor.contains(KEY_COLOR)) myColor = mspColor.getInt(KEY_COLOR, 0)
        if (myColor != 0) layMain.setBackgroundColor(myColor)
    }

    companion object {
        private const val KEY_PERIOD = "period"
        private const val KEY_WATER = "water"
        private const val KEY_PHOTO = "photo"
        private const val KEY_FIRSTSTART = "firststart"
        private const val KEY_COLOR = "color"
        private const val KEY_DAYSTART = "dayStart"
        private const val KEY_LASTDRINK = "lastdrink"
        private const val KEY_WATERPROG = "waterprogress"
        private const val KEY_REALPIC = "realpic"
    }
}