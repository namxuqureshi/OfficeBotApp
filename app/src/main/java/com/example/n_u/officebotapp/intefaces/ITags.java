package com.example.n_u.officebotapp.intefaces;

import com.example.n_u.officebotapp.models.ModelList;
import com.example.n_u.officebotapp.models.Status;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ITags {
    @GET("{id}/tag")
    Call<ModelList> getTagList(@Path("id") String paramString);

    @POST("set/permissions")
    Call<Status> setPermissionForTag(@Body Map<String, Object> paramMap);

    @PUT("tags/{id}")
    Call<Status> setTagFeature(@Body Map<String, Object> paramMap, @Path("id") int id);

    @DELETE("tags/{id}")
    Call<Status> deleteTag(@Path("id") int id);
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.intefaces.ITags

 * JD-Core Version:    0.7.0.1

 */