package com.example.n_u.officebotapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.VIBRATOR_SERVICE;

public final class AppLog {
    public static final int FILE_LENGTH = 3000000;
    public static final int AUDIO_LENGTH = 300000;
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String ACTION = "Action";
    private static final String APP_TAG = "OfficeBot";
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String ASPX_DOCUMENT = ".aspx";
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String AUDIO_DOWNLOADED = "Audio Downloaded Press Again to Listen!";
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String AUTHENTICATING = "Authenticating..";
    // --Commented out by Inspection (14-May-17 10:47 AM):public static final String BMP_PICTURE = ".bmp";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String BUTTON = "button";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String CASE_TAG = "CASE_TAG";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String CHECKED_LIST = "checkedList: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String DESCRIPTION = "Description";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String DOCUMENT = ".doc";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String DOCX_DOCUMENT = ".docx";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String DOWNLOADED = "File Downloaded Press Again to See!";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String EDIT_SUCCESSFULLY = "Edit Successfully";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String EMAIL = "email";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ERROR = "Error";
    public static final OnErrorListener ERROR_LISTENER = new OnErrorListener() {
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String GET_TAG_MESSAGES = "get/tag/messages";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String GET_TAG_RECEIVED_MESSAGES = "get/tag/received/messages";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String GIF_PICTURE = ".gif";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String HANDLE_SIGN_IN_RESULT = "handleSignInResult:";
    public static final OnInfoListener INFO_LISTENER = new OnInfoListener() {
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };
    public static final String INTERNET_NOT_AVAILABLE = "Internet Not Available";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String JPEG_PICTURE = ".jpeg";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String JPG_PICTURE = ".jpg";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String LIST_REFRESHED = "List Refreshed!";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String MP3_AUDIO = ".mp3";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String MP4_AV = ".mp4";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String NET_NOT_AVAILABLE = "Net Not Available";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String NOTHING_IS_SET = "Nothing is set! ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_ACTIVITY_RESULT = "onActivityResult: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_BIND_VIEW_HOLDER = "onBindViewHolder: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_CLICK = "onClick: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_ITEM_LONG_CLICK = "onItemLongClick: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_RESPONSE = "onResponse: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String ON_SELECTED_FILE_PATHS = "onSelectedFilePaths: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String OPTIONS_FOR = "Options for ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String PATE_PRESENTATIONS = ".pptx";
    public static final String PATH_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecorder" + "/";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String PAT_PRESENTATION = ".ppt";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String PDF_DOCUMENT = ".pdf";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String PLEASE_HOLD = "Please Hold more then a Second to record";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String PNG_PICTURE = ".png";
    public static final int RC_SIGN_IN = 9001;
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String RECORDING = "Recording..";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String REPLY_SEND = "Reply Send";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String REQUEST_SEND_SUCCESSFULLY = "Request Send Successfully";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String REQUEST_SEND_UN_SUCCESSFULLY = "Request Send UnSuccessfully";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final int RESPONSE_CODE = 200;
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String SECTION_NUMBER = "section_number";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String SEND_MSG_LOG = "SEND_MSG_LOG";
    public static final String SERVER_LOG = "Server Offline";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String SIGNING_IN = "Signing In..";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final int SIZE_TEXT = 20;
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String STOP_RECORDING = "stopRecording: ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String STRING_DELETE = "Your have no right to delete this message";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String TAG_ADAPT = "TAGAdapt";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String TITLE = "Title";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String XLSX_SHEET = ".xlsx";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String XLS_SHEET = ".xls";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String YOU_CLICKED_YES = "You clicked yes ";
    // --Commented out by Inspection (14-May-17 10:48 AM):public static final String YOU_WANTED_TO_DELETE_THIS = "You wanted to delete this?";
    public static final int INTENSITY_LOW = 30;
    public static final int INTENSITY_MIDDLE = 40;
    public static final int INTENSITY_HIGH = 60;
    // --Commented out by Inspection (14-May-17 10:48 AM):private static final String FILE_TEMP_NAME = "temp";
    private static final PrettyTime p = new PrettyTime();
    private static Vibrator mVibrate;

    public static void setVibrate(Context context, int intensity) {
//        mVibrate=new
        mVibrate = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (mVibrate.hasVibrator()) {
            mVibrate.vibrate(intensity);
        }
    }

    public static void offVibrate() {
        mVibrate.cancel();
    }

    public static void defaultFileOpener(String s, Context context) {
        File localFile = new File(s);
        StringBuilder string = new StringBuilder(s);
        String str = string.substring(string.indexOf(".") + 1);
        Log.e("TAG", "onClick: " + localFile);
        MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
        Intent i = new Intent("android.intent.action.VIEW");
        str = localMimeTypeMap.getMimeTypeFromExtension(str);
        i.setDataAndType(Uri.fromFile(localFile), str);
//        i.setFlags(Intent.FLAG_);
        try {
            context.startActivity(Intent.createChooser(i, "Choose"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context.getApplicationContext()
                    , "No handler for this type of file." + e.getMessage()
                    , Toast.LENGTH_LONG).show();
        }
    }

    public static String getDateAndTime(long l) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(l));
    }

    public static String getPretty(String s, Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            return p.format(formatter.parse(s));
        } catch (ParseException e) {
            AppLog.logString(e.getMessage());
            AppLog.toastShortString(e.getMessage(), context);
        }
        return null;
    }

    public static String getDateTimeNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String getFilename() {
        Object object = Environment.getExternalStorageDirectory().getPath();
        logString((String) object);
        object = new File((String) object, "AudioRecorder");
        if (!((File) object).exists()) {
            ((File) object).mkdirs();
        }
        return ((File) object).getAbsolutePath() + "/" + "temp" + ".mp3";
    }

    public static String getFileTempName() {
        Object object = Environment.getExternalStorageDirectory().getPath();
        logString((String) object);
        object = new File((String) object, "AudioTempRecorder");
        if (!((File) object).exists()) {
            ((File) object).mkdirs();
        }
        String st = UUID.randomUUID().toString();
        return ((File) object).getAbsolutePath() + "/" + st + ".mp3";
    }

    public static boolean isFilePresent(String s) {
        File f = new File(PATH_FILE + s);
        logString(f.getAbsolutePath());
        return f.exists();
    }

    public static int logString(String s) {
        return Log.e(APP_TAG, s);
    }

    public static void toastString(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void toastShortString(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static int logString(int code) {
        return Log.e(APP_TAG, String.valueOf(code));
    }
}
