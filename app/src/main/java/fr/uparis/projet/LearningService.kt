package fr.uparis.projet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class LearningService : Service() {
    private val CHANNEL_ID="channel"
    private val notificationManager by lazy {getSystemService(NOTIFICATION_SERVICE) as NotificationManager}

    override fun onCreate() {
        /** creation d'un seul notification channel puis regroupement par langue ensuite **/
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*val serviceIntent=Intent(this, LearningService::class.java).apply {
            action="TODO"
        }*/

        //val servicePendingIntent=PendingIntent.getService(this, 1, serviceIntent, PendingIntent.FLAG_IMMUTABLE)

        /*val activityPendingIntent=PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE)*/

        //for(i in 1..10){
            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Word ")
                .setContentText("getWordNbOfTheDay")
                .setSmallIcon(R.drawable.ic_notification) //Language icon by https://icons8.com
                //.setContentIntent(activityPendingIntent)
                .build()
        startForeground(1, notification)
        //}
        return START_NOT_STICKY
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

    override fun onBind(intent: Intent): IBinder? = null
}