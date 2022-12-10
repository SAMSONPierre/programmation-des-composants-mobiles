package fr.uparis.projet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            val toSearch = "${binding.wordEdit.text.toString()} ${binding.langSrcGoogle.text.toString()} to ${binding.langDstGoogle.text.toString()}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.google.fr/search?q=$toSearch")
            startActivity( intent )

        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SearchTranslationFragment()
    }
}