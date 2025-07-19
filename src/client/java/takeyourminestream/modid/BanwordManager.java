package takeyourminestream.modid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import takeyourminestream.modid.interfaces.IBanwordManager;
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
import java.util.logging.Logger;

public class BanwordManager implements IBanwordManager {
    private static final Logger LOGGER = Logger.getLogger(BanwordManager.class.getName());
    private static final String RESOURCE_PATH = "/assets/take-your-minestream/banned_words.json";
    private static BanwordManager instance;
    
    private final Set<String> banwords = new HashSet<>();

    private BanwordManager() {
        loadBanwords();
    }

    public static BanwordManager getInstance() {
        if (instance == null) {
            instance = new BanwordManager();
        }
        return instance;
    }

    @Override
    public void loadBanwords() {
        try (InputStreamReader reader = new InputStreamReader(
                BanwordManager.class.getClassLoader().getResourceAsStream("assets/take-your-minestream/banned_words.json"),
                StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> words = new Gson().fromJson(reader, listType);
            banwords.clear();
            if (words != null) {
                banwords.addAll(words);
                LOGGER.info("Загружено " + words.size() + " банвордов");
            }
        } catch (Exception e) {
            LOGGER.severe("Ошибка при загрузке банвордов: " + e.getMessage());
        }
    }

    @Override
    public boolean containsBanwords(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String lower = Normalizer.normalize(message, Normalizer.Form.NFC).toLowerCase();
        for (String word : banwords) {
            String normWord = Normalizer.normalize(word, Normalizer.Form.NFC).toLowerCase();
            if (lower.contains(normWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String filterBanwords(String message) {
        if (message == null || message.trim().isEmpty()) {
            return message;
        }
        
        String censored = Normalizer.normalize(message, Normalizer.Form.NFC);
        for (String word : banwords) {
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

    @Override
    public void addBanword(String banword) {
        if (banword != null && !banword.trim().isEmpty()) {
            banwords.add(banword.trim().toLowerCase());
            LOGGER.info("Добавлен банворд: " + banword);
        }
    }

    @Override
    public void removeBanword(String banword) {
        if (banword != null && !banword.trim().isEmpty()) {
            boolean removed = banwords.remove(banword.trim().toLowerCase());
            if (removed) {
                LOGGER.info("Удален банворд: " + banword);
            }
        }
    }

    public Set<String> getBanwords() {
        return Collections.unmodifiableSet(banwords);
    }
} 