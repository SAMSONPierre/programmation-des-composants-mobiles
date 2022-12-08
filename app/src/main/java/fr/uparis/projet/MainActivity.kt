package fr.uparis.projet

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.uparis.projet.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    /*class ScreenSlidePagerAdapter(fa: FragmentActivity, var fragmentList: MutableList<Fragment>): FragmentStateAdapter(fa){
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment =
            fragmentList[position]
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** binding et action bar **/
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setStatusBarColor()

        /** creation de nos fragments **/
        /*val searchFragment=SearchTranslationFragment.newInstance()
        val dictionaryFragment=ListDictionaryFragment.newInstance()
*/
        /** creation de notre pager adapter pour le contenu de pager **/
        /*val pagerAdapter= ViewPagerFragment.ScreenSlidePagerAdapter(
            this, mutableListOf(searchFragment, dictionaryFragment)
        ) // le relie aussi a notre activity this
        binding.pager.adapter=pagerAdapter

        *//** mettre les noms de nos tabs **//*
        val tabs=listOf("SEARCH", "DICTIONARIES")
        TabLayoutMediator(binding.tabLayout, binding.pager){
            tab, position -> tab.text=tabs[position]
        }.attach()*/

        /** ajout de notre fragment avec view pager **/
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, ViewPagerFragment.newInstance())
            .commit()
        /*
        //TODO partie tests affichage a enlever quand ok
        thread {
            model.dao.insertLanguage(Lang("English"))
            model.dao.insertLanguage(Lang("French"))
            model.dao.insertLanguage(Lang("Chinese"))
            model.dao.insertDictionary(DictionaryLang("English", "French", "https://fr.wikipedia.org/"))
            model.dao.insertDictionary(DictionaryLang("French", "English", "https://www.linguee.fr/francais-anglais/"))
            model.dao.insertDictionary(DictionaryLang("French", "Chinese", "https://blabla"))
            model.dao.insertWord(WordInfo2("bonjour", "French", "English", "https://www.linguee.fr/francais-anglais/search?source=auto&query=bonjour&cw=336"))
            model.dao.insertWord(WordInfo2("ciment", "French", "English", "https://www.linguee.fr/francais-anglais/search?source=auto&query=ciment&cw=336"))
        }
        */
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