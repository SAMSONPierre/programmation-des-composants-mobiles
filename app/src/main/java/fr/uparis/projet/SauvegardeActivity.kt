package fr.uparis.projet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import fr.uparis.projet.databinding.ActivitySauvegardeBinding

class SauvegardeActivity : AppCompatActivity() {

    lateinit var binding: ActivitySauvegardeBinding
    private val model: SauvegardeViewModel by viewModels()
    private var lastDic : Long = 0
    private var lastWord : Long = 0


    private fun parseURL(word : String, url : String) : String{
        var index = url.indexOf(word.lowercase().trim())
        return if(index == -1) ""
        else url.subSequence(0,index).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySauvegardeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.idWord.observe(this){
            lastWord = it

        }
        model.idDic.observe(this){
            lastDic = it
        }

        if( intent.action.equals( "android.intent.action.SEND" ) ){
            val txt = intent.extras?.getString( "android.intent.extra.TEXT" )
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
            binding.dictUrlEdit.setText(txt)
        }

        binding.saveButton.setOnClickListener{
            val urlWord= binding.dictUrlEdit.text.toString().trim()
            val langSRC = binding.sourceEdit.text.toString().trim()
            val langDST = binding.targetEdit.text.toString().trim()
            val word = binding.wordEdit.text.toString().trim()
            val urlDic = parseURL(word,urlWord)
            if(langSRC == "" || langDST == "" || word == ""){
                Toast.makeText(this, "Fill the empty field(s)", Toast.LENGTH_SHORT).show()
            }
            else if(urlDic == ""){
                Toast.makeText(this, "Make sure to enter the correct word", Toast.LENGTH_SHORT).show()
            }
            else{
                // Insertion des différents éléments
                model.insertLanguage(langSRC)
                model.insertLanguage(langDST)
                Thread.sleep(500) // Pour laisser le temps aux langages d'être insérés et que ça ne crée pas une erreur de FOREIGN KEY
                model.insertWord(word,langSRC,langDST,urlWord)
                model.insertDictionnary(langSRC,langDST,urlDic)
                /* TODO: Trouver une solution plus viable que ça */
                Thread.sleep(200)
                Thread{
                    model.getLastDic(urlDic)
                    model.getLastWord(word,langSRC,langDST)
                    model.insertWordDicAssociation(lastWord,lastDic)
                }.start()




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