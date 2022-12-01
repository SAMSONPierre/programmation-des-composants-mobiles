package fr.uparis.projet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import fr.uparis.projet.databinding.ActivitySauvegardeBinding

class SauvegardeActivity : AppCompatActivity() {

    lateinit var binding: ActivitySauvegardeBinding
    private val model: SauvegardeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySauvegardeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if( intent.action.equals( "android.intent.action.SEND" ) ){
            val txt = intent.extras?.getString( "android.intent.extra.TEXT" )
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
            binding.dictUrlEdit.setText(txt)
        }

        binding.saveButton.setOnClickListener{
            val url = binding.dictUrlEdit.text.toString()
            val src_lang = binding.sourceEdit.text.toString()
            val dst_lang = binding.targetEdit.text.toString()
            val word = binding.wordEdit.text.toString()
            if(src_lang == "" || dst_lang == "" || word == ""){
                Toast.makeText(this, "Fill the empty field(s)", Toast.LENGTH_SHORT).show()
            }
            else{
                // Insertion des différents éléments
                model.insertLanguage(src_lang)
                model.insertLanguage(dst_lang)
                model.insertWord(word,src_lang,dst_lang,url)
                model.insertDictionnary(src_lang,dst_lang,url)

                // On retourne à la page principale
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.cancelButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}