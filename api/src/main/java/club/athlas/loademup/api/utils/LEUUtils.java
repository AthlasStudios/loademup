package club.athlas.loademup.api.utils;

import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.object.LEUFile;

import java.io.File;

public class LEUUtils {
    
    public static boolean isSupported(File file, LEUAction action) {
        return LEUFile.from(file).map(f -> f.isSupported(action)).orElse(true);
    }

}
