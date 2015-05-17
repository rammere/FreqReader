package rm.mere.FreqReader;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.lang.Class;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONArray;

interface ITelephony {

boolean endCall();

//void answerRingingCall();

//void silenceRinger();

}


public class FreqReader extends CordovaPlugin {

    CallStateListener listener;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if( "start".equals(action))
            {
              this.start();
            }
         else  if( "stop".equals(action))
            {
            }

        return true;
    }
    private void start() {
      Context context=this.cordova.getActivity().getApplicationContext();
      AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Title");
            builder.setMessage("Is this a question?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
               public void onClick(DialogInterface dialog, int which) {
                // Code that is executed when clicking YES

                 dialog.dismiss();
                    }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                    public void onClick(DialogInterface dialog, int which) {
                // Code that is executed when clicking NO
                        dialog.dismiss();
                    }

            });

            AlertDialog alert = builder.create();
            alert.show();
}


    private void prepareListener() {
        if (listener == null) {
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
       private void EndCall() {

           // TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            try{
               Class clazz = Class.forName(TelephonyMgr.getClass().getName());

               Method method = clazz.getDeclaredMethod("getITelephony");
               method.setAccessible(true);

              ITelephony telephonyService = (ITelephony) method.invoke(TelephonyMgr);
              telephonyService.endCall();
            } catch ( ClassNotFoundException e ) {

            } catch ( NoSuchMethodException e ) {

            } catch ( IllegalAccessException e ) {


            }catch ( InvocationTargetException e ) {}



    }
}

class CallStateListener extends PhoneStateListener {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext)
    {


        this.callbackContext = callbackContext;


    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        if (callbackContext == null) return;

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            msg = "IDLE";
            break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
            msg = "OFFHOOK";
            break;

            case TelephonyManager.CALL_STATE_RINGING:
            msg = "RINGING";
            break;
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, msg+":"+incomingNumber);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }
}
