package com.example.n_u.officebotapp.intefaces;

import com.example.n_u.officebotapp.models.ModelList;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IFriends {
    @POST("friend/list")
    Call<ModelList> getFriendList(@Body HashMap<String, Object> paramHashMap);

    @POST("group/friend/list")
    Call<ModelList> getGroupMemberList(@Body HashMap<String, Object> paramHashMap);
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.intefaces.IFriends

 * JD-Core Version:    0.7.0.1

 */