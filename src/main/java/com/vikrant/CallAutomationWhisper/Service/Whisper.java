package com.vikrant.CallAutomationWhisper.Service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.StatusLine;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class Whisper {
    @Value("${openai.api.key}")
    private String apiKey;

    private final static String URL = "https://api.openai.com/v1/audio/transcriptions";
    public final static int MAX_ALLOWED_SIZE = 25 * 1024 * 1024;
    public final static int MAX_CHUNK_SIZE_BYTES = 20 * 1024 * 1024;

    private final static String MODEL = "whisper-1";



    public String transcribe(String fileName) {
        System.out.println("Transcribing " + fileName);
        File file = new File(fileName);
        List<String> transcriptions = new ArrayList<>();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .setContentType(ContentType.MULTIPART_FORM_DATA)
                    .addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY))
                    .addPart("model", new StringBody(MODEL, ContentType.DEFAULT_TEXT))
                    .addPart("response_format", new StringBody("text", ContentType.DEFAULT_TEXT))
//                    .addPart("prompt", new StringBody(prompt, ContentType.DEFAULT_TEXT))
                    .build();
            httpPost.setEntity(entity);

            String transcription = client.execute(httpPost, response -> {
                System.out.println("Status: " + new StatusLine(response));
                return EntityUtils.toString(response.getEntity());
            });

            transcriptions.add(transcription);
            return transcription;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
