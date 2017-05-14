package com.example.n_u.officebotapp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.adapters.ReplyAdapter;
import com.example.n_u.officebotapp.helpers.Progress;
import com.example.n_u.officebotapp.intefaces.IReply;
import com.example.n_u.officebotapp.models.Message;
import com.example.n_u.officebotapp.models.Reply;
import com.example.n_u.officebotapp.models.SearchUser;
import com.example.n_u.officebotapp.models.Status;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.InternetConnection;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.example.n_u.officebotapp.viewsholders.ActivityReplyHolder;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyActivity
        extends AppCompatActivity
        implements OnTouchListener {
    private ReplyAdapter adapter = null;
    @Nullable
    private MultipartBody.Part bodyAudio = null, bodyFile = null;
    private Bundle bundle = null;
    private Context context = this;
    private String fileAudioSource = null;
    private Message msg = new Message();
    private MediaRecorder recorder = null;
    private List<Reply> replyArray = new ArrayList<>();
    private ActivityReplyHolder holder;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        View v = LayoutInflater.from(this).inflate(R.layout.activity_reply, null);
        holder = new ActivityReplyHolder(v);
        setContentView(v);
        setUi();
        adapter = new ReplyAdapter(this, replyArray);
        SwingRightInAnimationAdapter animationAdapter = new SwingRightInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(holder.getReplyList());
        holder.getReplyList().setAdapter(animationAdapter);
        ((TextView) findViewById(R.id.text_title_in_bar)).setText(R.string.tag_main_message_activity);
        setListener();
        data();
        holder.getReplyList().setEmptyView(holder.getEmptyViewTextReplyBelow());
    }

    private void sendMsg() {
        if (InternetConnection.checkConnection(context))
            if (holder.getReplyMsgEditText().getText().toString().isEmpty()) {
                AppLog.toastString(getString(R.string.empty_field), getApplicationContext());
            } else {
                HashMap<String, Object> map = new HashMap<>();
                (map).put(getString(R.string.content_key), holder.getReplyMsgEditText().getText().toString());
                (map).put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.id_key), getApplicationContext()));
                (map).put(getString(R.string.tag_id_key), getIntent().getStringExtra(getString(R.string.tag_id_key)));
                (map).put(getString(R.string.message_id_key), bundle.getString(getString(R.string.MSG_ID_BUNDLE)));
                Log.e("TAGmsg", "onClick: " + map.toString());
                final IReply iReply = OfficeBotURI.retrofit().create(IReply.class);
                retrofit2.Call<Status> call = iReply.sendReply(map);
                holder.getReplyMsgEditText().setText(null);
                call.enqueue(new Callback<Status>() {
                    public void onFailure(Call<Status> call, Throwable t) {
                        AppLog.toastString("Error" + t.toString(), getApplicationContext());
                        AppLog.logString(t.getMessage());
                    }

                    public void onResponse(Call<Status> call, Response<Status> response) {
                        AppLog.logString(response.code() + "");
                        AppLog.logString(response.toString());
                        if ((response.code() == 200) && (response.body().isReply())) {
                            Log.e("SEND_MSG_LOG", "onResponse: " + response.body().getReplyId());
                            if ((bodyFile != null) || (bodyAudio != null)) {
                                if ((bodyAudio != null) && (bodyFile == null)) {
                                    call = iReply.fileSend(bodyAudio
                                            , String.valueOf(response.body().getReplyId()));
                                } else if ((bodyAudio == null) && (bodyFile != null)) {
                                    call = iReply.fileSend(bodyFile
                                            , String.valueOf(response.body().getReplyId()));
                                } else {
                                    call = iReply.fileSend(bodyAudio
                                            , bodyFile
                                            , String.valueOf(response.body().getReplyId()));
                                }
                                bodyAudio = bodyFile = null;
                                call.enqueue(new Callback<Status>() {
                                    public void onFailure(Call<Status> call, Throwable t) {
                                        call.request();
                                        AppLog.toastString("Server Offline" + t.getMessage(), getApplicationContext());
                                    }

                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        if (response.code() == 200) {
                                            AppLog.toastString("Reply Send", getApplicationContext());
                                            holder.getReplyListRefreshList().performLongClick();

                                        } else
                                            AppLog.toastString("Server Offline" + response.code(), getApplicationContext());
                                    }
                                });
                            } else {
                                AppLog.toastString("Reply Send", getApplicationContext());
                                holder.getReplyListRefreshList().performLongClick();
                            }
                        } else {
                            AppLog.logString(response.code() + "");
                            AppLog.toastString("Server Problem try Again" + "\n" + response.code(),
                                    getApplicationContext());
                        }
                    }
                });
            }
        else AppLog.toastString(AppLog.NET_NOT_AVAILABLE, getApplicationContext());
    }

