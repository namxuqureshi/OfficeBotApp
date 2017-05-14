package com.example.n_u.officebotapp.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.activities.permissions.DoNotSharePermissionActivity;
import com.example.n_u.officebotapp.activities.permissions.SharePermissionActivity;
import com.example.n_u.officebotapp.helpers.Progress;
import com.example.n_u.officebotapp.intefaces.IMessages;
import com.example.n_u.officebotapp.models.Message;
import com.example.n_u.officebotapp.models.Status;
import com.example.n_u.officebotapp.models.Tag;
import com.example.n_u.officebotapp.services.OfflineNotePostService;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.InternetConnection;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.example.n_u.officebotapp.viewsholders.NewMessageHolder;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.DateTimeBuilder;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.fastaccess.datetimepicker.callback.TimePickerCallback;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import onactivityresult.ActivityResult;
import onactivityresult.OnActivityResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMessageActivity
        extends NavigationActivity
        implements OnClickListener, OnTouchListener, DatePickerCallback, TimePickerCallback {
    public static final int REQUEST_CODE_FOR_DO_NOT_SHARE = 2;
    public static final int REQUEST_CODE_FOR_SHARE = 1;
    private MultipartBody.Part bodyAudio = null, bodyFile = null, bodyAudioTemp = null, bodyFileTemp = null;
    private String timeOutTemp, caseTypeTemp, casePublic, tempFilePath, tempAudioPath, fileAudioSource = null;
    private Bundle bundle = new Bundle();
    private boolean caseEight = false, caseElevDs = false, caseElevS = false;
    private boolean caseForDs = false, caseForS = false, caseNineDs = false;
    private boolean caseNineS = false, caseSeven = false;
    private boolean caseTenDs = false, caseTenS = false, caseTewDs = false;
    private boolean caseTewS = false, caseTheDs = false, caseTheS = false;
    private boolean publicFlag = false;
    private MediaRecorder recorder = null;
    private HashMap<String, Object> sendArray = new HashMap<>();
    private int tagId, total;
    private NewMessageHolder holder;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.activity_nav_new_message);
        View v = LayoutInflater
                .from(this)
                .inflate(R.layout.activity_nav_new_message, null);
        holder = new NewMessageHolder(v, this);
        setContentView(v);
        setUi();
        setListener();
    }

    private void setListener() {
        holder.getPermissionCheck().setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    sendArray.put(getString(R.string.public_key), "1");
                    sendArray.put(getString(R.string.case_type_key), "1");
                } else {
                    sendArray.put(getString(R.string.public_key), casePublic);
                    sendArray.put(getString(R.string.case_type_key), caseTypeTemp);
                }
                AppLog.logString(casePublic + caseTypeTemp);
                AppLog.logString(sendArray.toString());
            }
        });
        holder.getAudioCheck().setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    bodyAudio = null;
                } else {
                    bodyAudio = bodyAudioTemp;
                }
                if (bodyAudio != null) {
                    AppLog.logString(bodyAudio.toString());
                }
            }
        });
        holder.getAttachFileCheck().setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    bodyFile = null;
                } else {
                    bodyFile = bodyFileTemp;
                }
                if (bodyFile != null) {
                    AppLog.logString(bodyFile.toString());
                }
            }
        });
        holder.getTimeOutCheck().setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    sendArray.remove(getString(R.string.timeout_key));
                } else {
                    sendArray.put(getString(R.string.timeout_key), timeOutTemp);
                }
            }
        });

    }

    private void startRecording() throws IllegalStateException {
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
            holder.getBtnAudioSource().performHapticFeedback(20);
        } catch (IllegalStateException | IOException e) {
            AppLog.toastString("Please Hold more then a Second to record", getApplicationContext());
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            AppLog.logString("testing");
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            holder.getBtnPlayAudio().setVisibility(View.VISIBLE);
            File file = new File(fileAudioSource);
            RequestBody localRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            bodyAudioTemp = MultipartBody.Part.createFormData(getString(R.string.audio_key), file.getName(), localRequestBody);
            AppLog.logString(fileAudioSource);
            AppLog.logString(bodyAudioTemp.toString());
            AppLog.logString("testing" + fileAudioSource + file.getPath());
            holder.getAudioCheck().setChecked(true);
            holder.getAudioCheck().setVisibility(View.VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLog.logString(String.valueOf(requestCode));
        AppLog.logString(String.valueOf(requestCode));
        try {
            ActivityResult.onResult(requestCode, resultCode, data).into(this);
        } catch (Exception e) {
            AppLog.logString(e.getMessage());
            AppLog.toastString(e.getMessage(), getApplicationContext());
        }
    }

    public void onClick(View v) {
        int i = v.getId();
        AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
        Button BtnShareWith;
        Button BtnDontShare;
        final CheckBox share, d_share;
        switch (i) {
            case R.id.text_add_permission:
                final MaterialDialog.Builder mb = new MaterialDialog.Builder(context);
                mb.customView(R.layout.dialogue_new_message_permission, true);
                mb.title(R.string.add_permission_menu);
                final MaterialDialog md = mb.build();
                share = (CheckBox) md.findViewById(R.id.share_check);
                d_share = (CheckBox) md.findViewById(R.id.d_share_check);
                BtnShareWith = (Button) md.findViewById(R.id.btn_share_with);
                BtnDontShare = (Button) md.findViewById(R.id.btn_do_not_share_with);
                BtnShareWith.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        caseTewS = caseElevS = caseTheS = caseNineS = caseTenS = caseForS = false;
                        Intent i = new Intent(getApplicationContext(), SharePermissionActivity.class);
                        share.setChecked(true);
                        share.setVisibility(View.GONE);
                        startActivityForResult(i, REQUEST_CODE_FOR_SHARE);
                        AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                    }
                });
                BtnDontShare.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        caseTewDs = caseElevDs = caseTheDs = caseNineDs = caseTenDs = caseForDs = false;
                        Intent i = new Intent(getApplicationContext(), DoNotSharePermissionActivity.class);
                        d_share.setChecked(true);
                        d_share.setVisibility(View.GONE);
                        startActivityForResult(i, REQUEST_CODE_FOR_DO_NOT_SHARE);
                        AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                    }
                });
                mb.positiveText(R.string.done);
                mb.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(context, R.string.set_permission, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                        caseSet();
                        holder.getPermissionCheck().setChecked(true);
                        holder.getPermissionCheck().setVisibility(View.VISIBLE);
                    }
                });
                mb.negativeText(R.string.cancel_btn);
                mb.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(context, R.string.cancel, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                        caseSet();
                        holder.getPermissionCheck().setChecked(false);
                        holder.getPermissionCheck().setVisibility(View.VISIBLE);
                    }
                });
                mb.show();
                break;
            case R.id.btn_cancel_button:
                finish();
                break;
        }
    }

    private void caseSet() {
        if ((publicFlag) && caseSeven) {
            sendArray.put(getString(R.string.case_type_key), "7");
            sendArray.put(getString(R.string.public_key), "1");
            AppLog.logString("onClick: " + "7");
        } else if ((publicFlag) && (caseEight)) {
            sendArray.put(getString(R.string.case_type_key), "8");
            sendArray.put(getString(R.string.public_key), "1");
            Log.e("CASE_TAG", "onClick: 8");
        } else if ((caseNineDs) && (caseNineS)) {
            sendArray.put(getString(R.string.case_type_key), "9");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 9");
        } else if ((caseTenS) && (caseTenDs)) {
            sendArray.put(getString(R.string.case_type_key), "10");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 10");
        } else if ((caseElevS) && (caseElevDs)) {
            sendArray.put(getString(R.string.case_type_key), "11");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 11");
        } else if ((caseTewS) && (caseTewDs)) {
            sendArray.put(getString(R.string.case_type_key), "12");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 12");
        } else if ((caseTheS) && (caseTheDs)) {
            sendArray.put(getString(R.string.case_type_key), "13");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 13");
        } else if ((caseForS) && (caseForDs)) {
            sendArray.put(getString(R.string.case_type_key), "14");
            sendArray.put(getString(R.string.public_key), "0");
            Log.e("CASE_TAG", "onClick: 14");
        } else {
            Log.e("CASE_TAG", "onClick: 1");
        }
        caseTypeTemp = (String) sendArray.get(getString(R.string.case_type_key));
        casePublic = (String) sendArray.get(getString(R.string.public_key));
    }

    public void onDateSet(long date) {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @OnActivityResult(requestCode = REQUEST_CODE_FOR_DO_NOT_SHARE)
    void onDoNotSharePermissionSet(int i, Intent data) {
        if (data != null) {
            bundle = data.getBundleExtra(getString(R.string.permission_key));
            if (bundle.getString(getString(R.string.case_type_key)) != null) {
                if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "5")) {
                    sendArray.put(getString(R.string.case_type_key), "5");
                    if (sendArray.containsKey(getString(R.string.dnt_sh_group_key))) {
                        sendArray.remove(getString(R.string.dnt_sh_group_key));
                    }
                    caseSeven = true;
                    caseNineDs = true;
                    caseElevDs = true;
                    caseTheDs = true;
                    sendArray.put(getString(R.string.dnt_sh_user_key)
                            , bundle.getIntegerArrayList(getString(R.string.users_integer_key)));
                    Log.e("TAG1", "onActivityResult: " + sendArray.toString());
                    Toast.makeText(this, "User Specified DoNotSharePermission set"
                            , Toast.LENGTH_LONG).show();
                } else if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "6")) {
                    sendArray.put(getString(R.string.case_type_key), "6");
                    if (sendArray.containsKey(getString(R.string.dnt_sh_user_key)))
                        sendArray.remove(getString(R.string.dnt_sh_user_key));
                    caseEight = true;
                    caseTenDs = true;
                    caseTewDs = true;
                    caseForDs = true;
                    sendArray.put(getString(R.string.dnt_sh_group_key)
                            , bundle.getIntegerArrayList(getString(R.string.groups_integers_key)));
                    Log.e("TAG2", "onActivityResult: " + sendArray.toString());
                    Toast.makeText(this, "Group DoNotSharePermission set", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(this, "Nothing is set! ",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Nothing is set! ",
                Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnActivityResult(requestCode = REQUEST_CODE_FOR_SHARE)
    void onSharePermissionSet(int i, Intent data) {
        if (data != null) {
            bundle = data.getBundleExtra(getString(R.string.permission_key));
//            if (!sendArray.isEmpty()) sendArray.clear();

            if (bundle.getString(getString(R.string.case_type_key)) != null) {
                if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "1")) {
                    sendArray.put(getString(R.string.case_type_key), "1");
                    publicFlag = true;
                    sendArray.put(getString(R.string.public_key), bundle.getString(getString(R.string.public_key)));
                    if (sendArray.containsKey(getString(R.string.share_user_key)))
                        sendArray.remove(getString(R.string.share_user_key));
                    if (sendArray.containsKey(getString(R.string.share_group_key)))
                        sendArray.remove(getString(R.string.share_group_key));
                    Log.e("TAG0", "onActivityResult: " + sendArray.toString());
                    Toast.makeText(this, "Public SharePermission set", Toast.LENGTH_SHORT).show();

                } else if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "2")) {
                    if (sendArray.containsKey(getString(R.string.share_group_key)))
                        sendArray.remove(getString(R.string.share_group_key));
                    sendArray.put(getString(R.string.case_type_key), "2");
                    caseNineS = true;
                    caseTenS = true;
                    sendArray.put(getString(R.string.share_user_key), bundle.getIntegerArrayList(getString(R.string.users_integer_key)));
                    Log.e("TAG1", "onActivityResult: " + sendArray.toString());
                    Toast.makeText(this, "User Specified SharePermission set", Toast.LENGTH_SHORT).show();

                } else if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "3")) {
                    sendArray.put(getString(R.string.case_type_key), "3");
                    if (sendArray.containsKey(getString(R.string.share_user_key)))
                        sendArray.remove(getString(R.string.share_user_key));
                    sendArray.put(getString(R.string.share_group_key), bundle.getIntegerArrayList(getString(R.string.groups_integers_key)));
                    Log.e("TAG2", "onActivityResult: " + sendArray.toString());
                    caseElevS = true;
                    caseTewS = true;
                    Toast.makeText(this, "Group SharePermission set", Toast.LENGTH_SHORT).show();

                } else if (Objects.equals(bundle.getString(getString(R.string.case_type_key)), "4")) {
                    if (sendArray.containsKey(getString(R.string.share_user_key)))
                        sendArray.remove(getString(R.string.share_user_key));
                    if (sendArray.containsKey(getString(R.string.share_group_key)))
                        sendArray.remove(getString(R.string.share_group_key));
                    sendArray.put(getString(R.string.case_type_key), "4");
                    sendArray.put(getString(R.string.friends_only_key), "1");
                    caseTheS = true;
                    caseForS = true;
                    Log.e("TAG4", "onActivityResult: " + sendArray.toString());
                    Toast.makeText(this, "Friends Only SharePermission set", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            sendArray.put(getString(R.string.case_type_key), "1");
            sendArray.put(getString(R.string.public_key), "");
            Toast.makeText(this, "Public Permission set", Toast.LENGTH_SHORT).show();
        }
    }


    public void onTimeSet(long timeOnly, long dateWithTime) {
        AppLog.logString(AppLog.getDateAndTime(dateWithTime));
        AppLog.logString(String.valueOf(dateWithTime));
        AppLog.toastString(AppLog.getDateAndTime(dateWithTime), getApplicationContext());
        sendArray.put(getString(R.string.timeout_key), AppLog.getDateAndTime(dateWithTime));
        timeOutTemp = AppLog.getDateAndTime(dateWithTime);
        holder.getTimeOutCheck().setChecked(true);
        holder.getTimeOutCheck().setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int i = event.getAction();
        AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
        int id = v.getId();
        switch (id) {
            case R.id.btn_audio_source:
                switch (i) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            Progress.Show(context, "Recording..");
                            startRecording();
                        } catch (IllegalStateException e) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
//                    AppLog.logString(e.getMessage());
                        } catch (RuntimeException e1) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
