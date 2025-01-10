package com.arya.crypto.request;

import com.arya.crypto.enums.MediaTypeEnum;
import com.arya.crypto.http.AbstractHttpRequest;
import com.arya.crypto.response.CryptoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CryptoRequest extends AbstractHttpRequest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final StringBuffer url = new StringBuffer("http://localhost:8080/crypto/");

    public CryptoResponse get(String value, boolean decryptResponse) throws IOException {
        return get(value, null, decryptResponse);
    }

    public CryptoResponse get(String path, Object value, boolean decrypt) throws IOException {
        url.append(path);
        if (value != null) {
            url.append("?data=").append(encryptValue(value));
        }

        Request request = new Request.Builder().url(url.toString()).build();
        return client(request, decrypt);
    }

    /**
     * application/json; charset=utf-8
     */
    public CryptoResponse jsonPost(String path, Object value, boolean decrypt) throws IOException {
        MediaType parse = MediaType.parse(MediaTypeEnum.APPLICATION_JSON.getType());

        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("data", encryptValue(value));

        Request request = new Request.Builder()
                .url(String.valueOf(url.append(path)))
                .post(RequestBody.create(mapper.writeValueAsString(bodyMap), parse))
                .build();

        return client(request, decrypt);
    }

    /**
     * application/x-www-form-urlencoded
     */
    public CryptoResponse formPost(String path, Object value, boolean decrypt) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("data", encryptValue(value)).build();

        Request request = new Request.Builder()
                .url(String.valueOf(url.append(path)))
                .post(formBody)
                .build();

        return client(request, decrypt);
    }

    /**
     * multipart/form-data
     */
    public CryptoResponse filePost(String path, String filePath, boolean decrypt) throws IOException {
        MediaType parse = MediaType.parse(MediaTypeEnum.MULTIPART.getType());
        RequestBody fileBody = RequestBody.create(new File(filePath), parse);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "avatar", fileBody)
                .build();

        Request request = new Request.Builder()
                .url(String.valueOf(url.append(path)))
                .post(multipartBody)
                .build();

        return client(request, decrypt);
    }
}
