package club.athlas.loademup.api.virustotal.model;

import java.util.List;
import java.util.stream.Collectors;

public record VTAnalysisResult(String status, List<AVResult> results) {

    public record AVResult(String engine, String version, AVVerdict verdict, String type, String method) {

        public Severity severity() {
            return Severity.inferSeverity(verdict, type);
        }

    }

    public List<AVResult> detections() {
        return results.stream()
                .filter(avResult -> avResult.verdict().hasDetected())
                .collect(Collectors.toList());
    }

}
