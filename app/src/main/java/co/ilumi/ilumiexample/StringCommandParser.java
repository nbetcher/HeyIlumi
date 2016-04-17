package co.ilumi.ilumiexample;

import android.util.Log;

import com.ilumi.sdk.IlumiSDK;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by ruby_ on 4/16/16.
 */
public class StringCommandParser {

    HashSet <String> inputCommandSet;

    public StringCommandParser(String pCommandString) {
        String[] commandsLine = pCommandString.split(" ");
        inputCommandSet = new HashSet<String>(Arrays.asList(commandsLine));
    }

    public void parseCommandsOnDevice(byte[] pMacAddress) {
        if(inputCommandSet.contains(Util.COMMAND_KEYWORD_OFF))
            IlumiSDK.sharedManager().turnOff(pMacAddress);

        if(inputCommandSet.contains(Util.COMMAND_KEYWORD_ON)){
            Log.i("TEST", "TEST COMMAND IS" + inputCommandSet.toString());
            IlumiSDK.sharedManager().turnOn(pMacAddress);}
    }
}
