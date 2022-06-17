import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Main {
    public static final String LINK =
            "https://api.nasa.gov/planetary/apod?api_key=8eNBQhOlMqzJFDYOPVu00Z9a3WG8fnSCbv2VNpJ3";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static CloseableHttpClient clientBuilder() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        return  httpClient;
    }

    public static CloseableHttpResponse responseMethod(String link) throws IOException {
            return clientBuilder().execute(new HttpGet(link));
    }

    public static StringBuilder gettingFileName(Planetary planetary) {
        Deque<Character> partOfUrl = new ArrayDeque<>();
        StringBuilder builder = new StringBuilder();
        char[] urlArray = planetary.getUrl().toCharArray();

        for (int i = urlArray.length - 1; i > 0; i--) {
            if (urlArray[i] != '/') {
                partOfUrl.add(urlArray[i]);
            } else break;
        }
        int length = partOfUrl.size();
        for (int i = 0; i < length; i++) {
            builder.append(partOfUrl.pollLast());
        }
        return builder;
    }


    public static void main(String[] args) throws IOException {
        CloseableHttpResponse response =
                responseMethod(LINK);
        Planetary planetary = mapper.readValue(response.getEntity().getContent(), Planetary.class);

        String fileName = gettingFileName(planetary).toString();
        CloseableHttpResponse response1 = responseMethod(planetary.getUrl());

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = response1.getEntity().getContent().readAllBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
