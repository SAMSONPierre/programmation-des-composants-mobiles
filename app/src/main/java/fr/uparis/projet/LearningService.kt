package fr.uparis.projet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@RequiresApi(Build.VERSION_CODES.M)
class LearningService : Service() {
    private val CHANNEL_ID="channel"
    private val notificationManager by lazy {getSystemService(NotificationManager::class.java)}

    override fun onCreate() {
        /** creation d'un seul notification channel puis regroupement par langue ensuite **/
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service", "start")
        val webPage=Uri.parse("https://fr.m.wikipedia.org/")
        val intentWiki=Intent(Intent.ACTION_VIEW, webPage)
        val pendingIntent=PendingIntent.getActivity(this, 1, intentWiki, PendingIntent.FLAG_IMMUTABLE)
        val notif=NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Session 1")
            .setContentText("Word of the day")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(R.drawable.small, "WIKI", pendingIntent)
            .build()
        startForeground(1, notif)

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

    override fun onBind(intent: Intent): IBinder {TODO()}
}