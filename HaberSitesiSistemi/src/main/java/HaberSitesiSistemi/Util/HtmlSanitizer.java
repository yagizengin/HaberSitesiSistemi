package HaberSitesiSistemi.Util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            .removeTags("img")
            .addEnforcedAttribute("a", "rel", "nofollow");


    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return Jsoup.clean(input, Safelist.none()).trim();
    }

    public static String sanitizeHtml(String input) {
        if (input == null) {
            return "";
        }
        return Jsoup.clean(input, RICH_TEXT_SAFELIST).trim();
    }

    public static String trim(String input) {
        return input == null ? null : input.trim();
    }
}
