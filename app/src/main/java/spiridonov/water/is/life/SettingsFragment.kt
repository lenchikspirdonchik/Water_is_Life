package spiridonov.water.`is`.life

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import spiridonov.water.`is`.life.R
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var getColor: Button
    private lateinit var msp: SharedPreferences
    private var myColor = 0
    private lateinit var layMain: ConstraintLayout

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        getColor = root.findViewById(R.id.btn_getColor)
        layMain = root.findViewById(R.id.linearSettingMain)
        msp = this.requireActivity().getSharedPreferences("SettingsFragment", Context.MODE_PRIVATE)
        readdata()
        layMain.setBackgroundColor(myColor)
        getColor.setOnClickListener {
            ColorPickerDialog.Builder(activity)
                    .setTitle(resources.getString(R.string.ColorPicker_Dialog))
                    .setPositiveButton(getString(R.string.confirm),
                            ColorEnvelopeListener { envelope, _ ->
                                myColor = envelope.color
                                layMain.setBackgroundColor(envelope.color)
                                savedata()
                            })
                    .setNegativeButton(getString(R.string.cancel)
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                    .attachBrightnessSlideBar(false) // default is true. If false, do not show the BrightnessSlideBar.
                    .show()
        }
        return root
    }

    private fun savedata() {
        val editor = msp.edit()
        editor.putInt(KEY_COLOR, myColor)
        editor.apply()
    }

    private fun readdata() {
        if (msp.contains(KEY_COLOR)) myColor = msp.getInt(KEY_COLOR, 11011010)
    }

    override fun onResume() {
        super.onResume()
        readdata()
    }

    companion object {
        private const val KEY_COLOR = "color"
    }
}