package eu.europeana.api.myapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(QueryTranslator.class);
    private static final String FIRST_WORD_REGEX = "^\\s*\\S+";
    private static final Pattern FIRST_WORD_PATTERN = Pattern.compile(FIRST_WORD_REGEX);

    private TranslationService translationService;

    public QueryTranslator(TranslationService translationService){
        this.translationService = translationService;
    }

    private String translate(String text, String targetLanguage, String sourceLanguage, boolean enclose){
        StringBuilder sb =  new StringBuilder();
        String toTranslate = text.trim();
        if (!toTranslate.isEmpty()) {
            String translation;
            long start = System.nanoTime(); //DEBUG
            if (sourceLanguage == null) {
                translation = this.translationService.translate(toTranslate, targetLanguage);
            } else {
                translation = this.translationService.translate(toTranslate, targetLanguage, sourceLanguage);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("<TRANSLATION> text: {} time: {}", text.replaceAll("\t", " "), (System.nanoTime() - start) / 1000000);
            }
            if (text.startsWith(" ")) {
                sb.append(" ");
            }
            if (enclose) {
                sb.append("(").append(translation).append(")");
            } else {
                sb.append(translation);
            }
            if (text.endsWith(" ")) {
                sb.append(" ");
            }
        } else {
            return text;
        }
        return sb.toString();
    }

    public String translate(Query query, String targetLanguage, String sourceLanguage){
        QueryPartType previous = null;
        StringBuilder outputQuery = new StringBuilder();
        for (QueryPart queryPart : query.getQueryPartList()) {
            String originalText = queryPart.getText();
            QueryPartType type = queryPart.getPartType();
            if (type == QueryPartType.TEXT && previous == QueryPartType.UNARY_OPERATOR) {

                Matcher matcher = FIRST_WORD_PATTERN.matcher(originalText);
                if (matcher.find()) {
                    String firstWord = originalText.substring(0,matcher.end());
                    outputQuery.append(translate(firstWord, targetLanguage, sourceLanguage, true)); //translation first word unary operator in brackets
                    String rest = originalText.substring(matcher.end());
                    outputQuery.append(translate(rest, targetLanguage, sourceLanguage,false));
                } else {
                    outputQuery.append(translate(originalText, targetLanguage, sourceLanguage,false));
                }

            } else if (type == QueryPartType.QUOTED || type == QueryPartType.TEXT) {
                outputQuery.append(translate(originalText, targetLanguage, sourceLanguage,false));
            } else {
                outputQuery.append(originalText);
            }
            previous = type;
        }
    return outputQuery.toString();
    }

    public List<String> getLanguages(Query query){
        QueryParser qParser = new QueryParser();
        query = qParser.parse(query);
        QueryLanguageDetector queryLanguageDetector = new QueryLanguageDetector(this.translationService);
        return queryLanguageDetector.detectLanguages(query);
    }

}