package com.example.n_u.officebotapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.adapters.AddNewTagAdapter;
import com.example.n_u.officebotapp.intefaces.IStatus;
import com.example.n_u.officebotapp.models.Status;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewTagActivity
        extends NavigationActivity {
    private HashMap<String, Object> body = new HashMap<>();
    private IStatus request = (IStatus) OfficeBotURI.retrofit().create(IStatus.class);
    private GridView gridView;
    private AddNewTagAdapter adapter;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_nav_add_new_tag);
        nav(getString(R.string.add_new_tag_activity));
        List<Integer> numbers = new ArrayList<>();
        numbers.add(R.drawable.office);
        numbers.add(R.drawable.home);
        numbers.add(R.drawable.logo);
        adapter = new AddNewTagAdapter(this, numbers);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gridView.setItemChecked(1, true);
        gridView.setAdapter(adapter);
    }

    public void onPressedGo(View view) {
        if (((EditText) findViewById(R.id.tag_name)).getText().toString().isEmpty() && !adapter.getName().equals("0")) {
            if (!adapter.getName().equals("0"))
                Toast.makeText(this, R.string.empty_field, Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Chose Any One Picture!", Toast.LENGTH_LONG).show();
        } else {
            AppLog.logString(adapter.getName());
            body.put(getString(R.string.image_src_key), adapter.getName());
            body.put(getString(R.string.name_key), ((EditText) findViewById(R.id.tag_name)).getText().toString());
            body.put(getString(R.string.user_id_key), OBSession.getPreference(getString(R.string.id_key), this));
            body.put(getString(R.string.ssn_key), getIntent().getStringExtra(getString(R.string.tag_id_key)));
            Call<Status> call = request.addNewTag(this.body);
            call.enqueue(new Callback<Status>() {
                public void onFailure(Call<Status> call,
                                      Throwable t) {
                    Toast.makeText(AddNewTagActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }

                public void onResponse(Call<Status> callback,
                                       Response<Status> statusResponse) {
                    if (statusResponse.code() == 200) {
                        Toast.makeText(AddNewTagActivity.this, statusResponse.body().getStatus(),
                                Toast.LENGTH_LONG).show();
                        Intent i = new Intent(AddNewTagActivity.this, TagActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(AddNewTagActivity.this,
                                "Server Offline" + statusResponse.code(),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }
    }

//    public static String getName(Integer i) {
//        switch (i) {
//            case R.drawable.circle:
//                return "circle";
//            case R.drawable.space:
//                return "space";
//            case R.drawable.logo:
//                return "logo";
//            default:
//                return "0";
//        }
//
//    }
}
