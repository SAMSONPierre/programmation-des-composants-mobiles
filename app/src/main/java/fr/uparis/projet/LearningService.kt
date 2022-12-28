package fr.uparis.projet

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

@RequiresApi(Build.VERSION_CODES.M)
class LearningService : Service() {
    private val CHANNEL_ID="channel"
    private val notificationManager by lazy {getSystemService(NotificationManager::class.java)}
    private var translations: ArrayList<String>? = null
    private var words: ArrayList<String>? = null

    private lateinit var notificationReceiver: BroadcastReceiver

    override fun onCreate() {
        /** creation du broadcast receiver pour supp notif + ouvrir trad **/
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent!=null){
                    val id = intent.extras?.getInt("ID")
                    if(id!=null){
                        openTranslation(id)
                    }
                }
            }
        }
        registerReceiver(notificationReceiver, IntentFilter("process notification"))

        /** creation d'un seul notification channel puis regroupement par langue ensuite **/
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            /** for notification action **/
            words=intent.extras?.getStringArrayList("listWords")
            translations = intent.extras?.getStringArrayList("translations")
            val langSrc = intent.extras?.getStringArrayList("lang_src")
            val langDst = intent.extras?.getStringArrayList("lang_dst")
            /** create notifications and passing their translation in intent **/
            if(words != null && translations != null && langSrc != null && langDst != null){
                for(i in 1 .. 10){
                    val notifIntent = Intent("process notification")
                    notifIntent.putExtra("ID", i)
                    val pendingIntent = PendingIntent.getBroadcast(this, i, notifIntent, PendingIntent.FLAG_IMMUTABLE)

                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(langSrc[i-1] + " -> " + langDst[i-1])
                        .setContentText(words!![i-1])
                        .setSmallIcon(R.drawable.icons8_translator)
                        .addAction(R.drawable.icons8_translator, "TRANSLATE", pendingIntent)
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(i, notification)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /* Rmq : creer un seul notif channel pour tous les apprentissages
     * semble le plus adapte, dans la mesure ou on ne cherche
     * pas a avoir des notifications aux comportements differents
     * (bruits, visuels etc)
     * Par contre, grouper les notifications au sein du channel
     * est une bonne idee quand on apprend plusieurs langues en
     * meme temps
     */
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                CHANNEL_ID,
                "Learning channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description="This channel is for your learning sessions with the Dictionary app."
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun openTranslation(notifId: Int){//ne marche que depuis l'application
        if(translations != null && notifId>0){
            val webTranslation = Uri.parse(translations!![notifId-1     ])
            val webIntent = Intent(Intent.ACTION_VIEW, webTranslation)
            webIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(webIntent)
            notificationManager.cancel(notifId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    override fun onBind(intent: Intent): IBinder {TODO()}
}