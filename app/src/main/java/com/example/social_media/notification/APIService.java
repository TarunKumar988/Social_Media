package com.example.social_media.notification;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHS4oI9g:APA91bE9rpnTLdi1nDlP8MAabj4YssJpv7g7My9JbWFbOUDMvjHBgeDwDhrikjRzImV2UmpOLuxqyGTSnWWK6FA5z6VOUNnwh9ZseH3RXYPX52jGj00-xBul1qBhGc8I_UqUWq0IN-_C"
            })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
