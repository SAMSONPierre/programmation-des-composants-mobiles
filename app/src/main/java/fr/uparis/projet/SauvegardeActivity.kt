package fr.uparis.projet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import fr.uparis.projet.databinding.ActivitySauvegardeBinding
import kotlin.concurrent.thread

class SauvegardeActivity : AppCompatActivity() {

    lateinit var binding: ActivitySauvegardeBinding
    private val model: SauvegardeViewModel by viewModels()

    private fun parseURL(word : String, url : String) : String{
        val index = url.indexOf(word.lowercase().trim())
        return if(index == -1) ""
        else url.subSequence(0,index).toString()
    }

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
            val urlWord= binding.dictUrlEdit.text.toString().trim()
            val langSRC = binding.sourceEdit.text.toString().trim()
            val langDST = binding.targetEdit.text.toString().trim()
            val word = binding.wordEdit.text.toString().trim()
            val urlDic = parseURL(word,urlWord)
            if(langSRC == "" || langDST == "" || word == ""){
                Toast.makeText(this, "Fill the empty field(s)", Toast.LENGTH_SHORT).show()
            }
            else if(urlDic == ""){ //TODO faire un autre parsing parce que sinon on ne peut pas faire de phrases...
                Toast.makeText(this, "Make sure to enter the correct word", Toast.LENGTH_SHORT).show()
            }
            else{
                // Insertion des différents éléments
                thread{
                    model.insertLanguage(langSRC)
                    model.insertLanguage(langDST)
                    //Thread.sleep(500) // Pour laisser le temps aux langages d'être insérés et que ça ne crée pas une erreur de FOREIGN KEY
                    val idWord=model.insertWord(word,langSRC,langDST,urlWord)
                    var idDic=model.insertDictionnary(langSRC,langDST,urlDic)
                    if(idDic==-1L) idDic=model.getDicID(urlDic)
                    val wd=model.insertWordDicAssociation(idWord, idDic)
                    Log.d("last dic", "$idWord")
                    Log.d("last word", "$idDic")
                    for(id in wd) Log.d("l[] WD after", "$id")
                }

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