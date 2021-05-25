package eu.europeana.api.myapi.service;

/**
 * Google translate + autodetect language
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

/**
 * Translate a query/sentence from a source language to a target language
 */
@Service
public class MultilingualQueryGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(QueryTranslator.class);

    private TranslationService translationService;

    @Autowired
    public MultilingualQueryGenerator(TranslationService translationService) {
        this.translationService = translationService;
        LOG.info("MultilingualQueryGenerator initialised with {} service", translationService);
    }

    public String getMultilingualQuery(String queryString, String targetLanguage, String sourceLanguage) {
        return getMultilingualQuery(new Query(queryString), targetLanguage, sourceLanguage);
    }

    public String getMultilingualQuery(Query query, String targetLanguage, String sourceLanguage) throws IndexOutOfBoundsException {
        String mQuery = null;
        QueryParser qParser = new QueryParser();
        query = qParser.parse(query);
        // TODO creating a new QueryTranslator object seem rather expensive to do for each request
        QueryTranslator queryTranslator = new QueryTranslator(this.translationService);
        String translation = queryTranslator.translate(query,targetLanguage, sourceLanguage);
        mQuery = "(" +  query.getText() + ")" + " OR " + "(" + translation + ")"; //TODO: basic multilingual query
        return  mQuery;
    }

    @PreDestroy
    public void close(){
        if (this.translationService != null) {
            this.translationService.close();
        }
    }

}
