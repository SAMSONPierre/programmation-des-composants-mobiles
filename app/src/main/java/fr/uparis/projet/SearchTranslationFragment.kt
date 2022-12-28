package fr.uparis.projet

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import fr.uparis.projet.databinding.FragmentSearchTranslationBinding
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import android.content.Intent as Intent1


class SearchTranslationFragment : Fragment(R.layout.fragment_search_translation) {
    private val model: MainViewModel by activityViewModels()
    private lateinit var binding : FragmentSearchTranslationBinding
    private lateinit var adapterSpinner : ArrayAdapter<String>

    override fun onStart() {
        super.onStart()

        model.listDictionariesSpinner.observe(viewLifecycleOwner){
            adapterSpinner.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_translation, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchTranslationBinding.bind(view);
        binding.googleButton.setOnClickListener{
            val srcLang=binding.langSrcGoogle.text.toString().lowercase().trim()
            val dstLang=binding.langDstGoogle.text.toString().lowercase().trim()
            // parser avant pour savoir si on part sur du monolingue ou bilingue
            val target = if(srcLang=="" || srcLang==dstLang) "$dstLang definition"
                        else if(dstLang=="") "$srcLang definition"
                        else "$srcLang to $dstLang"
            val toSearch = "${binding.wordEdit.text.toString().trim()} $target"
            val intent = Intent1(Intent1.ACTION_VIEW)
            intent.data = Uri.parse("https://www.google.fr/search?q=$toSearch")
            startActivity( intent )
        }

        binding.wordEdit.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadPartialName(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchButton.setOnClickListener { searchWord() }

        /** spinner adapter **/
        setSpinner()
    }


    private fun setSpinner(){
        adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapterSpinner
    }

    /** on met dynamiquement les dictionnaires contenant le mot dans le spinner, sinon on
     * le laisse vide
     */
    private fun loadPartialName(prefix: String){
        if(prefix=="") adapterSpinner.clear()
        else
            model.loadPartialName(prefix).observe(viewLifecycleOwner){
                adapterSpinner.clear()
                adapterSpinner.addAll(it)
                adapterSpinner.notifyDataSetChanged()
            }
    }

    /** search button onclick **/
    private fun searchWord() {
        if(binding.spinner.selectedItem == null)
            Snackbar.make(requireActivity().findViewById(R.id.fragment_container_view), "No dictionary matches the word/sentence.", Snackbar.LENGTH_LONG)
                .show()
        else {
            val intent=Intent(Intent.ACTION_VIEW)
            intent.data=Uri.parse(binding.spinner.selectedItem as String)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchTranslationFragment()
    }

}