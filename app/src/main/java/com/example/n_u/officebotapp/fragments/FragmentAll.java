package com.example.n_u.officebotapp.fragments;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.activities.ReplyActivity;
import com.example.n_u.officebotapp.adapters.MessagesAdapter;
import com.example.n_u.officebotapp.intefaces.IMessages;
import com.example.n_u.officebotapp.models.Message;
import com.example.n_u.officebotapp.models.Status;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.utils.InternetConnection;
import com.example.n_u.officebotapp.utils.OBSession;
import com.example.n_u.officebotapp.utils.OfficeBotURI;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.n_u.officebotapp.R.id.empty_view_notes;
import static com.example.n_u.officebotapp.R.id.list_note;
import static com.example.n_u.officebotapp.R.id.post_note_refresh_list;

public class FragmentAll
        extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final IMessages request = (IMessages) OfficeBotURI.retrofit().create(IMessages.class);
    List<Message> msgList = new ArrayList<>();
    private Call<List<Message>> call = null;
    private MessagesAdapter adapter = null;
    private ScaleInAnimationAdapter animationAdapter;
    private DynamicListView lv = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    public static FragmentAll newInstance(int paramInt) {
        FragmentAll localFragmentAll = new FragmentAll();
        Bundle localBundle = new Bundle();
        localBundle.putInt(ARG_SECTION_NUMBER, paramInt);
        localFragmentAll.setArguments(localBundle);
        return localFragmentAll;
    }

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        try {
            if (InternetConnection.checkConnection(getActivity())) {
                data();
                return;
            }
            AppLog.toastString("Net Not Available", getActivity());
        } catch (Exception e) {
            AppLog.logString(e.getMessage());
            AppLog.toastString(e.getMessage(), getActivity());
        }
    }

    public View onCreateView(LayoutInflater lf
            , ViewGroup viewGroup
            , Bundle bundle) {
        View layoutInflater = lf.inflate(R.layout.tag_main_fragement_messages, viewGroup, false);
        lv = ((DynamicListView) layoutInflater.findViewById(list_note));
        swipeRefreshLayout = ((SwipeRefreshLayout) layoutInflater.findViewById(post_note_refresh_list));


        adapter = new MessagesAdapter(getActivity(), this.msgList, getChildFragmentManager());
        animationAdapter = new ScaleInAnimationAdapter(this.adapter);
        animationAdapter.setAbsListView(this.lv);
        lv.setAdapter(this.animationAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View listener, int position, long id) {
                Intent i = new Intent(FragmentAll.this.getActivity(), ReplyActivity.class);
                Bundle bundle = new Bundle();
                Message localMessage = FragmentAll.this.adapter.getItem(position);
                bundle.putString(getActivity().getString(R.string.MSG_ID_BUNDLE), String.valueOf(localMessage.getId()));
                bundle.putString(getActivity().getString(R.string.MSG_CONTENT_BUNDLE_KEY), localMessage.getContent());
                bundle.putString(getActivity().getString(R.string.MSG_TIME_BUNDLE_KEY), localMessage.getCreated_at());
                bundle.putString(getActivity().getString(R.string.MSG_OWNER_BUNDLE_KEY), String.valueOf(localMessage.getUser_id()));
                i.putExtra(getActivity().getString(R.string.MSG_KEY), bundle);
                getActivity().startActivity(i);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                try {
                    FragmentAll.this.data();
                } catch (Exception localException) {
                    AppLog.logString(localException.getMessage());
                    AppLog.toastString(localException.getMessage(), FragmentAll.this.getActivity());
                }
            }
        });
        swipeRefreshLayout.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View paramAnonymousView) {
                FragmentAll.this.swipeRefreshLayout.setRefreshing(true);
                try {
                    FragmentAll.this.data();
                    return false;
                } catch (Exception e) {
                    AppLog.logString(e.getMessage());
                    AppLog.toastString(e.getMessage(), FragmentAll.this.getActivity());
                }
                return false;
            }
        });
        lv.setEmptyView(layoutInflater.findViewById(empty_view_notes));
        if (this.adapter.getCount() > 0) {

        }
        viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.footer, this.lv, false);
        lv.addFooterView(viewGroup, null, false);
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public boolean onItemLongClick(AdapterView<?> parent, final View listener, final int position, long id) {
                if (Objects.equals(adapter.getItem(position).getUser_id(), Integer.parseInt(OBSession.getPreference(getActivity().getString(R.string.id_key), getActivity())))) {
                    Builder ab = new Builder(getActivity());
                    final HashMap<String, Object> map = new HashMap<>();
                    final IMessages localIMessages = OfficeBotURI.retrofit().create(IMessages.class);
                    ab.setMessage("Are you sure, You wanted to delete this?");
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int paramAnonymous2Int) {
                            map.put(getActivity().getString(R.string.message_id_key), adapter.getItem(position).getId());
                            adapter.remove(position);
                            adapter.notifyDataSetChanged();
                            localIMessages.deleteMessage(map).enqueue(new Callback<Status>() {
                                public void onFailure(Call<Status> callback, Throwable throwable) {
                                    Toast.makeText(FragmentAll.this.getActivity()
                                            , throwable.getMessage()
                                            , Toast.LENGTH_LONG).show();
                                }

                                public void onResponse(Call<Status> callback, Response<Status> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(FragmentAll.this.getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(FragmentAll.this.getActivity(), "" + response.errorBody(), Toast.LENGTH_LONG).show();
                                }
                            });
                            dialogInterface.cancel();
                        }
                    });
                    ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int paramAnonymous2Int) {
                            dialogInterface.cancel();
                        }
                    });
                    ab.create().show();
                } else {
                    Toast.makeText(getActivity()
                            , "Your have no right to delete this message"
                            , Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        return layoutInflater;
    }

    public void onActivityCreated(@Nullable Bundle paramBundle) {
        super.onActivityCreated(paramBundle);
    }

    void data() throws Exception {
        HashMap<String, Object> localHashMap = new HashMap<>();
        localHashMap.put(getActivity().getString(R.string.user_id_key), OBSession.getPreference(getActivity().getString(R.string.id_key), getActivity()));
        localHashMap.put(getActivity().getString(R.string.tag_id_key), getActivity().getIntent().getStringExtra(getActivity().getString(R.string.tag_id_key)));
        AppLog.logString(localHashMap.toString());
        call = request.getMessages(localHashMap);
        call.enqueue(new Callback<List<Message>>() {
            public void onFailure(Call<List<Message>> callback
                    , Throwable throwable) {
                AppLog.logString(throwable.getMessage());
                Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);
            }

            public void onResponse(Call<List<Message>> callback
                    , Response<List<Message>> response) {
                if (response.code() == 200) {
                    msgList = response.body();
                    adapter.clear();
                    adapter.addAll(FragmentAll.this.msgList);
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    AppLog.toastString("List Refreshed!", getActivity().getApplicationContext());

                    animationAdapter.notifyDataSetChanged();
                    return;
                }
                AppLog.toastString("Server Offline", FragmentAll.this.getActivity());
            }
        });
    }
}

