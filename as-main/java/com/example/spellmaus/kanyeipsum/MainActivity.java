package com.example.spellmaus.kanyeipsum;
import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.content.ClipboardManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.locks.*;

import static java.lang.Integer.parseInt;



//generate button should hide kb

public class MainActivity extends AppCompatActivity {
    private static Lock lock;
    TextView e;
    //TextGenerator t;
    InputStream ip;
    BufferedReader br;
    BreakIterator iterator;

    ArrayList<String> sentences;
    ArrayList<Integer> paragraphs;
    ArrayList<Integer> usedIndex;

    int index;
    String source;

    private int numSentences;
    private int numPara;

    //TextSwitcher fields
    TextSwitcher ts;
    int sentOrPara=0;
    String textToShow[]={"Sentence #: ", "Paragraph #: "};

    //EditText number inputter fied
    EditText numInput;

    //Checkbox fields
    boolean pTags;
    boolean caps;
    boolean censor;

    boolean debug = false;

    String textField;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pTags = false;
        caps = false;

        numInput = (EditText)findViewById(R.id.numField);
        ts = (TextSwitcher) findViewById(R.id.paraOrSent);
        ts.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(16);
                myText.setTextColor(Color.RED);
                return myText;
            }
        });
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);

        // set the animation type of textSwitcher
        ts.setInAnimation(in);
        ts.setOutAnimation(out);
        ts.setCurrentText(textToShow[sentOrPara]); //initial is sentence #

        // ClickListener for NEXT button
        // When clicked on Button TextSwitcher will switch between texts
        // The current Text will go OUT and next text will come in with specified animation
        ts.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(sentOrPara==0){
                    sentOrPara=1;
                    ts.setText(textToShow[sentOrPara]); //show para #
                } else if(sentOrPara==1){
                    sentOrPara=0;
                    ts.setText(textToShow[sentOrPara]); //show sent #

                }
            }
        });

        lock = new ReentrantLock();
        e = (TextView) findViewById(R.id.scroll_content);

        Runnable readSpeech = new ReadSpeech();
        Thread threadS = new Thread(readSpeech);
        threadS.start();
        if(debug)
            Toast.makeText(getApplicationContext(),"readSpeech", Toast.LENGTH_SHORT).show();
        //reading in speech at startup

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        numInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //rightside floating action buttons
        FloatingActionButton clearFab = (FloatingActionButton) findViewById(R.id.fa2);
        clearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e.setText(""); //clears text field
                textField = "";

            }
        });

        FloatingActionButton copyFab = (FloatingActionButton) findViewById(R.id.fab);
        copyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Copied to Clipboard", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("textContent", textField);
                clipboard.setPrimaryClip(clip);

            }

        });

        FloatingActionButton downloadFab = (FloatingActionButton) findViewById(R.id.fa3);
        downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar s = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Save file to Downloads?", Snackbar.LENGTH_INDEFINITE);
//                s.setAction("SAVE", new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                       // storagePermitted(MainActivity.this);
//                        writeStorage(v);
//                    }
// 6.               });
//                s.setActionTextColor(Color.WHITE);
//                s.show();

                storagePermitted(MainActivity.this);
                writeStorage(view);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

