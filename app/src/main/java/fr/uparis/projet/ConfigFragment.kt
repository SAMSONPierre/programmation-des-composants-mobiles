package fr.uparis.projet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import fr.uparis.projet.databinding.ConfigSpecialSessionBinding
import java.util.*
import kotlin.concurrent.thread

class ConfigFragment(val model: MainViewModel, val pair: LanguagePair): DialogFragment(){
    lateinit var binding: ConfigSpecialSessionBinding

    /** to save config view **/
    private var monday = false
    private var tuesday = false
    private var wednesday = false
    private var thursday = false
    private var friday = false
    private var saturday = false
    private var sunday = false
    private var hour = 0
    private var minute = 0
    private var nbWords = 10

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.config_special_session, container, false)
        binding = ConfigSpecialSessionBinding.bind(view)
        binding.time.setIs24HourView(true)

        /** restore config **/
        val configuration = model.getConfiguration(pair)
        if(configuration != null){
            with(binding){
                monday.isChecked = configuration.monday
                tuesday.isChecked = configuration.tuesday
                wednesday.isChecked = configuration.wednesday
                thursday.isChecked = configuration.thursday
                friday.isChecked = configuration.friday
                saturday.isChecked = configuration.saturday
                sunday.isChecked = configuration.sunday
                time.hour = configuration.hour
                time.minute = configuration.minute
                words.setText(configuration.nbWords.toString())
            }
        }

        binding.cancelButton.setOnClickListener { dismiss() }
        binding.saveButton.setOnClickListener {
            manageAlarm()
            saveConfig()
        }
        return view
    }

    /** creates alarm based on config dialog, then dismisses dialog **/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun manageAlarm(){
        val days = mutableListOf<Int>()
        monday = binding.monday.isChecked
        tuesday = binding.tuesday.isChecked
        wednesday = binding.wednesday.isChecked
        thursday = binding.thursday.isChecked
        friday = binding.friday.isChecked
        saturday = binding.saturday.isChecked
        sunday = binding.sunday.isChecked

        if(monday) days.add(Calendar.MONDAY)
        if(tuesday) days.add(Calendar.TUESDAY)
        if(wednesday) days.add(Calendar.WEDNESDAY)
        if(thursday) days.add(Calendar.THURSDAY)
        if(friday) days.add(Calendar.FRIDAY)
        if(saturday) days.add(Calendar.SATURDAY)
        if(sunday) days.add(Calendar.SUNDAY)

        hour = binding.time.hour
        minute = binding.time.minute

        val nbWordsEdit = binding.words.text.toString()
        if(nbWordsEdit!="") nbWords=nbWordsEdit.toInt() //par defaut 10 mots

        if(days.isNotEmpty()){
            (parentFragment as SettingsFragment).planSpecialSession(pair, days, hour, minute, nbWords.toInt())
        }
        dismiss()
    }

    /** saves configuration on save button click **/
    private fun saveConfig(){
        model.addConfiguration(
                nbWords, pair, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                hour, minute
        )
    }
}