//    private void setDraw() {
//        Object localObject = new StringBuilder(this.msg.getData_src());
//        localObject = ((StringBuilder) localObject).substring(((StringBuilder) localObject).indexOf("."));
//        int i = -1;
//        switch (localObject.hashCode()) {
//        }
//        for (; ; ) {
//            switch (i) {
//                default:
//                    return;
//                if (localObject.equals(".jpg")) {
//                    i = 0;
//                    continue;
//                    if (localObject.equals(".mp3")) {
//                        i = 1;
//                        continue;
//                        if (localObject.equals(".mp4")) {
//                            i = 2;
//                            continue;
//                            if (localObject.equals(".pdf")) {
//                                i = 3;
//                                continue;
//                                if (localObject.equals(".doc")) {
//                                    i = 4;
//                                    continue;
//                                    if (localObject.equals(".docx")) {
//                                        i = 5;
//                                        continue;
//                                        if (localObject.equals(".png")) {
//                                            i = 6;
//                                            continue;
//                                            if (localObject.equals(".aspx")) {
//                                                i = 7;
//                                                continue;
//                                                if (localObject.equals(".jpeg")) {
//                                                    i = 8;
//                                                    continue;
//                                                    if (localObject.equals(".bmp")) {
//                                                        i = 9;
//                                                        continue;
//                                                        if (localObject.equals(".gif")) {
//                                                            i = 10;
//                                                            continue;
//                                                            if (localObject.equals(".xls")) {
//                                                                i = 11;
//                                                                continue;
//                                                                if (localObject.equals(".xlsx")) {
//                                                                    i = 12;
//                                                                    continue;
//                                                                    if (localObject.equals(".pptx")) {
//                                                                        i = 13;
//                                                                        continue;
//                                                                        if (localObject.equals(".ppt")) {
//                                                                            i = 14;
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                break;
//            }
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838663));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838663));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839478));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839478));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839620));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839620));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838671));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838671));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838680));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838680));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838680));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838680));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839217));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839217));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838671));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838671));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839217));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839217));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839217));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839217));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130839217));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130839217));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838658));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838658));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838658));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838658));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838673));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838673));
//        return;
//        if (Build.VERSION.SDK_INT >= 21) {
//            this.otherFile.setImageDrawable(getDrawable(2130838673));
//            return;
//        }
//        this.otherFile.setImageDrawable(getResources().getDrawable(2130838673));
//    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setMaxDuration(AppLog.AUDIO_LENGTH);
        recorder.setMaxFileSize(AppLog.FILE_LENGTH);
        MediaRecorder localMediaRecorder = recorder;
        String str = AppLog.getFilename();
        fileAudioSource = str;
        localMediaRecorder.setOutputFile(str);
        recorder.setOnErrorListener(AppLog.ERROR_LISTENER);
        recorder.setOnInfoListener(AppLog.INFO_LISTENER);
        try {
            recorder.prepare();
            recorder.start();
            holder.getFloatMsgOptions().performHapticFeedback(20);
        } catch (IllegalStateException | IOException localIllegalStateException) {
            AppLog.logString(localIllegalStateException.getMessage());
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            AppLog.logString("testing");
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            File localFile = new File(fileAudioSource);
            RequestBody localRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), localFile);
            bodyAudio = MultipartBody.Part.createFormData(getString(R.string.audio_key), localFile.getName(), localRequestBody);
            AppLog.logString(localFile.getAbsolutePath());
            AppLog.logString("testing" + fileAudioSource + localFile.getPath());
            holder.getReplyMsgEditText().setText("Check Audio Attached");
            sendMsg();
            AppLog.toastString("Audio Send", getApplicationContext());
            holder.getFloatMsgOptions().performHapticFeedback(20);
        }
    }

    public void attachFile(View view) {
        try {
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = new File(AppLog.PATH_FILE);
            properties.error_dir = new File(AppLog.PATH_FILE);
            properties.offset = new File(AppLog.PATH_FILE);
            properties.extensions = new String[]{".doc", ".docx", ".pdf", ".jpg", ".png", ".aspx"
                    , ".jpeg", ".mp3", ".mp4", ".bmp", ".gif", ".xls", ".xlsx", ".ppt", ".pptx"};
            FilePickerDialog views = new FilePickerDialog(this, properties);
            views.setTitle(getString(R.string.select_file));
            views.setDialogSelectionListener(new DialogSelectionListener() {
                public void onSelectedFilePaths(String[] arrayOfString) {
                    if (InternetConnection.checkConnection(context))
                        if (arrayOfString.length > 0) {
                            AppLog.logString("onSelectedFilePaths: " + Arrays.toString(arrayOfString));
                            File file = new File(arrayOfString[0]);
                            long fileSizeInBytes = file.length();
                            AppLog.logString(fileSizeInBytes + "");
                            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            AppLog.logString(fileSizeInKB + "");
                            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                            long fileSizeInMB = fileSizeInKB / 1024;
                            AppLog.logString(fileSizeInMB + "");
                            if (fileSizeInMB <= 2) {
                                RequestBody localRequestBody = RequestBody
                                        .create(MediaType.parse("multipart/form-data"), file);
                                bodyFile = MultipartBody.Part.createFormData(getString(R.string.data_key)
                                        , file.getName()
                                        , localRequestBody);
                                holder.getReplyMsgEditText().setText("Check File Attached");
                                sendMsg();
                                AppLog.toastString("File Send", getApplicationContext());
                                holder.getFloatMsgOptions().performHapticFeedback(20);
                            } else AppLog.toastString("File size is exceed only 2MB is applicable"
                                    , getApplicationContext());
                        } else AppLog.toastString("No File Selected", getApplicationContext());
                    else
                        AppLog.toastString(AppLog.NET_NOT_AVAILABLE, getApplicationContext());
                }
            });
            views.show();
        } catch (RuntimeException e) {
            AppLog.logString(e.getMessage());
            AppLog.toastString(e.getMessage(), getApplicationContext());
        }
    }

    void data() {
        IReply iReply = OfficeBotURI.retrofit().create(IReply.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.id_key), context));
        map.put(getString(R.string.message_id_key), bundle.getString(getString(R.string.MSG_ID_BUNDLE)));
        retrofit2.Call<Message> call = iReply.getDetailReplies(map);
        call.enqueue(new Callback<Message>() {
            public void onFailure(Call<Message> callback, Throwable t) {
                AppLog.toastString("Net Not Available" + t.getCause(), getApplicationContext());
                holder.getReplyListRefreshList().setRefreshing(false);
                AppLog.logString(t.getMessage());
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onResponse(Call<Message> callback
                    , Response<Message> response) {
                AppLog.logString(response.toString() + "\n" + response.code());
                if (response.code() == 200) {
                    if (!replyArray.isEmpty()) {
                        replyArray.clear();
                    }
                    msg = response.body();
//                    replyArray = msg.getReplies();
//                    adapter.clear();
//                    adapter.addAll(replyArray);
                    new Delete().from(Reply.class).where("message_id = ?", msg.getMsgId()).execute();
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Reply temp : response.body().getReplies()) {
                            Reply item = new Reply(temp);
                            item.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                    adapter.clear();
                    adapter.addAll(msg.getMsgId());
                    if ((msg.getAudio_src() != null) && (!msg.getAudio_src().isEmpty())) {
                        holder.getFrameAudio().setVisibility(View.VISIBLE);
                    }
                    if ((msg.getData_src() != null) && (!msg.getData_src().isEmpty())) {
                        holder.getFrameOther().setVisibility(View.VISIBLE);
//                        setDraw();
                    }
                    if (Objects.equals(bundle.getString(getString(R.string.MSG_OWNER_BUNDLE_KEY))
                            , OBSession.getPreference(getString(R.string.id_key), context))) {
                        holder.getMessageMainViewBy().setVisibility(View.VISIBLE);
                        holder.getMessageMainViewBy().setText(String.valueOf(msg.getSeen_by()));
                    }

                } else
                    Snackbar.make(holder.getReplyMsgEditText(), "Server Offline" + response.code()
                            , Snackbar.LENGTH_SHORT).show();
                holder.getReplyListRefreshList().setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.addAll(Integer.parseInt(bundle.getString(getString(R.string.MSG_ID_BUNDLE))));
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            View localView = getCurrentFocus();
            if ((localView instanceof EditText)) {
                Rect localRect = new Rect();
                localView.getGlobalVisibleRect(localRect);
                if (!localRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    localView.clearFocus();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(localView.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouch(View v, MotionEvent motionEvent) {
        int i = motionEvent.getAction();
//        contentEditText.setText("Check File Attached");
//        sendMsg();
////        AppLog.toastString("File Send", getApplicationContext());
        switch (i) {
            case MotionEvent.ACTION_DOWN:
                if (InternetConnection.checkConnection(context))
                    try {
                        Progress.Show(this.context, "Recording..");
                        startRecording();
                    } catch (IllegalStateException e) {
                        AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
                        AppLog.logString(e.getMessage());
                    } catch (RuntimeException e1) {
                        AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
                        AppLog.logString(e1.getMessage());
                    }
                else AppLog.toastShortString(AppLog.NET_NOT_AVAILABLE, getApplicationContext());
                break;
            case MotionEvent.ACTION_UP:
                try {
                    Progress.Cancel();
                    stopRecording();
                } catch (IllegalStateException e) {
                    AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
//                    AppLog.logString(e.getMessage());
                } catch (RuntimeException e1) {
                    AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
//                    AppLog.logString(e1.getMessage());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                try {
                    Progress.Cancel();
                    if (recorder != null) {
                        AppLog.logString("testingMove");
                        recorder.stop();
                        recorder.reset();
                        recorder.release();
                        recorder = null;
                        AppLog.toastShortString("Audio Cancel", getApplicationContext());
                    }
                } catch (IllegalStateException e) {
                    AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
//                    AppLog.logString(e.getMessage());
                } catch (RuntimeException e1) {
                    AppLog.toastShortString("Please Hold more then a Second to record", getApplicationContext());
//                    AppLog.logString(e1.getMessage());
                }
                break;
            default:
                break;

        }
        return false;
    }

    void setListener() {
        holder.getReplyListRefreshList().setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    data();
                } else {
                    AppLog.toastString("Net Not Available", getApplicationContext());
                    holder.getReplyListRefreshList().setRefreshing(false);
                }
            }
        });
        holder.getReplyListRefreshList().setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View paramAnonymousView) {
                holder.getReplyListRefreshList().setRefreshing(true);
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    data();
                    return false;
                } else {
                    AppLog.toastString("Net Not Available", getApplicationContext());
                    holder.getReplyListRefreshList().setRefreshing(false);
                }
                return false;
            }
        });
        holder.getMenuItemAudio().setOnTouchListener(this);
        holder.getAudioSourceIcon().setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Log.e("TAG", "onClick: " + AppLog.isFilePresent(msg.getAudio_src()));
                if (AppLog.isFilePresent(msg.getAudio_src())) {
                    AppLog.defaultFileOpener(AppLog.PATH_FILE + msg.getAudio_src(), context);

                } else {
                    if (InternetConnection.checkConnection(context)) {
                        new DownloadFileFromURL(true, false)
                                .execute(OfficeBotURI.getFileUrlPreFix() + msg.getAudio_src()
                                        , msg.getAudio_src());
                    } else {
                        AppLog.toastShortString(AppLog.NET_NOT_AVAILABLE
                                , context);
                    }
                }
            }
        });
        holder.getFileSourceIcon().setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Log.e("TAG", "onClick: " + AppLog.isFilePresent(msg.getData_src()));
                if (AppLog.isFilePresent(msg.getData_src())) {
                    AppLog.defaultFileOpener(AppLog.PATH_FILE + msg.getData_src(), context);

                } else {
                    if (InternetConnection.checkConnection(context)) {
                        new DownloadFileFromURL(false, true)
                                .execute(OfficeBotURI.getFileUrlPreFix() + msg.getData_src()
                                        , msg.getData_src());
                    } else {
                        AppLog.toastShortString(AppLog.NET_NOT_AVAILABLE
                                , context);
                    }
                }
            }
        });
        holder.getFloatMsgOptions().setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            public void onMenuToggle(boolean aBoolean) {
                if (aBoolean) {
                    holder.getMenuItemAudio().setVisibility(View.VISIBLE);
                    holder.getMenuItemFile().setVisibility(View.VISIBLE);
                    return;
                }
                holder.getMenuItemAudio().setVisibility(View.GONE);
                holder.getMenuItemFile().setVisibility(View.GONE);
            }
        });
        holder.getReplyMsgEditText().addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
                if (!paramAnonymousEditable.toString().isEmpty()) {
                    holder.getFloatMsgOptions().setVisibility(View.GONE);
                    holder.getSendMsgFab().setVisibility(View.VISIBLE);
                    holder.getSendMsgFab().setOnMenuButtonClickListener(new OnClickListener() {
                        public void onClick(View paramAnonymous2View) {
                            sendMsg();
                        }
                    });
                    return;
                }
                holder.getFloatMsgOptions().setVisibility(View.VISIBLE);
                holder.getSendMsgFab().setVisibility(View.GONE);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        holder.getRefresh().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getReplyListRefreshList().performLongClick();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void setUi() {
        bundle = getIntent().getBundleExtra(getString(R.string.MSG_KEY));
        holder.getAudioProgress().setVisibility(View.GONE);
        holder.getOtherFileProgress().setVisibility(View.GONE);
        if (Objects.equals(bundle.getString(getString(R.string.MSG_OWNER_BUNDLE_KEY)), OBSession.getPreference(getString(R.string.id_key), this))) {
            AppLog.logString("equal");
            holder.getMessageOwnerName().setText(OBSession.getPreference(getString(R.string.name_key), this));
        } else {
            holder.getMessageOwnerName().setText(String.valueOf(SearchUser.getNameUserOther(Integer.parseInt(bundle.getString(getString(R.string.MSG_OWNER_BUNDLE_KEY))))));
        }
        if (holder.getMessageOwnerName().getText().length() > 8) {
            holder.getMessageOwnerName().setTextSize(2, 10.0F);
        }
        holder.getMessageMainTime().setText(AppLog.getPretty(bundle.getString(getString(R.string.MSG_TIME_BUNDLE_KEY)), context));
        if (holder.getMessageMainTime().getText().length() > 8) {
            holder.getMessageMainTime().setTextSize(2, 8.0F);
        }
        holder.getMessageMainContent().setText(bundle.getString(getString(R.string.MSG_CONTENT_BUNDLE_KEY)));
        if (holder.getMessageMainContent().getText().length() > 15) {
            holder.getMessageMainContent().setTextSize(2, 12.0F);
        }
        holder.getBackBtnImg().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        holder.getMessageMainViewBy().setVisibility(View.GONE);
    }

    private class DownloadFileFromURL
            extends AsyncTask<String, String, String> {
        static final int BYTE_DATA_SIZE = 1024;
        static final int SIZE = 8192;
        boolean isAudio = false;
        boolean isOther = false;

        DownloadFileFromURL(boolean b, boolean b1) {
            isAudio = b;
            isOther = b1;
        }

        @Nullable
        protected String doInBackground(String... strings) {
            int count;
            try {
                URL url = new URL(strings[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(AppLog.PATH_FILE + strings[1]);

                byte data[] = new byte[BYTE_DATA_SIZE];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                AppLog.logString(e.getMessage());
                AppLog.toastString(e.getMessage(), context.getApplicationContext());
            }

            return null;
        }

        protected void onPostExecute(String paramString) {
            if (isAudio) {
                holder.getAudioProgress().setVisibility(View.GONE);
                holder.getAudioSourceIcon().setClickable(true);
                AppLog.toastString("Audio Downloaded Press Again to Listen!", getApplicationContext());
            }
            if (isOther) {
                holder.getOtherFileProgress().setVisibility(View.GONE);
                holder.getFileSourceIcon().setClickable(true);
                AppLog.toastString("File Downloaded Press Again to See!", getApplicationContext());
            }
            AppLog.setVibrate(context, AppLog.INTENSITY_HIGH);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            if (isAudio) {
                holder.getAudioSourceIcon().setClickable(false);
                holder.getAudioProgress().setVisibility(View.VISIBLE);
                holder.getAudioProgress().setIndeterminate(false);
            }
            if (isOther) {
                holder.getFileSourceIcon().setClickable(false);
                holder.getOtherFileProgress().setVisibility(View.VISIBLE);
                holder.getOtherFileProgress().setIndeterminate(false);
            }
        }

        protected void onProgressUpdate(String... paramVarArgs) {
            if (isAudio) {
                holder.getAudioProgress().setProgress(Integer.parseInt(paramVarArgs[0]));
            }
            if (isOther) {
                holder.getOtherFileProgress().setProgress(Integer.parseInt(paramVarArgs[0]));
            }
        }
    }
}