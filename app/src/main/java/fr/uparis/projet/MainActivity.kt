package fr.uparis.projet

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import fr.uparis.projet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    class ScreenSlidePagerAdapter(fa: FragmentActivity, var fragmentList: MutableList<Fragment>): FragmentStateAdapter( fa ){
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment =
            fragmentList[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** binding et action bar **/
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setStatusBarColor()

        /** creation de nos fragments **/
        val searchFragment=SearchTranslationFragment.newInstance()
        val dictionaryFragment=ListDictionaryFragment.newInstance()

        /** creation de notre pager adapter pour le contenu de pager **/
        val pagerAdapter=ScreenSlidePagerAdapter(
            this, mutableListOf(searchFragment, dictionaryFragment)
        ) // le relie aussi a notre activity this
        binding.pager.adapter=pagerAdapter

        /** mettre les noms de nos tabs **/
        val tabs=listOf("SEARCH", "DICTIONARIES")
        TabLayoutMediator(binding.tabLayout, binding.pager){
            tab, position -> tab.text=tabs[position]
        }.attach()

    }

    /** changer la couleur du status bar **/
    private fun setStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.teal_700)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when(item.itemId){
            R.id.exit->{
                finish()
                true
            }
            R.id.settings->{
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                true
            }
            else->super.onOptionsItemSelected(item)
        }
}