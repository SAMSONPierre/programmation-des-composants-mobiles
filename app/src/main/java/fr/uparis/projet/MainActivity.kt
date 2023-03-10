package fr.uparis.projet

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.uparis.projet.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** binding et action bar **/
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setStatusBarColor()

        /** ajout de notre fragment avec view pager **/
        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, ViewPagerFragment.newInstance(), "viewPagerFragment")
                .commit()
        }
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, SettingsFragment.newInstance(), "settingsFragment")
                    .addToBackStack(null)
                    .commit()
                true
            }
            else->super.onOptionsItemSelected(item)
        }
}