//                    AppLog.logString(e1.getMessage());
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        try {
                            Progress.Cancel();
                            stopRecording();
                        } catch (IllegalStateException e) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
                        } catch (RuntimeException e1) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
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
                                AppLog.toastShortString("Audio Cancel"
                                        , getApplicationContext());
                            }
                        } catch (IllegalStateException e) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
                        } catch (RuntimeException e1) {
                            AppLog.toastString("Please Hold more then a Second to record"
                                    , getApplicationContext());
                        }
                        break;
                }
                break;
            case R.id.btn_play_audio:
                AppLog.defaultFileOpener(fileAudioSource, context);
                break;
        }
        return false;
    }

    public void sendMsg(final View view) {
        if (!holder.getEditTextContentMessage().getText().toString().isEmpty()) {

            sendArray.put(getString(R.string.content_key), holder.getEditTextContentMessage().getText().toString());
            sendArray.put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.id_key), getApplicationContext()));
            sendArray.put(getString(R.string.tag_id_key), tagId);
            sendArray.put(getString(R.string.type_key), "text");
            final IMessages sendMsg = OfficeBotURI.retrofit().create(IMessages.class);
            AppLog.logString(sendArray.toString());
            if (InternetConnection.checkConnection(context)) {
                Progress.Show(this, getString(R.string.sending_loading));
                Call<Status> call = sendMsg.sentMessage(sendArray);
                call.enqueue(new Callback<Status>() {
                    public void onFailure(Call<Status> call, Throwable t) {
                        AppLog.toastString(t.getMessage() + "\n" + "Note Failed", getApplicationContext());
                        AppLog.logString(t.getMessage());
                        Progress.Cancel();
                        finish();
                    }

                    public void onResponse(Call<Status> call
                            , Response<Status> response) {
                        AppLog.logString(response.code() + "");
                        if (response.code() == 200) {
                            Log.e("TAG", "onResponse: " + response.body().getMessage_id());
                            if ((bodyFile != null) || (bodyAudio != null)) {
                                if ((bodyAudio != null) && (bodyFile == null)) {
                                    call = sendMsg.sentMessageFile(bodyAudio, String.valueOf(response.body().getMessage_id()));
                                } else if ((bodyAudio == null) && (bodyFile != null)) {
                                    call = sendMsg.sentMessageFile(bodyFile, String.valueOf(response.body().getMessage_id()));
                                } else {
                                    call = sendMsg.sentMessageFile(bodyAudio, bodyFile, String.valueOf(response.body().getMessage_id()));
                                }
                                call.enqueue(new Callback<Status>() {
                                    public void onFailure(Call<Status> call
                                            , Throwable t) {
                                        Progress.Cancel();
                                        AppLog.toastString(t.getMessage(), getApplicationContext());
                                        AppLog.logString(t.getMessage());
                                        finish();
                                    }

                                    public void onResponse(Call<Status> call
                                            , Response<Status> response) {
                                        if (response.code() == 200) {
                                            AppLog.toastString(getString(R.string.note_send), getApplicationContext());

                                        } else {
                                            AppLog.toastString(getString(R.string.note_send) + "file not send slow net"
                                                    , getApplicationContext());
                                        }
                                        Progress.Cancel();
                                        finish();

                                    }

                                });

                            } else
                                AppLog.toastString(getString(R.string.note_send), getApplicationContext());
                            Progress.Cancel();
                            Tag.setTagTotalMsg(tagId, (total + 1));
                            finish();

                        } else {
                            AppLog.toastString("Server Offline\n" + response.code(), getApplicationContext());
                        }
                        finish();
                        Progress.Cancel();
                    }
                });
            } else {
                Bundle bnd = new Bundle();
                sendArray.put("created_at", AppLog.getDateTimeNow());
                if (bodyAudioTemp != null) {
                    tempAudioPath = AppLog.getFileTempName();
                    File from = new File(fileAudioSource);
                    File to = new File(tempAudioPath);
                    if (!to.exists()) {
                        try {
                            to.createNewFile();

                            System.out.println("Destination file doesn't exist. Creating one!");
                        } catch (IOException e) {
                            AppLog.logString(e.getMessage());
                        }
                    }
                    FileChannel source = null;
                    FileChannel destination = null;
                    try {
                        source = new FileInputStream(from).getChannel();
                        destination = new FileOutputStream(to).getChannel();
                        if (source != null) {
                            destination.transferFrom(source, 0, source.size());
                        }
                    } catch (IOException e) {
                        AppLog.logString(e.getMessage());
                    } finally {
                        if (source != null) {
                            try {
                                source.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (destination != null) {
                            try {
                                destination.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    sendArray.put("audioFile", tempAudioPath);
                }
                if (bodyFileTemp != null)
                    sendArray.put("otherFile", tempFilePath);
                bnd.putSerializable(getString(R.string.EXTRA_ARRAY), sendArray);
                Intent i = new Intent(context, OfflineNotePostService.class);
                AppLog.toastShortString("When Net Available Note will be posted", getApplicationContext());
                i.putExtra("bnd", bnd);
                setNoteOffline();
                startService(i);
                finish();
            }
        } else {
            AppLog.toastShortString("Msg Field Empty", getApplicationContext());
            holder.getEditTextContentMessage().setError("Msg Required");
        }
        AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
    }

    private void setNoteOffline() {
        Message msg = new Message();
        msg.setMsgId(0);
        if (casePublic != null)
            msg.setMpublic(Integer.parseInt(casePublic));
        else msg.setMpublic(1);
        msg.setTimeout(timeOutTemp);
        msg.setCreated_at(AppLog.getDateTimeNow());
        if (caseTypeTemp != null)
            msg.setCase_type(Integer.parseInt(caseTypeTemp));
        else msg.setCase_type(1);
        msg.setType("text");
        msg.setTag_id(tagId);
        msg.setContent(holder.getEditTextContentMessage().getText().toString());
        msg.setUser_id(Integer.parseInt(OBSession.getPreference(getString(R.string.id_key)
                , getApplicationContext())));
        msg.save();
    }

    public void setAttachFile(final View view) {
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
                            tempFilePath = file.getAbsolutePath();
                            RequestBody requestBody = RequestBody
                                    .create(MediaType.parse("multipart/form-data"), file);
                            bodyFileTemp = MultipartBody.Part.createFormData(getString(R.string.data_key)
                                    , file.getName()
                                    , requestBody);
                            holder.getAttachFileCheck().setChecked(true);
                            holder.getAttachFileCheck().setVisibility(View.VISIBLE);
                        } else AppLog.toastString("File size is exceed only 2MB is applicable"
                                , getApplicationContext());
                    } else AppLog.toastString("No File Selected", getApplicationContext());
                    AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                }
            });
            views.show();
        } catch (RuntimeException e) {
            AppLog.logString(e.getMessage());
            AppLog.toastString(e.getMessage(), getApplicationContext());
        }
        AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
    }

    public void setTimeOut(final View view) {
        Calendar paramView = Calendar.getInstance(Locale.getDefault());
        paramView.set(paramView.get(Calendar.YEAR)
                , paramView.get(Calendar.MONTH)
                , paramView.get(Calendar.DATE));
        Date localDate = new Date();
        DatePickerFragmentDialog.newInstance(
                DateTimeBuilder.get()
                        .withTime(true)
                        .with24Hours(true)
                        .withSelectedDate(paramView.getTime().getTime())
                        .withMinDate(paramView.getTimeInMillis())
                        .withCurrentHour(localDate.getHours())
                        .withCurrentMinute(localDate.getMinutes()))
                .show(getSupportFragmentManager(), "DatePickerFragmentDialog");
        AppLog.setVibrate(this, AppLog.INTENSITY_LOW);
    }

    void setUi() {
        tagId = getIntent().getIntExtra(getString(R.string.tag_id_key), 0);
        total = getIntent().getIntExtra(getString(R.string.total_msg), 0);
        holder.getBtnCancelButton().setOnClickListener(this);
        holder.getBtnAudioSource().setOnTouchListener(this);
        holder.getBtnPlayAudio().setOnTouchListener(this);
        holder.getTextAddPermission().setOnClickListener(this);
        nav(false, getString(R.string.message_activity));
    }
}