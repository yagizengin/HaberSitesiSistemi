package HaberSitesiSistemi.Util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            .addTags("iframe")
            .addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen", "class",
                    "style")
            .addProtocols("iframe", "src", "http", "https")
            .addAttributes("blockquote", "class", "data-lang", "data-theme", "data-instgrm-captioned",
                    "data-instgrm-permalink", "data-instgrm-version", "style")
            .addAttributes("p", "dir", "lang")
            .addAttributes("div", "class", "style", "dir")
            .addAttributes("span", "class", "style", "dir")
            .addAttributes("a", "class", "style", "href", "target", "rel")
            .addAttributes("img", "class", "style", "width", "height", "alt")
            .preserveRelativeLinks(true)
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
        return Jsoup.clean(input, "http://localhost", RICH_TEXT_SAFELIST).trim();
    }

    public static String trim(String input) {
        return input == null ? null : input.trim();
    }
}
