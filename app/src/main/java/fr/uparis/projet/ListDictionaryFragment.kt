package fr.uparis.projet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import fr.uparis.projet.databinding.FragmentListDictionaryBinding
import fr.uparis.projet.databinding.FragmentListDictionaryListBinding
import fr.uparis.projet.databinding.FragmentListWordBinding

class ListDictionaryFragment() : Fragment() {
    private val model: MainViewModel by activityViewModels() // shared viewModel entre MainActivity et les fragments
    lateinit var binding: FragmentListDictionaryListBinding
    val adapter by lazy{MyDictionaryRecyclerViewAdapter(model.listDictionaries.value?:listOf(), model)}

    override fun onStart() {
        super.onStart()
        /** observer sur la liste de dictionnaires **/
        model.listDictionaries.observe(viewLifecycleOwner){
            adapter.dictionaries=it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_list_dictionary_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** creation du binding **/
        binding=FragmentListDictionaryListBinding.bind(view)

        /** set adapter de notre recycler view **/
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(context)
    }

    companion object {
        @JvmStatic
        fun newInstance()=ListDictionaryFragment()
    }
}