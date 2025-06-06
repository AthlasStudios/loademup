package club.athlas.loademup.api.virustotal;

import club.athlas.loademup.api.virustotal.model.VTAnalysisResult;
import club.athlas.loademup.api.virustotal.model.VTUploadResponse;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface VirusTotal {

    CompletableFuture<VTUploadResponse> runScan(File file);

    CompletableFuture<VTAnalysisResult> getAnalysisResult(String analysisId);

    CompletableFuture<Boolean> hasValidApiKey();

    void shutdown();
}