//    public boolean fileExistance(String fname){
//        File file = getBaseContext().getFileStreamPath(fname);
//        return file.exists();
//    }

    private static boolean storagePermitted(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

            return true;

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        return false;

    }

    public void writeStorage(View v){
//        int count = 1;
//        String append = "";
//        String file_name = "kanye_ipsum";
//        while(fileExistance(file_name)){
//            count++;
//            append = "_" + count;
//        }
//        file_name = file_name + append;
//        //save multiple instances
//        Toast.makeText(getApplicationContext(),"Ipsum 1",Toast.LENGTH_SHORT).show();
//        String file_name = "_kanman.txt";
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        File f = new File(path, "KanyeIpsum.txt");
//        if (!f.mkdirs()) {
//            Log.e("File creation", "Directory not created");
//        }
//        Toast.makeText(getApplicationContext(),"Ipsum 2",Toast.LENGTH_SHORT).show();
//
//        try{
//            Toast.makeText(getApplicationContext(),"Ipsum 3",Toast.LENGTH_SHORT).show();
//            FileOutputStream fo = new FileOutputStream(f, false);
//            Toast.makeText(getApplicationContext(),"Ipsum 4",Toast.LENGTH_SHORT).show();
//*
//            fo.write(textField.getBytes());
//            Toast.makeText(getApplicationContext(),"Ipsum 5",Toast.LENGTH_SHORT).show();
//
//            fo.close();
//            Toast.makeText(getApplicationContext(),"Ipsum Saved",Toast.LENGTH_SHORT).show();
//        } catch (IOException e2){
//            e2.printStackTrace();
//        }

        //internal storage
        if(textField == null || textField.isEmpty()){
            return;
        }

//        FileOutputStream fo = null;
//        File file = new File("kanye_ipsum.txt");
//        try{
//            fo = openFileOutput("kanye_ipsum.txt", MODE_PRIVATE);
//            fo.write(textField.getBytes());
//            Log.i("writeToFile", "File created and written");
//            fo.close();
//        } catch (IOException e2){
//            e2.printStackTrace();
//        }

        //Saving to external storage
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "_kanye.txt");
        Uri contentUri = Uri.fromFile(file);
        try {
            Log.i("tryBlock", "Enter Try");
            FileOutputStream stream = new FileOutputStream(file, false);
            stream.write(textField.getBytes());
            stream.close();
            Log.i("saveData", "Data Saved");
            contentUri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.e("SAVE DATA", "Could not write file " + e.getMessage());
        }
        //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        //sendBroadcast(mediaScanIntent);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/msword");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(shareIntent, "Share via"));


        //downloading from the Internet
