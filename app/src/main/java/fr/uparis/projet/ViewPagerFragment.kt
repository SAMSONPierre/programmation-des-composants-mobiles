package fr.uparis.projet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import fr.uparis.projet.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {
    lateinit var binding: FragmentViewPagerBinding

    class ScreenSlidePagerAdapter(fa: FragmentActivity, var fragmentList: MutableList<Fragment>): FragmentStateAdapter(fa){
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment =
            fragmentList[position]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** creation du binding **/
        binding = FragmentViewPagerBinding.bind(view)

        /** creation de nos fragments **/
        val searchFragment=SearchTranslationFragment.newInstance()
        val dictionaryFragment=ListDictionaryFragment.newInstance()

        /** creation de notre pager adapter pour le contenu de pager **/
        val pagerAdapter= ScreenSlidePagerAdapter(
            requireActivity(), mutableListOf(searchFragment, dictionaryFragment)
        ) // le relie aussi a notre activity this
        binding.pager.adapter=pagerAdapter

        /** mettre les noms de nos tabs **/
        val tabs=listOf("SEARCH", "DICTIONARIES")
        TabLayoutMediator(binding.tabLayout, binding.pager){
                tab, position -> tab.text=tabs[position]
        }.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_view_pager, container, false)
        return view
    }

    companion object {
        @JvmStatic fun newInstance() =
                ViewPagerFragment()
    }
}