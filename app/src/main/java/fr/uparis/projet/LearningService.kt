package fr.uparis.projet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

/**
 * Plusieurs remarques sur notre code (pas eu le temps de modifier) :
 * 1) on fait interagir le service avec la base de donnees
 * directement, on devrait peut etre trouver une autre alternative
 * (Repository)
 * 2) a cause des restrictions android (a partir de Android 8.0?)
 * il n'est plus possible de lancer le service quand l'app est en background
 * -> il semblerait que c'est ce qui se passe (l. 186) malgre qu'on a une
 * notification pour interagir avec l'application (conformement aux exceptions
 * donnees par la doc Android..)
 * 3) la gestion des id pour les notifications est clairement mauvaise : il
 * aurait fallu faire un random pour assurer l'unicite des id, mais on utilise
 * cet id pour get le mot et sa traduction dans words[] et translations[]
 * -> l'utilisateur doit donc ici faire a des moments differents les sessions..
 */
@RequiresApi(Build.VERSION_CODES.M)
class LearningService : Service() {
    private val CHANNEL_ID="channel"
    private val notificationManager by lazy {getSystemService(NotificationManager::class.java)}
    private var translations: ArrayList<String>? = null
    private var words: ArrayList<String>? = null
    private

    val db by lazy{DictionariesDatabase.getDB(applicationContext)}
    val dao by lazy{db.dao()}

    private lateinit var notificationReceiver: BroadcastReceiver

