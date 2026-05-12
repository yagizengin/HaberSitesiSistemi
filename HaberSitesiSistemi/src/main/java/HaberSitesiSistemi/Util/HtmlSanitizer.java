package HaberSitesiSistemi.Util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            .addTags("iframe")
            .addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen", "class",
                    "loading", "referrerpolicy")
            .addProtocols("iframe", "src", "http", "https")
            .addAttributes("blockquote", "class", "data-lang", "data-theme", "data-instgrm-captioned",
                    "data-instgrm-permalink", "data-instgrm-version")
            .addAttributes("p", "dir", "lang")
            .addAttributes("div", "class", "dir")
            .addAttributes("span", "class", "dir")
            .addAttributes("a", "class", "href", "target", "rel")
            .addAttributes("img", "class", "width", "height", "alt")
            .preserveRelativeLinks(true)
            .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");

    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile(
            "(?:youtube(?:-nocookie)?\\.com/(?:embed/|watch\\?v=|shorts/)|youtu\\.be/)([A-Za-z0-9_-]{11})");
    private static final Pattern INSTAGRAM_URL_PATTERN = Pattern.compile(
            "^https://(?:www\\.)?instagram\\.com/(?:p|reel|tv)/[A-Za-z0-9_-]+/?(?:\\?.*)?$");
    private static final Pattern TWITTER_URL_PATTERN = Pattern.compile(
            "^https://(?:twitter\\.com|x\\.com)/[A-Za-z0-9_]{1,15}/status/[0-9]+(?:\\?.*)?$");
    private static final Set<String> ALLOWED_EMBED_CLASSES = Set.of("ql-video", "twitter-tweet", "instagram-media");

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
        String cleaned = Jsoup.clean(input, "http://localhost", RICH_TEXT_SAFELIST).trim();
        Document doc = Jsoup.parseBodyFragment(cleaned, "http://localhost");
        hardenEmbeds(doc);
        return doc.body().html().trim();
    }

    public static String trim(String input) {
        return input == null ? null : input.trim();
    }

    private static void hardenEmbeds(Document doc) {
        for (Element iframe : doc.select("iframe")) {
            String youtubeId = extractYoutubeId(iframe.attr("src"));
            if (youtubeId == null) {
                iframe.remove();
                continue;
            }
            iframe.clearAttributes();
            iframe.attr("class", "ql-video");
            iframe.attr("src", "https://www.youtube-nocookie.com/embed/" + youtubeId);
            iframe.attr("width", "560");
            iframe.attr("height", "315");
            iframe.attr("loading", "lazy");
            iframe.attr("referrerpolicy", "strict-origin-when-cross-origin");
            iframe.attr("allow", "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share");
            iframe.attr("allowfullscreen", "");
        }

        for (Element blockquote : doc.select("blockquote")) {
            String className = blockquote.className();
            if (className.contains("twitter-tweet")) {
                if (!hasMatchingLink(blockquote, TWITTER_URL_PATTERN)) {
                    blockquote.unwrap();
                    continue;
                }
                keepOnlyAllowedClasses(blockquote);
            } else if (className.contains("instagram-media")) {
                String permalink = blockquote.attr("data-instgrm-permalink");
                if (!INSTAGRAM_URL_PATTERN.matcher(permalink).matches()) {
                    blockquote.unwrap();
                    continue;
                }
                keepOnlyAllowedClasses(blockquote);
            } else {
                blockquote.removeAttr("class");
            }
        }
    }

    private static String extractYoutubeId(String src) {
        Matcher matcher = YOUTUBE_ID_PATTERN.matcher(src == null ? "" : src);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static boolean hasMatchingLink(Element element, Pattern pattern) {
        for (Element link : element.select("a[href]")) {
            if (pattern.matcher(link.attr("abs:href")).matches()) {
                return true;
            }
        }
        return false;
    }

    private static void keepOnlyAllowedClasses(Element element) {
        Set<String> classes = element.classNames();
        classes.removeIf(className -> !ALLOWED_EMBED_CLASSES.contains(className));
        element.classNames(classes);
        if (classes.isEmpty()) {
            element.removeAttr("class");
        }
    }
}
