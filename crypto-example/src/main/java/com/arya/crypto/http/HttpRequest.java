package com.arya.crypto.http;

import com.arya.crypto.response.CryptoResponse;
import okhttp3.Request;

import java.io.IOException;

public interface HttpRequest {

    CryptoResponse client(Request request, boolean decryptResponse) throws IOException;

    String encryptValue(Object value);
}