//        Uri c = Uri.parse("http://m.uploadedit.com/ba3s/1483548069740.txt");
//        DownloadManager.Request request = new DownloadManager.Request(c);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "_kanye.txt");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
//        request.allowScanningByMediaScanner();// if you want to be available from media players
//        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        manager.enqueue(request);


    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onCensorClicked(View v){ censor = true; }

    public void onCapsClicked(View v){
        caps = true;
    }

    public void onParaTagClicked(View v){
        pTags = true;
    }

    public void clickGen(View v){
        if(debug)
            Toast.makeText(getApplicationContext(),"Generator clicked", Toast.LENGTH_SHORT).show();

        hideKeyboard(v);
        String sub = numInput.getText().toString();
        String op = "";
        if(sub.isEmpty() || sub.length() == 0 || sub.equals("") || sub == null)
        {
            //EditText is empty
            Toast.makeText(getApplicationContext(),"Only numbers from 1-99", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //EditText is not empty
            int num = parseInt(sub);
            if(num == 0){
                Toast.makeText(getApplicationContext(),"Only numbers from 1-99", Toast.LENGTH_SHORT).show();
            }
            else {
                if (sentOrPara == 0) {
                    if (debug)
                        Toast.makeText(getApplicationContext(), "You have selected " + num + " sentences in one paragraph.", Toast.LENGTH_SHORT).show();

                    op = getSentences(num, caps, pTags);
                } else {
                    if (debug)
                        Toast.makeText(getApplicationContext(), "You have selected " + num + " paragraphs.", Toast.LENGTH_SHORT).show();
                    op = getParagraphs(num, caps, pTags);
                }
            }
        }

        if(censor){
            op = censor(op);
            if (debug)
                Toast.makeText(getApplicationContext(), "Censor.", Toast.LENGTH_SHORT).show();

        }


        textField = op;
        e.setText(op);
        // num caps ptags
//        String i = "\n\n3 sentences: " + getSentences(3, true, true);
//
//        e.setText("1 para: " + getParagraphs(4, false, true) + i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }



    public class ReadSpeech implements Runnable{
        @Override
        public void run(){
            lock.lock();
            try{
                iterator = BreakIterator.getSentenceInstance(Locale.US);
                sentences = new ArrayList<String>();
                paragraphs = new ArrayList<Integer>();
                usedIndex = new ArrayList<Integer>();
                index = 0;
                ip = getAssets().open("speech.txt");
                //Toast.makeText(getApplicationContext(), "read speech", Toast.LENGTH_SHORT).show();
                br = new BufferedReader(new InputStreamReader(ip, "UTF-8"));
                while((source = br.readLine()) != null){
                    //each paragraph
                    if(source.length() > 0 && !source.trim().startsWith("//")){ //ignore comments and empty lines
                        paragraphs.add(index); //we mark where the paragraphs begin
                        iterator.setText(source);
                        int start = iterator.first();
                        String s = "";
                        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
                            s = source.substring(start,end);
                            sentences.add(s.trim()); //trim whitespace off front and end
                            index++; //increment index to keep track of sentences
                        }
                        //load sentences up
                    }
                }
                numSentences = sentences.size();
                numPara = paragraphs.size();

            } catch (Exception e){
                System.out.println("bad speech read");
                e.printStackTrace();
            }
            finally{
                lock.unlock();
            }

        }
    }

    public String getSentences(int n , boolean caps, boolean pTags){
        String s = "";
        int index = 0;
        String op = "";

        for(int i = 0; i<n; i++){
            do{
                index = (int)(Math.random()*numSentences);
            }while(usedIndex.contains(index));
            //finds an unused index

            s+=sentences.get(index)+" ";
            usedIndex.add(index);
            if(usedIndex.size()==numSentences) //used up all sentences
            {
                usedIndex.clear();
                //giving the most flexibility with no repeats with large n
            }

        }

        usedIndex.clear(); //clear at the end

        if(caps){
            op = (s.trim()).toUpperCase();
        }

        else{
            op = s.trim();
        }

        if(pTags){
            return "<p>" + op + "</p>";
        }
        else{
            return op;
        }
    }

    public String getParagraphs(int n, boolean caps, boolean pTags){
        String s = "";
        int index = 0;
        int nextIndex = 0;
        int rand = 0;

        for(int i = 0; i<n; i++){
            do{
                rand = (int)(Math.random()*numPara);
                index = paragraphs.get(rand);
            }while(usedIndex.contains(index));
            //finds an unused index

            usedIndex.add(index);
            int a = rand+1;
            if(a==paragraphs.size()) //dealing with out of bounds
            {
                nextIndex = sentences.size();
            }
            else{
                nextIndex = paragraphs.get(a); //sentences.get(nextIndex-1) is the last sentence of cur paragraph
            }
            if(pTags)
                s+="<p>";
            for(int j = index; j<nextIndex; j++){
                if(caps){
                    s+=sentences.get(j).toUpperCase()+" ";
                }
                else{
                    s+=sentences.get(j)+" ";

                }
            }
            s=s.trim();
            if(pTags)
                s+="</p>";
            s+="\n\n";

            if(usedIndex.size()==numPara) //used up all paragraphs
            {
                usedIndex.clear();
                //giving the most flexibility with no repeats with large n
            }

        }

        usedIndex.clear(); //clear at the end

        return s.trim();



    }

    public static String censor(String s){ //Strings are immutable
        String m = s;
        String origWord = "";
        String[] sb = m.split("\\s+");
        for(String l : sb){
            origWord = l; //capitalization safe
            l = l.toLowerCase();

            if(l.startsWith("fuc")||l.startsWith("nigg")||l.startsWith("bitc")||l.startsWith("shit")){
                //any non-word characters at the beginning or start would affect the word boundary replacing whole words
                if(!Character.isLetter(l.charAt(l.length()-1))){
                    origWord = origWord.substring(0, origWord.length()-1);
                }
                else if(!Character.isLetter(l.charAt(0))){
                    origWord = origWord.substring(1, origWord.length());
                }

                m = m.replaceAll("\\b"+origWord+"\\b", "¡UH!"); //word boundary
            }
        }
        return m;
    }

//
//    //Thread skeleton
//    public class re implements Runnable{
//        @Override
//        public void run(){
//            lock.lock();
//            try{
//
//            } catch(Exception e){
//
//            }
//            finally{
//                lock.unlock();
//            }
//        }
//    }
}
