package searchengine.service.search;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchPage;
import searchengine.dto.search.SearchSentence;
import searchengine.dto.search.SearchSnippet;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.service.text.LemmaProcessor;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SnippetGenerator {
    private static final String regexWordAndSpaceAllow = "[^a-zA-Zа-яА-ЯёЁ\\s]+";
    private static final Logger log = LoggerFactory.getLogger(SnippetGenerator.class);
    private final CacheManager cacheManager;
    private final LemmaProcessor lemmaProcessor;

    public List<SearchSnippet> generateSnippetsResponse(List<SearchPage> searchPageList, Integer offset, Integer limit) {
        if (searchPageList.size() <= offset + limit) {
            cacheManager.cleanCache();
            return shapingSnippets(searchPageList);
        }
        List<SearchPage> searchPagesPartFirst = searchPageList.subList(offset, offset + limit);
        List<SearchPage> searchPagesPartNext = searchPageList.subList(offset + limit + 1, searchPageList.size());
        List<SearchSnippet> generatedSnippets = shapingSnippets(searchPagesPartFirst);
        cacheManager.saveGeneratedSnippets(generatedSnippets);
        cacheManager.saveSearchPagesPartNext(searchPagesPartNext);
        return generatedSnippets;
    }

    public List<SearchSnippet> generateSnippetsResponseUsingTheCache(Integer limit) {
        List<SearchSnippet> generatedSnippets = cacheManager.getGeneratedSnippets();
        List<SearchPage> searchPagesPartNew = cacheManager.getSearchPagesPartNext();

        if (searchPagesPartNew.size() <= limit) {
            generatedSnippets.addAll(shapingSnippets(searchPagesPartNew));
            return generatedSnippets;
        }

        List<SearchPage> searchPagesPartFirst = searchPagesPartNew.subList(0, limit);
        List<SearchPage> searchPagesPartNext = searchPagesPartNew.subList(limit + 1, searchPagesPartNew.size());
        generatedSnippets.addAll(shapingSnippets(searchPagesPartFirst));
        cacheManager.saveGeneratedSnippets(shapingSnippets(searchPagesPartFirst));
        cacheManager.saveSearchPagesPartNext(searchPagesPartNext);

        log.info("Cache was used");
        return generatedSnippets;
    }

    private List<SearchSnippet> shapingSnippets(List<SearchPage> pageWithIndexesList) {
        List<SearchSnippet> searchSnippetList = new ArrayList<>();
        for (SearchPage searchPage : pageWithIndexesList) {
            String siteName = searchPage.getPageEntity().getSite().getName();
            String site = searchPage.getPageEntity().getSite().getUrl();
            String uri = searchPage.getPageEntity().getPath();
            String title = generateTitle(searchPage.getPageEntity());
            String snippet = generateContentSnippet(searchPage);
            double relevance = searchPage.getRelativeRelevance();
            SearchSnippet snippetResponse = new SearchSnippet();
            snippetResponse.setSiteName(siteName);
            snippetResponse.setSite(site);
            snippetResponse.setUri(uri);
            snippetResponse.setTitle(title);
            snippetResponse.setSnippet(snippet);
            snippetResponse.setRelevance(relevance);
            searchSnippetList.add(snippetResponse);
        }
        return searchSnippetList;
    }
    private String generateContentSnippet(SearchPage searchPage) {
        List<String> keywords = searchPage.getIndexEntityList().stream()
                .map(IndexEntity::getLemma)
                .map(LemmaEntity::getLemma)
                .toList();
        String bodyHtml = searchPage.getPageEntity().getContent();
        String content = Jsoup.parse(bodyHtml).body().text();
        SearchSentence theTopSnippet = getTopSentenceByKeywords(content, keywords);
        String snippet = theTopSnippet.getSnippet();
        if (snippet.length() >= 260) {
            return snippet.substring(0, 260) + "...";
        }
        return snippet.substring(0, snippet.length() - 1) + " ";
    }

    /**
     * GET TOP SENTENCE BY KEYWORDS
     *
     * <p>The method select a sentence from that contains as many keywords as possible</p>
     *
     * @param content content of HTML pages
     * @param keywords lemmas
     * @return SearchSentence with actually sentence
     */
    private SearchSentence getTopSentenceByKeywords(String content, List<String> keywords) {
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.forLanguageTag("ru"));
        int start = boundary.first();
        SearchSentence theTopSnippet = null;
        boundary.setText(content);
        int maxRank = 0;

        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            String sentence = content.substring(start, end);

            String[] words = getOnlyWordsFromText(sentence);
            List<String> foundSearchWords = findByKeywords(words, keywords);
            String snippetWithBoldKeywords = makeSearchWordsBoldByKeywords(sentence, foundSearchWords);

            SearchSentence searchSentence = new SearchSentence();
            searchSentence.setRank(foundSearchWords.size());
            searchSentence.setSnippet(snippetWithBoldKeywords);

            if (searchSentence.getRank() > maxRank) {
                maxRank = searchSentence.getRank();
                theTopSnippet = searchSentence;
            }
        }

        if (theTopSnippet == null) {
            theTopSnippet = new SearchSentence();
            theTopSnippet.setSnippet(content);
        }
        return theTopSnippet;
    }

    private String[] getOnlyWordsFromText(String text) {
        return text.replaceAll(regexWordAndSpaceAllow, " ").split(" ");
    }

    private List<String> findByKeywords(String[] words, List<String> keyWords) {
        List<String> foundSearchWords = new ArrayList<>();
        for (String word : words) {
            List<String> normalizedWords = lemmaProcessor.getAllNormalizedForms(word);
            for (String normalWords : normalizedWords) {
                if (keyWords.contains(normalWords)) {
                    foundSearchWords.add(word);
                }
            }
        }
        return foundSearchWords;
    }

    private String generateTitle(PageEntity pageEntity) {
        String htmlCode = pageEntity.getContent();
        String title = Jsoup.parse(htmlCode).title();
        if (title.length() >= 70) {
            title = title.substring(0, 70) + "...";
        }
        return title;
    }

    private String makeSearchWordsBoldByKeywords(String text, List<String> keywords) {
        for (String searchWord : keywords) {
            text = text.replaceAll(searchWord, "<b>" + searchWord + "</b>");
        }
        return text;
    }
}
