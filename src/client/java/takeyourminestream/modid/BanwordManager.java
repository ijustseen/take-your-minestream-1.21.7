package takeyourminestream.modid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BanwordManager {
    private static final String RESOURCE_PATH = "/assets/take-your-minestream/banned_words.json";
    private static final Set<String> BANWORDS = new HashSet<>();

    public static void loadBanwords() {
        try (InputStreamReader reader = new InputStreamReader(
                BanwordManager.class.getClassLoader().getResourceAsStream("assets/take-your-minestream/banned_words.json"),
                StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> words = new Gson().fromJson(reader, listType);
            BANWORDS.clear();
            if (words != null) BANWORDS.addAll(words);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean containsBanword(String message) {
        String lower = Normalizer.normalize(message, Normalizer.Form.NFC).toLowerCase();
        for (String word : BANWORDS) {
            String normWord = Normalizer.normalize(word, Normalizer.Form.NFC).toLowerCase();
            if (lower.contains(normWord)) {
                return true;
            }
        }
        return false;
    }

    public static String censorBanwords(String message) {
        String censored = Normalizer.normalize(message, Normalizer.Form.NFC);
        for (String word : BANWORDS) {
            String normWord = Normalizer.normalize(word, Normalizer.Form.NFC);
            if (normWord.length() > 0) {
                // Создаем маску: первая буква + звездочки для остальных
                String firstChar = normWord.substring(0, 1);
                String stars = "*".repeat(normWord.length() - 1);
                String mask = firstChar + stars;
                censored = censored.replaceAll("(?iu)(?<!\\p{L})" + java.util.regex.Pattern.quote(normWord) + "(?!\\p{L})", mask);
            }
        }
        return censored;
    }

    public static Set<String> getBanwords() {
        return Collections.unmodifiableSet(BANWORDS);
    }
} 