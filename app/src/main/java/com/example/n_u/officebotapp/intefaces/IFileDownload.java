package com.example.n_u.officebotapp.intefaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface IFileDownload {
    @GET
    @Streaming
    Call<ResponseBody> fileUrlLink(@Url String paramString);
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.intefaces.IFileDownload

 * JD-Core Version:    0.7.0.1

 */