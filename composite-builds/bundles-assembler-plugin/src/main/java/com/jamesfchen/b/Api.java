package com.jamesfchen.b;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Aug/06/2022  Sat
 */
public class Api {
    private static final OkHttpClient sOkHttpClient = new OkHttpClient();

    public static void postFile(String url, File file) {
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file)).build();
        Request r = new Request.Builder().url(url).post(multipartBody).build();
        try (Response resp = sOkHttpClient.newCall(r).execute()) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
