package fr.uparis.projet

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import fr.uparis.projet.databinding.FragmentListWordListBinding
import kotlin.concurrent.thread

/**
 * A fragment representing a list of Items.
 */
class ListWordFragment() : Fragment() {
    private val model: MainViewModel by activityViewModels()
    lateinit var binding: FragmentListWordListBinding
    val adapter by lazy{MyWordRecyclerViewAdapter(binding, model.listWords?:listOf(), model)}

    override fun onStart() {
        super.onStart()
        /** observer sur la liste de mots d'un dictionnaire **/
        model.selectedDicWords?.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                adapter.words=it[0].words
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_word_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** creation du binding **/
        binding=FragmentListWordListBinding.bind(view)

        /** set adapter de notre recycler view **/
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(context)

        /** on swipe delete en utilisant notre delete item touch helper **/
        val itemTouchHelper= ItemTouchHelper(DeleteCallback(requireContext(), "word", model))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        /** review == ouvrir la traduction et on
         * ne peut en regarder qu une a la fois
         */
        binding.reviewButton.setOnClickListener {
            when(model.selectedWords.size){
                1 -> {
                    val word = model.selectedWords[0]
                    model.selectedWords.remove(word)
                    binding.reviewButton.isEnabled = false
                    val webTranslation = Uri.parse(word.urlToTranslation)
                    val webIntent = Intent(Intent.ACTION_VIEW, webTranslation)
                    thread { model.updateLookedUp(word.lookedUp+1, word.idWord) }
                    startActivity(webIntent)
                }
                else -> Toast.makeText(requireContext(), "You can only review one word at a time.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        /** reset == lookedUp = 0 : on fait comme si on venait d'ajouter le mot
         * et comme si l'utilisateur ne l'avait jamais revise
         */
        binding.resetButton.setOnClickListener {
            when(model.selectedWords.size){
                0 -> Toast.makeText(requireContext(), "Please select a word to reset.", Toast.LENGTH_LONG)
                    .show()
                else -> AlertDialog.Builder(context)
                    .setMessage("Resetting words will bring them back to \"unreviewed/new word\" state.\n"
                    + "Do you want to continue?")
                    .setPositiveButton("Yes" ){ _, _ ->
                        model.resetWords()
                    }
                    .setNegativeButton("Cancel"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(idDic: Long)=ListWordFragment().apply {
            arguments=Bundle().apply { putLong("idDic", idDic) }
        }
    }
}