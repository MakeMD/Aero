package com.chemart.aero;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;












import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
//import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements OnPreparedListener,
    OnCompletionListener, OnClickListener, OnTouchListener, OnBufferingUpdateListener, OnSeekBarChangeListener, Runnable{
  private int mediaFileLengthInMilliseconds; 
  final int MENU_QUIT_ID = 2;
  private static final int NOTIFY_ID = 1;	
  final String LOG_TAG = "myLogs";
  final String DATA_HTTP = "http://aquarium.lipetsk.ru/MESTA/mp3/Aerostat/Aerostat_vol_";
  final String DATA_SD = Environment.getExternalStorageDirectory() + "/Aero";
  final String FILENAME_SD = "last";
  final String DIR_SD = "Aero";
  MediaPlayer mediaPlayer;
  AudioManager am;
  TextView Tv;
  String match;
  EditText edit;
  String boris;
TextView resp;
  ProgressDialog pd;
  Handler handler;
  //private final Handler seekHandler = new Handler();
  SeekBar seek_bar;
  Button play_button_on, play_button_off, pause_button, stop_button;
  TextView current_pos, duration, last_track, last_sd;
  StringBuilder text;
  int last = 439;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
   // setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    am = (AudioManager) getSystemService(AUDIO_SERVICE);
    AudioControl();
    edit = (EditText) findViewById(R.id.editText1);
    edit.setClickable(true);
    edit.setEnabled(true);
    writeFileSD();
    
    // int x = 438;
    try {
		getResponseCode("http://aquarium.lipetsk.ru/MESTA/mp3/Aerostat/Aerostat_vol_"+Integer.toString(last)+"/");
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    resp = (TextView) findViewById(R.id.textView5);
    last_track = (TextView) findViewById(R.id.textView4);
    
    
    if (!Environment.getExternalStorageState().equals(
	        Environment.MEDIA_MOUNTED)) {
	      Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
	      return;
	    }
    
    last_sd = (TextView) findViewById(R.id.textView6);
	    // получаем путь к SD
	    File sdPath = Environment.getExternalStorageDirectory();
	    // добавляем свой каталог к пути
	    sdPath = new File(sdPath.getAbsolutePath()  + "/" + DIR_SD);
	    // формируем объект File, который содержит путь к файлу
	    File sdFile = new File(sdPath, FILENAME_SD);
	    
	    try {
	      // открываем поток для чтения
	      BufferedReader br = new BufferedReader(new FileReader(sdFile));
	 text = new StringBuilder();
	      String str = "";
	      // читаем содержимое
	      while ((str = br.readLine()) != null) {
	          text.append(str);
	    	  Log.d(LOG_TAG, str);
	      }
	      br.close();
	      last_sd.setText(text); 
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
    
    
    if(last_sd.getText() != Integer.toString(last) ){
    
    if ( resp.getText() != "not valid")
	{
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений 
    int icon = R.drawable.ic_launcher; // Иконка для уведомления, я решил воспользоваться стандартной иконкой для Email
    CharSequence tickerText = "New series ready!"; 
    long when = System.currentTimeMillis(); // Выясним системное время
    Notification notification = new Notification(icon, tickerText, when); // Создаем экземпляр уведомления, и передаем ему наши параметры
    Context context = getApplicationContext(); 
    CharSequence contentTitle = "Aerostat"; // Текст заголовка уведомления при развернутой строке статуса
    CharSequence contentText = "New series ready!"; //Текст под заголовком уведомления при развернутой строке статуса
    Intent notificationIntent = new Intent(this, MainActivity.class); // Создаем экземпляр Intent
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); 
    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); // Передаем в наше уведомление параметры вида при развернутой строке состоянияs
    mNotificationManager.notify(NOTIFY_ID, notification); // И наконец показываем наше уведомление через менеджер передав его ID 
	}
  }
  }

  private void writeFileSD() {
	  // проверяем доступность SD
	    //if (!Environment.getExternalStorageState().equals(
	      //  Environment.MEDIA_MOUNTED)) {
	      //Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
	      //return;
	    //}
	    // получаем путь к SD
	    File sdPath_wr = Environment.getExternalStorageDirectory();
	    // добавляем свой каталог к пути
	    sdPath_wr = new File(sdPath_wr.getAbsolutePath()   + "/" + DIR_SD);
	    // создаем каталог
	    sdPath_wr.mkdirs();
	    // формируем объект File, который содержит путь к файлу
	    File sdFile_wr = new File(sdPath_wr, FILENAME_SD);
	    try {
	      // открываем поток для записи
	      BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile_wr));
	      // пишем данные
	      bw.write(Integer.toString(last));
	      bw.flush();
	      // закрываем поток
	      bw.close();
	      Log.d(LOG_TAG, "Файл записан на SD: " + sdFile_wr.getAbsolutePath());
	    } catch (IOException e) {
	      e.printStackTrace();
	  }
  }
  
  public  void getResponseCode(String urlString) throws MalformedURLException, IOException {
	  resp = (TextView) findViewById(R.id.textView5);
	  last_track = (TextView) findViewById(R.id.textView4);
	  
	  URL url = new URL ("http://aquarium.lipetsk.ru/MESTA/mp3/Aerostat/Aerostat_vol_"+Integer.toString(last)+"/");
	  HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  con.setRequestMethod("GET");
	  con.connect () ; 
	  int code = con.getResponseCode() ;
	  if (code == HttpURLConnection.HTTP_NOT_FOUND)
	  {
	  resp.setText("not valid");
	  }
	  else {
		  resp.setText("valid");
		  last_track.setText(Integer.toString(last));
		  last++;
		  writeFileSD();
	  }
  }
  
  
  /** This method initialize all the views in project*/
  public void AudioControl(){
	  seek_bar = (SeekBar) findViewById(R.id.seekBar1);
	  seek_bar.setProgress(0);
	  seek_bar.setMax(99);
	   seek_bar.setOnSeekBarChangeListener(this);
  }
  public void run() {
	  
      int currentPosition= 0;
      int total = mediaPlayer.getDuration();
      while (mediaPlayer!=null && currentPosition<total) {
          try {
        	  Thread.sleep(1000);
              currentPosition= mediaPlayer.getCurrentPosition();
            
          } catch (InterruptedException e) {
              return;
          } catch (Exception e) {
              return;
          }            
          seek_bar.setProgress(currentPosition);
          //current_pos.setText(currentPosition);
      }
  }     
    public void onClickStart(View view) {
    releaseMP();
    try {
      switch (view.getId()) {
      case R.id.btnStartHttp:
    	  edit = (EditText) findViewById(R.id.editText1);
    	    match = edit.getText().toString();
    	    boris = match+"/Boris%20Grebenshchikov%20-%20Aerostat%20Radio%20vol."+match+".mp3";
    		Log.d(LOG_TAG, "start HTTP");
    		edit.setClickable(false);
            edit.setEnabled(false);
            Tv = (TextView) findViewById(R.id.textView1);
            Tv.setText("Aerostat № "+match);
            seek_bar.setVisibility(View.VISIBLE);
            seek_bar.setProgress(0);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(DATA_HTTP+boris);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d(LOG_TAG, "prepareAsync");
        mediaPlayer.setOnPreparedListener((OnPreparedListener) this);
        mediaPlayer.prepareAsync();
       // mediaPlayer.start();
        
        new Thread(this).start();
        //seek_bar.setVisibility(View.INVISIBLE);
        break;
        case R.id.btnStartSD:
        	edit = (EditText) findViewById(R.id.editText1);
    	    match = edit.getText().toString();
    	    boris = match+"/Boris%20Grebenshchikov%20-%20Aerostat%20Radio%20vol."+match+".mp3";
    	    Tv = (TextView) findViewById(R.id.textView1);
  	 	  Tv.setText(DATA_HTTP+boris);
        	downloadFile(DATA_HTTP+boris);
        	Log.d(LOG_TAG, "start SD");
        break;
        case R.id.button1:
        	edit = (EditText) findViewById(R.id.editText1);
    	    match = edit.getText().toString();
    	    boris = match+"/Boris%20Grebenshchikov%20-%20Aerostat%20Radio%20vol."+match+".mp3";
    	    java.io.File file = new java.io.File(DATA_SD+"/", match+".mp3");
        	if(file.exists())
        	{
        	mediaPlayer = new MediaPlayer();
        	mediaPlayer.setDataSource(DATA_SD+"/"+match+".mp3");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            edit.setClickable(false);
            edit.setEnabled(false);
            Tv = (TextView) findViewById(R.id.textView1);
            Tv.setText("Aerostat № "+match);
            seek_bar.setVisibility(View.VISIBLE);
            seek_bar.setProgress(0);
            seek_bar.setMax(mediaPlayer.getDuration());
            new Thread(this).start();
        	}
            else{ 
            	Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        	            break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (mediaPlayer == null)
      return;
    mediaPlayer.setOnCompletionListener((OnCompletionListener) this);
    duration = (TextView) findViewById(R.id.textView3);
    duration.setText(""+milliSecondsToTimer(seek_bar.getMax()));
    current_pos = (TextView) findViewById(R.id.textView2);
    current_pos.setText(""+milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

  }
    public String milliSecondsToTimer(int milliseconds){
        String finalTimerString = "";
        String secondsString = "";
 
        // Convert total duration into time
           int hours = (int)( milliseconds / (1000*60*60));
           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours + ":";
           }
 
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10){
               secondsString = "0" + seconds;
           }else{
               secondsString = "" + seconds;}
 
           finalTimerString = finalTimerString + minutes + ":" + secondsString;
 
        // return timer string
        return finalTimerString;
    }
    
    private void downloadFile(String url) {
    	new AsyncTask<String, Integer, File>() {
    	final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
    	private Exception m_error = null;
        @Override
        protected void onPreExecute() {
        	progressDialog.setMessage("Downloading ...");
            progressDialog.setCanceledOnTouchOutside(false);
        	progressDialog.setCancelable(true);
        	progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        	Toast.makeText(MainActivity.this, "Start download", Toast.LENGTH_SHORT).show();
        }
    @Override
        protected File doInBackground(String... params) {
        	URL url;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            int totalSize;
            int downloadedSize;
            byte[] buffer;
            int bufferLength;
            File file = null;
            FileOutputStream fos = null;
            try {
             url = new URL(params[0]);
             urlConnection = (HttpURLConnection) url.openConnection();
             urlConnection.setRequestMethod("GET");
             urlConnection.setDoOutput(true);
             urlConnection.connect();
             //file = File.createTempFile("Mustachify", "download");
             fos = new FileOutputStream(DATA_SD+"/"+match+".mp3");
             inputStream = urlConnection.getInputStream();
             totalSize = urlConnection.getContentLength();
             downloadedSize = 0;
             buffer = new byte[1024];
             bufferLength = 0;
             // читаем со входа и пишем в выход, 
             // с каждой итерацией публикуем прогресс
             while ((bufferLength = inputStream.read(buffer)) > 0) {
              fos.write(buffer, 0, bufferLength);
              downloadedSize += bufferLength;
              publishProgress(downloadedSize, totalSize);
             }
             fos.close();
             inputStream.close();
             return file;
            } catch (MalformedURLException e) {
             e.printStackTrace();
             m_error = e;
            } catch (IOException e) {
             e.printStackTrace();
             m_error = e;
            }
            return null;
           }
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
           };          
           @Override
           protected void onPostExecute(File file) {
            // отображаем сообщение, если возникла ошибка
            if (m_error != null) {
             m_error.printStackTrace();
             return;
            }
            // закрываем прогресс и удаляем временный файл
            progressDialog.hide();
            Toast.makeText(MainActivity.this, "Download finished", Toast.LENGTH_LONG).show();
           }
    }.execute(url);
    }
  private void releaseMP() {
    if (mediaPlayer != null) {
      try {
    	  mediaPlayer.release();
        mediaPlayer = null;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  public void onClick(View view) {
    if (mediaPlayer == null)
      return;
    switch (view.getId()) {
    case R.id.btnPause:
      if (mediaPlayer.isPlaying())
        mediaPlayer.pause();
      break;
    case R.id.btnResume:
      if (!mediaPlayer.isPlaying())
        mediaPlayer.start();
      break;
    case R.id.btnStop:
     	mediaPlayer.stop();
    	seek_bar.setProgress(0);
    	seek_bar.setMax(0);
    	edit.setText("");
    	Tv.setText("Track");
    	edit.setClickable(true);
        edit.setEnabled(true);
        //current_pos = (TextView) findViewById(R.id.textView2);
        current_pos.setText(R.string.Current_pos);
        duration.setText(R.string.Duration);
      break;
    case R.id.btnBackward:
      mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
      seek_bar.setProgress(mediaPlayer.getCurrentPosition());
      //current_pos = (TextView) findViewById(R.id.textView2);
      current_pos.setText(""+milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
      break;
    case R.id.btnForward:
      mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
      seek_bar.setProgress(mediaPlayer.getCurrentPosition());
      //current_pos = (TextView) findViewById(R.id.textView2);
      current_pos.setText(""+milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
      break;
    }
  }
  public void onStartTrackingTouch(SeekBar seekBar) 
  	{
  	}
  public void onStopTrackingTouch(SeekBar seekBar) 
  	{
	
   
  	}
  public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser) 
  {
      if(fromUser) mediaPlayer.seekTo(progress);
      //current_pos = (TextView) findViewById(R.id.textView2);
      current_pos.setText(""+milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

  }
  @Override
  public boolean onTouch(View v, MotionEvent event) {
      if(v.getId() == R.id.seekBar1){
          /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
          if(mediaPlayer.isPlaying()){
              SeekBar sb = (SeekBar)v;
              int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
              mediaPlayer.seekTo(playPositionInMillisecconds);
              //current_pos = (TextView) findViewById(R.id.textView2);
              current_pos.setText(""+milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
          }
      }
      return false;
  }
  public void onPrepared(MediaPlayer mp) {
    Log.d(LOG_TAG, "onPrepared");
    
 
    mp.start();
  }
  public void onCompletion(MediaPlayer mp) {
    Log.d(LOG_TAG, "onCompletion");
       seek_bar.setMax(mediaPlayer.getDuration());
       //edit.setText("");
	//Tv.setText("Track");
	//edit.setClickable(true);
   // edit.setEnabled(true);
    //seek_bar.setVisibility(View.INVISIBLE);
  }
  public void onBufferingUpdate(MediaPlayer mp, int percent) {
      /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
      seek_bar.setSecondaryProgress(percent);
  }
  protected void onDestroy() {
    super.onDestroy();
    releaseMP();
  }
//создание меню
public boolean onCreateOptionsMenu(Menu menu) {
//TODO Auto-generated method stub
menu.add(0, MENU_QUIT_ID, 0, "Quit");
return super.onCreateOptionsMenu(menu);
}
//обработка нажатий на пункты меню
@SuppressWarnings("unused")
public boolean onOptionsItemSelected(MenuItem item) {
//TODO Auto-generated method stub
 MENU_QUIT_ID:
//выход из приложения
finish();
return super.onOptionsItemSelected(item);
	}
}
    

