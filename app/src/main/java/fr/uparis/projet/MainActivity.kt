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
                .add(R.id.fragment_container_view, ViewPagerFragment.newInstance())
                .commit()
        }

        thread { startLearningService() }
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

    /** start service **/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun startLearningService(){
        val intent = Intent(this, LearningService::class.java)
        val words = model.getDailyWords()
        if(words.count()>=10){
            intent.putStringArrayListExtra("listWords", words)
            intent.putStringArrayListExtra("translations", model.getDailyWordsTranslation())
            intent.putStringArrayListExtra("lang_src", model.getDailyWordsLangSrc())
            intent.putStringArrayListExtra("lang_dst", model.getDailyWordsLangDst())
            startService(intent)
        }
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