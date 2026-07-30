package br.com.oficina.adapters.in.web.mapper;

import java.util.regex.Pattern;

final class TextSecuritySanitizer {

    private static final Pattern SUSPICIOUS_CONTENT = Pattern.compile(
        "<!--|-->|<\\?|\\?>|<!\\[CDATA|\\]\\]>|<[^>]+>|\\$\\{|#\\{|\\{\\{|\\}\\}|<%|%>|xsl:|javascript:",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private TextSecuritySanitizer() {
    }

    static String sanitize(String value) {
        if (value == null || !SUSPICIOUS_CONTENT.matcher(value).find()) {
            return value;
        }
        return "[conteudo removido]";
    }
}
