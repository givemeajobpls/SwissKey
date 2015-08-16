package maxirozay.com.swisskey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import android.database.Cursor;
import android.provider.Telephony;

/**
 * Created by Leto on 14/08/2015.
 */
public class LoadDict {
    private String filePath = "";
    private Context mContext;
    //Attention, peut etre utiliser une Treemap, c'est deja trié et je crois
    //qu'on doit avoir la liste triée tout le temps
    //comme sa, quand on commence a ecrire un mot, c'est facile de 'split' la liste
    ///d'ailleur on devrait peut etre avoir plusieurs listes, avec tout les mots qui commence pareil par liste
    private HashMap<String,Double> DicoFR=new HashMap<String,Double>();

    public LoadDict(Context mContext){
        //Pour utiliser l'AssetManager, il faut un context, qu'on ne peut avoir que dans une activity,
        //voila pourquoi je le passe en paramettre
        this.mContext=mContext;
    }
    public void LoadFromFile(String fileName) {
        //Je met les fichier dans le dossier assets que j'ai fait via un clique drois depuis android studio,
        //faut y mettre nos fichier et on y accede via l'assetManager
        AssetManager mngr = this.mContext.getAssets();
        String line = null;
        int cpt=0;
        try{

            BufferedReader in = new BufferedReader(new InputStreamReader(mngr.open(fileName),"UTF-8"));
            while ((line = in.readLine()) != null) {
                String[] splited = line.split("\\s+");
                DicoFR.put(splited[0], Double.parseDouble(splited[1]));
                //Log.d("liste", line);

            }
            in.close();  //very important to close streams
        }catch (Exception e) {
            Log.d("exeption", "probleme de lecture de fichier !");
        }
    }


    //https://developer.android.com/reference/android/provider/Telephony.html
    //attention au warning, la j'utilise la methode officielle pour les sms, mais c'est recent
    //avant android 4.1, on passe par des content provider qui sont pas officiels et qui peuvent changer


    //ne pas oubleir qu'il y a un USER dictionnary directement integer a android, va peut etre
    //falloir l'utiliser aussi
    public List<String> getAllSmsFromProvider() {
        List<String> lstSms = new ArrayList<String>();
        ContentResolver cr = this.mContext.getContentResolver();
        final String[] projection = new String[] { Telephony.Sms._ID, Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.READ};
        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Sms.Inbox.BODY,Telephony.Sms.Inbox.BODY}, // Select body text
               // null,
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(c.getString(0));
                c.moveToNext();
            }
        } else {
            Log.d("exeption", "You have no SMS in Inbox");
            throw new RuntimeException("You have no SMS in Inbox");

        }
        c.close();

        return lstSms;
    }
    public List<SMSObject> readSMS() {
        List<SMSObject> lstSms = new ArrayList<SMSObject>();
        SMSObject objSms = new SMSObject();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr =  this.mContext.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        // this.startManagingCursor(c);
        int totalSMS = c.getCount();
        Log.d("SMS Count->", "" + totalSMS);
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new SMSObject();
                objSms.set_id(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.set_address(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.set_msg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.set_readState(c.getString(c.getColumnIndex("read")));
                objSms.set_time(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.set_folderName("inbox");
                } else {
                    objSms.set_folderName("sent");
                }

                lstSms.add(objSms);

                Log.d("SMS at " + i, objSms.toString());

                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

         return lstSms;

    }
}
