package albertperez.healthsites;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationToServer(token);
    }

    public static void sendRegistrationToServer(String token) {
        /*DatabaseReference fcmRef = FirebaseDatabase.getInstance().getReference("Tokens");
        final String key = fcmRef.push().getKey();
        assert key != null;
        fcmRef.child(key).setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Send Registration:", "success");
                } else {
                    Log.e("Send Registration:" , "failure");
                }
            }
        });*/

        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        ref.collection("Tokens").document(token);
    }

    public static void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.d(TAG, "Refreshed Token: "+ token);
                sendRegistrationToServer(token);
            }
        });
    }

    /*@Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: "+ remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: "+ remoteMessage.getData());
        }

        if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification body: "+ remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage.getNotification().getBody());
    }*/

    /*private void sendNotification(String body) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("FCM message")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }*/
}