    override fun onCreate() {
        /** creation du broadcast receiver pour supp notif + ouvrir trad **/
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent!=null){
                    val id = intent.extras?.getInt("ID") //notification id
                    if(id!=null && context != null){
                        if(intent.action=="process notification") openTranslation(id)
                        else if(intent.action=="dismissed") {// on remplace la notification selon le type de session --ne marche pas..
                            thread {
                                Log.d("clicked", "dismissed")
                                if(intent.extras?.getString("type")!! == "special") { //session de type special
                                    val langSrc = intent.extras?.getString("langSrc")
                                    val langDst = intent.extras?.getString("langDst")
                                    val word = getWordReplacementForPair(langSrc!!, langDst!!)
                                    words!![id-1] = word.word
                                    translations!![id-1] = word.urlToTranslation
                                    val actionPendingIntent = createActionPendingIntent(id)
                                    val dismissedPendingIntent = createOnDismissedPendingIntentSpecial(id, langSrc, langDst)
                                    val notification = createNotification(word.word, langSrc, langDst, actionPendingIntent, dismissedPendingIntent)
                                    notificationManager.notify(id, notification)
                                } else { //session de type global
                                    val word = getWordReplacementGlobal()
                                    words!![id-1] = word.word
                                    translations!![id-1] = word.urlToTranslation
                                    val actionPendingIntent = createActionPendingIntent(id)
                                    val dismissedPendingIntent = createOnDismissedPendingIntentGlobal(id)
                                    val notification = createNotification(word.word, word.lang_src, word.lang_dst, actionPendingIntent, dismissedPendingIntent)
                                    notificationManager.notify(id, notification)
                                }
                            }
                        }
                    }
                }
            }
        }
        registerReceiver(notificationReceiver, IntentFilter("process notification"))

        /** creation d'un seul notification channel puis regroupement par langue ensuite **/
        createNotificationChannel()
    }

    /**
     * Quand on commence le service, on cree les notifications et on les affiche
     * en fonction des informations qu'on a recues par l'alarme sur le "type" (paire
     * de langue ou global sans distinction des langues)
     * d'apprentissage
     * On recoit aussi les mots par l'alarme
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            /** for notification action **/
            words=intent.extras?.getStringArrayList("listWords")
            translations = intent.extras?.getStringArrayList("translations")
            val sessionType = intent.extras?.getString("sessionType")
            val nbWords = intent.extras?.getInt("nbWords")
            if(sessionType != null && sessionType == "special"){
                val langSrc = intent.extras?.getString("lang_src")
                val langDst = intent.extras?.getString("lang_dst")
                for(i in 1 ..nbWords!!){
                    /** notifications prises en charge par un broadcast receiver **/
                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(langSrc!! + " -> " + langDst!!)
                        .setContentText(words!![i-1])
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.icons8_translator)
                        .addAction(R.drawable.icons8_translator, "TRANSLATE", createActionPendingIntent(i))
                        .setDeleteIntent(createOnDismissedPendingIntentGlobal(i))
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(i, notification)
                }
            }
            else if(sessionType != null){ //global
                val langSrc = intent.extras?.getStringArrayList("lang_src")
                val langDst = intent.extras?.getStringArrayList("lang_dst")
                for(i in 1 .. nbWords!!){
                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(langSrc?.get(i-1)!! + " -> " + langDst?.get(i-1)!!)
                        .setContentText(words!![i-1])
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.icons8_translator)
                        .addAction(R.drawable.icons8_translator, "TRANSLATE", createActionPendingIntent(i))
                        .setDeleteIntent(createOnDismissedPendingIntentSpecial(i, langSrc.get(i-1)!!, langDst.get(i-1)!!))
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(i, notification)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** cree une notification avec les pending intents **/
    private fun createNotification(word: String, langSrc: String, langDst: String, pendingIntent: PendingIntent, dismissedPendingIntent: PendingIntent): Notification{
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$langSrc -> $langDst")
            .setContentText(word)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.icons8_translator)
            .addAction(R.drawable.icons8_translator, "TRANSLATE", pendingIntent)
            .setDeleteIntent(dismissedPendingIntent)
            .setAutoCancel(true)
            .build()
    }

    /** bouton pour ouvrir la traduction **/
    private fun createActionPendingIntent(requestCodeAndId: Int): PendingIntent{
        val notifIntent = Intent("process notification")
        notifIntent.putExtra("ID", requestCodeAndId)
        return PendingIntent.getBroadcast(this, requestCodeAndId, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }


    /** quand on supprime une notification **/
    private fun createOnDismissedPendingIntentSpecial(requestCode: Int, langSrc: String, langDst: String): PendingIntent{
        val onDismissedIntent = Intent("dismissed")
        onDismissedIntent.putExtra("type", "special")
        onDismissedIntent.putExtra("langSrc", langSrc)
        onDismissedIntent.putExtra("langDst", langDst)
        return PendingIntent.getBroadcast(this, requestCode, onDismissedIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createOnDismissedPendingIntentGlobal(requestCode: Int): PendingIntent{
        val onDismissedIntent = Intent("dismissed")
        onDismissedIntent.putExtra("type", "global")
        return PendingIntent.getBroadcast(this, requestCode, onDismissedIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    /* Rmq : creer un seul notif channel pour tous les apprentissages
     * semble le plus adapte, dans la mesure ou on ne cherche
     * pas a avoir des notifications aux comportements differents
     * (bruits, visuels etc)
     * Par contre, grouper les notifications au sein du channel
     * est une bonne idee quand on apprend plusieurs langues en
     * meme temps --extension non implementee
     */
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                CHANNEL_ID,
                "Learning channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description="This channel is for your learning sessions with the Dictionary app."
            notificationManager.createNotificationChannel(channel)
        }
    }

    /** ouvre la notification **/
    fun openTranslation(notifId: Int){//ne marche que depuis l'application
        if(translations != null && notifId>0){
            val webTranslation = Uri.parse(translations!![notifId-1])
            val webIntent = Intent(Intent.ACTION_VIEW, webTranslation)
            webIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(webIntent)
            notificationManager.cancel(notifId)
            updateLookedUp(words!![notifId-1])

        }
    }

    fun updateLookedUp(wordName: String){
        thread{
            val word = dao.getWord(wordName)
            dao.updateLookedUp(word.lookedUp+1, word.idWord)
        }
    }


    /** donner un nouveau mot pour remplacer **/
    fun getWordReplacementGlobal(): Word{
        return dao.loadWordsGlobal().asSequence().shuffled().take(1).first()
    }

    fun getWordReplacementForPair(langSrc: String, langDst: String): Word{
        return dao.loadWordsOfPair(langSrc, langDst).asSequence()
            .shuffled().take(1).first()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    override fun onBind(intent: Intent): IBinder {TODO()}
}