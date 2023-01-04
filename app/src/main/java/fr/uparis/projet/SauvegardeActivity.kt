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

    private fun countOccurrences(s: String, ch: Char): Int {
        return s.filter { it == ch }.count()
    }

    private fun parseURL(word : String, url : String) : String{
        if(countOccurrences(word,' ') > 0){
            // 3 de manières arbitraires parce que dans les URLs j'ai vu que l'apostrophe pouvait être changée par "-" ou "%27"
            // et je peux pas mettre ".*" parce que sinon ça peut très mal parser l'URL.
            val pattern = Regex(word.replace("'",".{0,3}").replace(" ",".*"))
            val match = pattern.find(url)!!

            if(match.value != ""){
                val index = url.indexOf(match.value)
                return url.subSequence(0,index).toString()
            }
            return ""
        }
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
            else if(urlDic == ""){
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
                    model.insertWordDicAssociation(idWord, idDic)
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