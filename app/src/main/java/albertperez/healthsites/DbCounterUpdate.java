package albertperez.healthsites;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class DbCounterUpdate  extends AsyncTask <String, Integer, Void>  {

    private final static String TAG = "DbCounterUpdate async";
    private final static String stadistics = "Stadistics";
    private AsyncResponse asyncResponse = null;

    public interface AsyncResponse{
        void processFinished(Boolean output);
    }

    public DbCounterUpdate(){

    }

    public DbCounterUpdate(AsyncResponse asyncResponse){
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.i(TAG,"doInBackground");
        String count = params[0];
        int type = Integer.parseInt(params[1]);
        int j = type;
        onUserCountUpdate(count, j);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(asyncResponse != null) {
            asyncResponse.processFinished(true);
        }
    }

    private void onUserCountUpdate(String count, final int type){
        Log.i("onUserREGISTERED", "postTransaction:onComplete:");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(stadistics).child(count);
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.i("Set UsersCount", "postTransaction:onComplete:");
                if(mutableData.getValue(Integer.class) == null){
                    return Transaction.success(mutableData);
                }

                int i;
                if(type >= 0){
                    i =  mutableData.getValue(Integer.class);
                    i += 1;
                    mutableData.setValue(i);
                }else{
                    i =  mutableData.getValue(Integer.class);
                    i -= 1;
                    mutableData.setValue(i);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.i("Set UsersCount", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
