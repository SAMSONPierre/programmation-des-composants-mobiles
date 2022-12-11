package fr.uparis.projet

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import fr.uparis.projet.databinding.FragmentListWordBinding
import fr.uparis.projet.databinding.FragmentListWordListBinding

/**
 * A fragment representing a list of Items.
 */
class ListWordFragment() : Fragment() {
    private val model: MainViewModel by activityViewModels()
    lateinit var binding: FragmentListWordListBinding
    val adapter by lazy{MyWordRecyclerViewAdapter(model.listWords?:listOf(), model)}

    override fun onStart() {
        super.onStart()
        /** observer sur la liste de mots d'un dictionnaire **/
        model.selectedDicWords?.observe(viewLifecycleOwner){
            adapter.words=it[0].words
            adapter.notifyDataSetChanged()
        }

        model.selectedDicWords?.observe(viewLifecycleOwner){

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_word_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** creation du binding **/
        binding=FragmentListWordListBinding.bind(view)

        /** set adapter de notre recycler view **/
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(context)
    }

    companion object {
        @JvmStatic
        fun newInstance(idDic: Long)=ListWordFragment().apply {
            arguments=Bundle().apply { putLong("idDic", idDic) }
        }
    }
}