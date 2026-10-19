package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.virustotal.model.VTAnalysisResult;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.utils.TagResolverUtils;
import dev.gurwi.prism.bukkit.argument.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ScanCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public ScanCommand(LoadEmUp plugin) {
        super(plugin, "scan");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withDescription("Scan a plugin for viruses.");
        withPermission(LEUPermission.SCAN_COMMAND);

        withArguments(
                new GreedyStringArgument("plugin")
                        .replaceSuggestions(
                                loader.getPluginJarFiles().stream()
                                        .map(File::getName)
                                        .collect(Collectors.toList())
                        )
        );

        executes((sender, args) -> {
            String jarFileName = args.get("plugin");

            sender.sendMessage("\n");

            plugin.getVirusTotalIntegration().ifPresentOrElse(
                    virusTotal -> loader.getPluginFile(jarFileName).ifPresentOrElse(
                            file -> {

                                plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_START);
                                virusTotal.runScan(file).whenComplete((uploadResponse, uploadErr) -> {
                                    if (uploadErr != null) {
                                        plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_UNABLE_TO_SCAN);
                                        plugin.getLogger().log(Level.WARNING, "[VirusTotal] Unable to scan " + jarFileName, uploadErr);
                                        return;
                                    }

                                    virusTotal.getAnalysisResult(uploadResponse.id()).whenComplete(((vtAnalysisResult, getErr) -> {
                                        if (getErr != null) {
                                            plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_NO_RESULT);
                                            plugin.getLogger().log(Level.WARNING, "[VirusTotal] Unable get result " + jarFileName, getErr);
                                            return;
                                        }

                                        List<VTAnalysisResult.AVResult> detections = vtAnalysisResult.results();
                                        TagResolver[] placeholders = {
                                                Placeholder.component("list", getDetectionsList(detections)),
                                                TagResolverUtils.resolverFrom(vtAnalysisResult)
                                        };

                                        if (detections.isEmpty()) {
                                            plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_CLEAR, placeholders);
                                            return;
                                        }

                                        plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_THREATS_FOUND, placeholders);
                                    }));
                                });
                            },

                            () -> plugin.getMessages().send(sender, LangKey.JAR_NOT_FOUND, Placeholder.parsed("file", jarFileName))
                    ),

                    () -> plugin.getMessages().send(sender, LangKey.VIRUS_SCAN_SERVICE_DISABLED)
            );

            sender.sendMessage("\n");
        });
    }

    private @NotNull Component getDetectionsList(@NotNull List<VTAnalysisResult.AVResult> results) {
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < results.size(); i++) {
            VTAnalysisResult.AVResult avResult = results.get(i);
            builder.append(plugin.getMessages().getComponent(LangKey.VIRUS_SCAN_RESULT_FORMAT, TagResolverUtils.resolverFrom(avResult)));
            if (i < results.size() - 1) builder.appendNewline();
        }

        return builder.build();
    }

}