package fr.uparis.projet

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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


class SearchTranslationFragment : Fragment(R.layout.fragment_search_translation) {
    private val model: MainViewModel by activityViewModels()
    lateinit var binding : FragmentSearchTranslationBinding

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
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.google.fr/search?q=$toSearch")
            startActivity( intent )
        }

        binding.searchButton.setOnClickListener {
            //test notifications
            val serviceIntent=Intent(context, LearningService::class.java)
            val pendingIntent= PendingIntent.getService(
                context, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE
            )
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) context?.startForegroundService(serviceIntent)
            else context?.startService(serviceIntent)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SearchTranslationFragment()
    }
}