package fr.uparis.projet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.projet.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private val model: MainViewModel by activityViewModels()
    lateinit var binding: FragmentSettingsBinding
    private val adapterRV by lazy{LanguageRecyclerViewAdapter(childFragmentManager, model.listPairLanguages.value?:listOf(), model)}

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
    }

    /** pour l'heure de la session global **/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setOnClickTimeGlobal(){
        val calendar = Calendar.getInstance()
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        binding.timeGlobal.setOnClickListener{
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                        //TODO
            }, h, m, true).show()
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
                        AlertDialog.Builder(requireContext())
                            .setMessage("Global sessions are meant to review all saved words, regardless of source and " +
                                    "target languages. If you wish to only review words by groups of languages, please disable " +
                                    "global sessions. \n" +
                                    "To optimize learning several languages, global sessions only take" +
                                    " place once a day when enabled, but you can choose at what time.")
                            .show()
                    }
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListenerSpecific(){
        binding.infoSpecial.setOnTouchListener { view, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> {
                    val textLocation=IntArray(2)
                    binding.infoGlobal.getLocationOnScreen(textLocation)
                    if(event.rawX >= textLocation[0] + binding.infoGlobal.right - binding.infoGlobal.totalPaddingRight){
                        AlertDialog.Builder(requireContext())
                            .setMessage("Special sessions are custom sessions for each pair of " +
                                    "source and target languages, and for which you can choose time of session, and frequency. \n" +
                                    "Like global sessions, you can choose to disable certain special sessions.")
                            .show()
                    }
                }
            }
            true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}