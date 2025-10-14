package com.arya.crypto.http;

import com.arya.crypto.response.CryptoResponse;
import com.arya.crypto.util.CryptoUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Slf4j
public abstract class AbstractHttpRequest implements HttpRequest {

    private static final String SECRET_KEY = "secretkey";
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final OkHttpClient client = new OkHttpClient();
    private static final Logger log = LoggerFactory.getLogger(AbstractHttpRequest.class);

    @Override
    public CryptoResponse client(Request request, boolean decrypt) throws IOException {
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        JsonNode bodyNode = mapper.readTree(response.body().string());
        response.body().close();

        if (decrypt) {
            try {
                String decodeString = CryptoUtils.decode(bodyNode.get("data").textValue(), SECRET_KEY);
                return mapper.readValue(decodeString, CryptoResponse.class);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return mapper.readValue(response.body().string(), CryptoResponse.class);
    }

    @Override
    public String encryptValue(Object value) {
        try {
            String requestParams = mapper.writeValueAsString(value);
            return CryptoUtils.encrypt(requestParams, SECRET_KEY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
