package com.arya.crypto.test;

import com.arya.crypto.request.CryptoRequest;
import com.arya.crypto.response.CryptoResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestTest {

    public static void main(String[] args) throws IOException {

        CryptoRequest cryptoRequest = new CryptoRequest();

        Map<String, String> params = new HashMap<>();
        params.put("id", "123456");

//        CryptoResponse response = cryptoRequest.get("get", true);
//        CryptoResponse response = cryptoRequest.get("get-params", params, true);

        Map<String, String> postParams = new HashMap<>();
        postParams.put("name", "user1");
        postParams.put("idCard", "147258");
        postParams.put("timestamp", String.valueOf(System.currentTimeMillis()));

//        CryptoResponse response = cryptoRequest.jsonPost("encrypted-body", postParams, true);
//        CryptoResponse response = cryptoRequest.formPost("encrypted-param", postParams, true);
        // 文件默认不加密
        CryptoResponse response = cryptoRequest.filePost("upload", "E:\\project\\crypto_request\\crypto-example\\src\\main\\java\\com\\arya\\crypto\\image\\avatar.jpg", true);

        System.out.println(response);
    }
}
