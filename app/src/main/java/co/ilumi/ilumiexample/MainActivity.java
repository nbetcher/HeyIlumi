package co.ilumi.ilumiexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.ilumi.sdk.IlumiSDK;
import com.ilumi.sdk.IlumiSDKDelegate;
import com.ilumi.sdk.callbacks.IsSuccessCallBack;

import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

import co.ilumi.ilumiexample.adapters.PairedBulbAdapter;
import co.ilumi.ilumiexample.adapters.UnpairedBulbAdapter;

public class MainActivity extends AppCompatActivity implements IlumiSDKDelegate {

    private static PairedBulbAdapter mPairedBulbAdapter = null;
    private static UnpairedBulbAdapter mUnpairedBulbAdapter = null;
    private static final String TAG = "MainActivity";

    private ArrayList<byte[]> paired = new ArrayList<byte[]>();
    private ArrayList<byte[]> unpaired = new ArrayList<byte[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mPairedBulbAdapter = new PairedBulbAdapter(this, this.paired);
        mUnpairedBulbAdapter = new UnpairedBulbAdapter(this, this.unpaired);

        ListView lvUnPaired = (ListView) findViewById(R.id.listView2);
        lvUnPaired.setAdapter(mUnpairedBulbAdapter);

        ListView lvPaired = (ListView) findViewById(R.id.listView);
        lvPaired.setAdapter(mPairedBulbAdapter);

        //Load network key, or create a new random one if one does not exist

        SharedPreferences shared = this.getSharedPreferences("ilumiPrefs", this.MODE_PRIVATE);
        int networkKey = shared.getInt("ilumi_network_key", 0);

        if(networkKey == 0) {
            Random rand = new Random();
            int r = rand.nextInt();

            //Generate new network key
            networkKey = r % 99999999;

            //Save to local cache
            SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("ilumiPrefs", networkKey);
            editor.commit();

        }

        IlumiSDK.sharedManager().setNetworkKey(networkKey);

        IlumiSDK.sharedManager().setDelegate(this);
        IlumiSDK.sharedManager().retrieveIlumis();
    }


    @Override
    public void didFindiLumi(final byte[] macAddress, final boolean isCommissioned, final int iLumiType) {

        if (!isCommissioned) {
            runOnUiThread(new Runnable() {

                public void run() {

                    if (unpaired.contains(macAddress)) {
                    } else {
                        Log.i(TAG, "Found new ilumi!" + macAddress.toString() + " added it to the list.");
                        if (!paired.contains(macAddress)) {
                            unpaired.add(macAddress);
                            IlumiSDK.sharedManager().connectIlumi(macAddress);
                        }

                        mUnpairedBulbAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void didDisconnectediLumi(final byte[] macAddress) {
    }

    @Override
    public void didConnectediLumi(final byte[] macAddress) {
        runOnUiThread(new Runnable() {

            public void run() {

                if(unpaired.contains(macAddress)) {
                    Log.i(TAG, "Connected ilumi removed from unpaired list.");
                    unpaired.remove(macAddress);
                    mUnpairedBulbAdapter.remove(macAddress);
                    mUnpairedBulbAdapter.notifyDataSetChanged();
                    if(paired.contains(macAddress)) {
                        Log.i(TAG, "Found ilumi, but it's already in the paired list.");
                    } else {
                        Log.i(TAG, "Found new ilumi! Added it to the paired list.");
                        paired.add(macAddress);
                        mPairedBulbAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void bluetoothNotEnabled() {
        android.widget.Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPairedBulbAdapter.onActivityResult(requestCode, resultCode, data);
    }
}
