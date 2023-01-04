package fr.uparis.projet

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.ContentInfoCompat.Flags
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fr.uparis.projet.databinding.FragmentSettingsBinding
import kotlin.concurrent.thread

class SettingsFragment : Fragment() {
    private val model: MainViewModel by activityViewModels()
    lateinit var binding: FragmentSettingsBinding
    private val adapterRV by lazy{LanguageRecyclerViewAdapter(childFragmentManager, model.listPairLanguages.value?:listOf(), model)}
    private var hourGlobal = 18
    private var minuteGlobal = 0

    /** information dialogs **/
    private lateinit var specialSessionInfo: AlertDialog
    private lateinit var globalSessionInfo: AlertDialog
    /** end of information dialogs **/

    val alarmManager by lazy {requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager}

    override fun onStart() {
        super.onStart()
        model.listPairLanguages.observe(viewLifecycleOwner){
            adapterRV.pairs = it
            adapterRV.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** creation du binding **/
        binding=FragmentSettingsBinding.bind(view)

        setOnTouchListenerGlobal()
        setOnTouchListenerSpecific()
        setOnClickTimeGlobal()

        binding.recyclerView.adapter = adapterRV
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        /** restore switch compat isChecked and edit text value **/
        binding.switchGlobal.isChecked = model.isEnabledGlobal
        binding.nbWords.setText(model.nbWordsGlobal.toString())

        binding.saveButton.setOnClickListener {
            val nbWords = binding.nbWords.text.toString()
            if(nbWords == "" && binding.switchGlobal.isChecked){
                Snackbar.make(requireActivity().findViewById(R.id.fragment_container_view), "Please choose how many words for your global session.", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if(binding.switchGlobal.isChecked) {
                planGlobalSession(hourGlobal, minuteGlobal, nbWords.toInt())
                model.isEnabledGlobal = binding.switchGlobal.isChecked
                model.nbWordsGlobal = binding.nbWords.text.toString().toInt()
            }
        }

        specialSessionInfo = AlertDialog.Builder(requireContext())
            .setMessage("Special sessions are custom sessions for each pair of " +
                    "source and target languages, and for which you can choose time of session, and frequency. \n" +
                    "Like global sessions, you can choose to disable certain special sessions.")
            .create()

        globalSessionInfo = AlertDialog.Builder(requireContext())
            .setMessage("Global sessions are meant to review all saved words, regardless of source and " +
                    "target languages. If you wish to only review words by groups of languages, please disable " +
                    "global sessions. \n" +
                    "To optimize learning several languages, global sessions only take" +
                    " place once a day when enabled, but you can choose at what time.")
            .create()
    }

    /** pour l'heure de la session global **/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setOnClickTimeGlobal(){
        binding.timeGlobal.setOnClickListener{
            TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                hourGlobal = hourOfDay
                minuteGlobal = minute
            }, 18, 0, true).show()
        }
    }

    /** on touch listeners pour les petits icones information **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListenerGlobal(){
        binding.infoGlobal.setOnTouchListener { view, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> {
                    val textLocation=IntArray(2)
                    binding.infoGlobal.getLocationOnScreen(textLocation)
                    if(event.rawX >= textLocation[0] + binding.infoGlobal.right - binding.infoGlobal.totalPaddingRight){
                        globalSessionInfo.show()
                    }
                }
            }
            true
        }
    }

    /** info dialog pour les sessions speciales **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListenerSpecific(){
        binding.infoSpecial.setOnTouchListener { view, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> {
                    val textLocation=IntArray(2)
                    binding.infoGlobal.getLocationOnScreen(textLocation)
                    if(event.rawX >= textLocation[0] + binding.infoGlobal.right - binding.infoGlobal.totalPaddingRight){
                        specialSessionInfo.show()
                    }
                }
            }
            true
        }
    }


    /**
     * REMARQUE GENERALE POUR LES ALARMES : ON N A PAS DE MOYEN POUR LES ANNULER
     * un fragment avec la liste de toutes les alarmes qui ont ete planifiees seraient mieux
     * style application native pour les reveils
     */

    /**
     * alarme pour les sessions globales
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun planGlobalSession(hour: Int, minute: Int, nbWords: Int){
        thread {
            val intent = Intent(requireContext(), LearningService::class.java)

            /** extras pour la seance globale **/
            val words = model.getGlobalDailyWords(nbWords)
            if (words.size > 0){
                 val wordsName = mutableListOf<String>()
                for(word in words) wordsName.add(word.word)
                intent.putExtra("nbWords", words.size)
                intent.putExtra("sessionType", "global")
                intent.putStringArrayListExtra("listWords", ArrayList(wordsName))
                intent.putStringArrayListExtra("translations", model.getWordsTranslation(words))
                intent.putStringArrayListExtra("lang_src", model.getGlobalDailyWordsLangSrc())
                intent.putStringArrayListExtra("lang_dst", model.getGlobalDailyWordsLangDst())

                val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                val pendingIntent = PendingIntent.getService(requireContext(), 1, intent, flags)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent
                )
            }
        }
    }

    /**
     * alarme pour les sessions speciales
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun planSpecialSession(pair: LanguagePair, days: MutableList<Int>, hour: Int, minute: Int, nbWords: Int){
        thread{
            for(day in days){
                val intent = Intent(requireContext(), LearningService::class.java)
                val words = model.getWordsForPair(pair.lang_src, pair.lang_dst, nbWords)
                if(words.size > 0){
                    /** extras == toutes les informations sur les mots et le type de session a envoyer au service **/
                    intent.putExtra("sessionType", "special")
                    intent.putExtra("lang_src", pair.lang_src)
                    intent.putExtra("lang_dst", pair.lang_dst)
                    intent.putExtra("nbWords", words.size)
                    val wordsName = mutableListOf<String>()
                    for(word in words) wordsName.add(word.word)
                    intent.putStringArrayListExtra("listWords", ArrayList(wordsName))
                    intent.putStringArrayListExtra("translations", model.getWordsTranslation(words))

                    val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    val pendingIntent = PendingIntent.getService(requireContext(), model.requestCodeAlarm, intent, flags)
                    model.requestCodeAlarm+=1 //unique a chaque alarme

                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_WEEK, day)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)

                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis, 7*AlarmManager.INTERVAL_DAY, pendingIntent
                    )
                }
            }
        }
    }


    /** dismissing all opened dialogs on orientation change **/
    /**
     * rmq : on fait expres de ne pas sauvegarder l'etat de
     * adapterRV.currentDialog (dialogue de config) puisque sinon
     * c'est comme si l'utilisateur avait click sur save avant
     * de dismiss() : on ne veut pas sauvegarder de configuration
     * dans le ViewModel autrement que par le bouton save
     */
    override fun onPause() {
        if(specialSessionInfo.isShowing) specialSessionInfo.dismiss()
        if(globalSessionInfo.isShowing) globalSessionInfo.dismiss()
        if(adapterRV.currentDialog != null &&
                adapterRV.currentDialog!!.isVisible) adapterRV.currentDialog!!.dismiss()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}