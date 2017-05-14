package com.example.n_u.officebotapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.appnirman.vaidationutils.ValidationUtils;
import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.helpers.Progress;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.InternetConnection;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.example.n_u.officebotapp.utils.OkHttp;
import com.example.n_u.officebotapp.viewsholders.ActivityRegisterHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity
        extends AppCompatActivity {
    Context context = this;
    private ValidationUtils validationUtils = null;
    private ActivityRegisterHolder holder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
        validationUtils = new ValidationUtils(this);
        View v = LayoutInflater.from(this).inflate(R.layout.activity_register, null);
        holder = new ActivityRegisterHolder(v);
        setContentView(v);
//        email = (TextInputEditText) findViewById(R.id.input_reg_email);
//        password = (TextInputEditText) findViewById(R.id.input_reg_password);
//        name = (TextInputEditText) findViewById(R.id.input_reg_name);
//        emailLayout = (TextInputLayout) findViewById(R.id.layout_input_reg_email);
//        passwordLayout = (TextInputLayout) findViewById(R.id.layout_input_reg_password);
//        nameLayout = (TextInputLayout) findViewById(R.id.layout_input_reg_name);

        holder.getInputRegEmail().addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                holder.getLayoutInputRegEmail().setErrorEnabled(false);
            }

            public void beforeTextChanged(CharSequence s
                    , int start
                    , int count
                    , int after) {
            }

            public void onTextChanged(CharSequence s
                    , int start
                    , int before
                    , int count) {
            }
        });
        holder.getInputRegPassword().addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                holder.getLayoutInputRegPassword().setErrorEnabled(false);
            }

            public void beforeTextChanged(CharSequence s
                    , int start
                    , int count
                    , int after) {
            }

            public void onTextChanged(CharSequence s
                    , int start
                    , int before
                    , int count) {
            }
        });
        holder.getInputRegName().addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                holder.getLayoutInputRegName().setErrorEnabled(false);
            }

            public void beforeTextChanged(CharSequence s
                    , int start
                    , int count
                    , int after) {
            }

            public void onTextChanged(CharSequence s
                    , int start
                    , int before
                    , int count) {
            }
        });
    }

    private boolean isValidData() {
        if (!validationUtils.isValidEmail(holder.getInputRegEmail().getText().toString())) {
            holder.getLayoutInputRegEmail().setErrorEnabled(true);
            holder.getLayoutInputRegEmail().setError(getString(R.string.email_error));
            AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
            return false;
        }
        if (!validationUtils.isValidPassword(holder.getInputRegPassword().getText().toString())) {
            holder.getLayoutInputRegPassword().setErrorEnabled(true);
            holder.getLayoutInputRegPassword().setError(getString(R.string.password_error));
            AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
            return false;
        }
        if (!validationUtils.isValidFirstName(holder.getInputRegName().getText().toString())) {
            holder.getLayoutInputRegName().setErrorEnabled(true);
            holder.getLayoutInputRegName().setError(getString(R.string.name_error));
            AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
            return false;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            View localView = getCurrentFocus();
            if ((localView instanceof EditText)) {
                Rect localRect = new Rect();
                localView.getGlobalVisibleRect(localRect);
                if (!localRect.contains((int) ev.getRawX(),
                        (int) ev.getRawY())) {
                    localView.clearFocus();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(localView.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void goBack(View view) {
        AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
        onBackPressed();
    }

    public void register(View view) {
        if ((!holder.getInputRegEmail().getText().toString().isEmpty())
                && (!holder.getInputRegName().getText().toString().isEmpty())
                && (!holder.getInputRegPassword().getText().toString().isEmpty())) {
            if ((isValidData()) && (InternetConnection.checkConnection(getApplicationContext()))) {
                JSONObject request_data = new JSONObject();
                try {
                    request_data.put(getString(R.string.email_key), holder.getInputRegEmail().getText().toString());
                    request_data.put(getString(R.string.password_key), holder.getInputRegPassword().getText().toString());
                    request_data.put(getString(R.string.phone_key), "");
                    request_data.put(getString(R.string.name_key), holder.getInputRegName().getText().toString());
                    Progress.Show(context, "Signing In..");
                    OkHttp.post(OfficeBotURI.getRegister(),
                            request_data.toString(),
                            new Callback() {
                                public void onFailure(Call call, final IOException e) {
                                    AppLog.setVibrate(context, AppLog.INTENSITY_LOW);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    e.getMessage() + AppLog.NET_NOT_AVAILABLE,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    AppLog.logString(e.getLocalizedMessage());
                                    Progress.Cancel();
                                }

                                public void onResponse(final Call call, final Response response)
                                        throws IOException {
                                    AppLog.setVibrate(context, AppLog.INTENSITY_MIDDLE);
                                    final String responseData = response.body().string();
                                    Log.e("TAG2", "onResponse: " + call);
                                    try {
                                        JSONObject jObj = new JSONObject(responseData);
                                        Log.e("TAG2", "onResponse: " + call);
                                        final JSONObject a = jObj.getJSONObject("error");
                                        if (a.getBoolean("if")) {
                                            Progress.Cancel();
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Toast.makeText(RegisterActivity.this, a.getString("status"), Toast.LENGTH_LONG).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            Progress.Cancel();
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    try {
                                                        Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_LONG).show();
                                                        JSONObject jsonObject1 = new JSONObject(responseData).getJSONObject(getString(R.string.user_data_key));
                                                        AppLog.logString("friendListSet" + response.toString());
                                                        OBSession.putPreference(getString(R.string.user_data_key),
                                                                responseData,
                                                                getApplicationContext());
                                                        OBSession.putPreference(getString(R.string.id_key),
                                                                jsonObject1.getString("id"),
                                                                getApplicationContext());
                                                        OBSession.putPreference(getString(R.string.name_key),
                                                                jsonObject1.getString("name"),
                                                                getApplicationContext());
                                                        OBSession.putPreference(getString(R.string.email_key),
                                                                jsonObject1.getString("email"),
                                                                getApplicationContext());
                                                        OBSession.putPreference(getString(R.string.phone_key),
                                                                jsonObject1.getString("phone"),
                                                                getApplicationContext());
                                                        Intent intent = new Intent(RegisterActivity.this, TagActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } catch (JSONException localJSONException) {
                                                        AppLog.logString(localJSONException.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    } catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                } catch (JSONException localJSONException) {
                    AppLog.logString(localJSONException.getMessage());
                }
            } else
                Snackbar.make(view, AppLog.NET_NOT_AVAILABLE, Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(view, R.string.fields_empty, Snackbar.LENGTH_SHORT).show();
        AppLog.setVibrate(this, AppLog.INTENSITY_MIDDLE);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.activities.RegisterActivity

 * JD-Core Version:    0.7.0.1

 */