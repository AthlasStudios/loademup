package club.athlas.loademup.integration;

import club.athlas.loademup.api.virustotal.VirusTotal;
import club.athlas.loademup.api.virustotal.model.AVVerdict;
import club.athlas.loademup.api.virustotal.model.VTAnalysisResult;
import club.athlas.loademup.api.virustotal.model.VTUploadResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirusTotalClient implements VirusTotal {

    private static final ExecutorService executors = Executors.newFixedThreadPool(2);
    private static final Gson gson = new Gson();

    private final OkHttpClient client = new OkHttpClient();

    private final String apiKey;

    public VirusTotalClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public CompletableFuture<VTUploadResponse> runScan(File file) {
        return CompletableFuture.supplyAsync(() -> {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.virustotal.com/api/v3/files")
                    .post(requestBody)
                    .addHeader("x-apikey", apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response.code());
                }

                String json = response.body().string();
                JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

                JsonObject data = jsonObj.getAsJsonObject("data");
                String type = data.get("type").getAsString();
                String id = data.get("id").getAsString();
                String link = data.getAsJsonObject("links").get("self").getAsString();

                return new VTUploadResponse(type, id, link);
            } catch (IOException e) {
                throw new RuntimeException("VirusTotal scan failed", e);
            }
        }, executors);
    }

    @Override
    public CompletableFuture<VTAnalysisResult> getAnalysisResult(String analysisId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://www.virustotal.com/api/v3/analyses/" + analysisId;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-apikey", apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response.code());
                }

                String json = response.body().string();
                JsonObject root = gson.fromJson(json, JsonObject.class);
                JsonObject attributes = root.getAsJsonObject("data").getAsJsonObject("attributes");

                String status = attributes.get("status").getAsString();

                List<VTAnalysisResult.AVResult> avResults = new ArrayList<>();
                JsonObject results = attributes.getAsJsonObject("results");

                for (String engine : results.keySet()) {
                    JsonObject res = results.getAsJsonObject(engine);
                    String categoryRaw = res.get("category").getAsString();
                    AVVerdict verdict = AVVerdict.fromString(categoryRaw);
                    String type = res.has("result") && !res.get("result").isJsonNull() ? res.get("result").getAsString() : null;
                    String engineVersion = res.has("engine_version") && !res.get("engine_version").isJsonNull() ? res.get("engine_version").getAsString() : null;
                    String method = res.has("method") && !res.get("method").isJsonNull() ? res.get("method").getAsString() : null;

                    avResults.add(new VTAnalysisResult.AVResult(engine, engineVersion, verdict, type, method));
                }

                return new VTAnalysisResult(status, avResults);

            } catch (IOException e) {
                throw new RuntimeException("Failed to fetch analysis result", e);
            }
        }, executors);
    }

    @Override
    public CompletableFuture<Boolean> hasValidApiKey() {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url("https://www.virustotal.com/api/v3/users/me")
                    .get()
                    .addHeader("x-apikey", apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                throw new RuntimeException("Failed to check API key validity", e);
            }
        }, executors);
    }

    @Override
    public void shutdown() {
        client.dispatcher().cancelAll();
        client.connectionPool().evictAll();
        client.dispatcher().executorService().shutdownNow(); // ensure this is forced
        try {
            if (!client.dispatcher().executorService().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                System.err.println("OkHttp executor did not terminate in time.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executors.shutdownNow(); // this was shutdown() before — change to shutdownNow()
        try {
            if (!executors.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                System.err.println("Custom executor did not terminate in time.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
