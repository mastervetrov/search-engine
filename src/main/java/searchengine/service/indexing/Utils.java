package searchengine.service.indexing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Utils {
    public static String generateDomain(String url) {
        String[] parts = url.split("/");
        StringBuilder urlPath = new StringBuilder();
        for (int i = 0; i < Math.min(3, parts.length); i++) {
            urlPath.append(parts[i]).append("/");
        }
        url = urlPath.toString();
        url = url.replaceAll("https://www.", "https://");
        url = url.replaceAll("http://www.", "http://");
        log.warn("domain: " + url);
        return url;
    }

    /**
     * Using for writing regex
     *
     * @param input target string
     * @return target string with protective screens("\\")
     */
    public static String makeShieldForSpecialCharacters(String input) {
        StringBuilder escapedString = new StringBuilder();
        for (char c : input.toCharArray()) {
            if ("()[]{}.-*+?|^$\\\\".indexOf(c) >= 0) {
                escapedString.append("\\");
            }
            escapedString.append(c);
        }
        return escapedString.toString();
    }
}
