package co.ilumi.ilumiexample.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ilumi.sdk.IlumiSDK;
import com.ilumi.sdk.callbacks.IsSuccessCallBack;

import java.util.List;
import java.util.Random;

import co.ilumi.ilumiexample.R;

/**
 * Created by mxmerce on 4/16/16.
 */
public class UnpairedBulbAdapter extends ArrayAdapter<byte[]>{

    private static final String TAG = "UnpairedBulbAdapter";
    private List<byte[]> items = null;

    static private char[] charArray = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E' , 'F'
    };

    private static char NibbleToChar(int data) {
        return charArray[data & 0xF];
    }

    // Format raw mac address bytes to a human readable form.
    public static String bytesToString(byte[] mac) {

        if(mac != null && mac.length > 0) {

            char[] result = new char[(mac.length*3)-1];
            int temp;

            for(int i = 0; i < mac.length; i++) {

                temp = mac[mac.length-1-i] & 0x0F;
                result[i*3+1] = NibbleToChar(temp);

                temp = mac[mac.length-1-i] >> 4;
                result[i*3] = NibbleToChar(temp);

                if( (i+1) < mac.length ) {
                    result[i*3+2] = ':';
                }
            }

            return new String(result);
        }else{
            return "null";
        }
    }

    public UnpairedBulbAdapter(Context context, List<byte[]> items) {
        super(context, 0, items);
        this.items = items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public byte[] getItem(int position) {
        // TODO Auto-generated method stub
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        String bulbLabel = bytesToString(getItem(position));


        if (bulbLabel != null) {
            final TextView label = (TextView) v.findViewById(R.id.bulbLabel);
            Button setBulbColorButton = (Button) v.findViewById(R.id.setBulbColorButton);
            setBulbColorButton.setVisibility(View.GONE);

            if (label != null) {
                label.setText(bulbLabel);
            }

            setBulbColorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pairIlumi(getItem(position));
                }
            });

        }

        return v;
    }

    private void pairIlumi(byte[] macAddress)
    {
        Random rand = new Random();
        int r = rand.nextInt();

        int groupID = r % 99999; //Can be generated randomly for this sample
        r = rand.nextInt();
        int nodeID = r % 99999; //Can be generated randomly for this sample


        IlumiSDK.sharedManager().commissionWithId(macAddress, groupID, nodeID, new IsSuccessCallBack() {
            @Override
            public void run(boolean success) {
                if(success) {
                    Log.d(TAG, "bulb commission completed with success.");
                } else {
                    Log.d(TAG, "bulb commission failed.");
                }
            }
        });


    }

}
