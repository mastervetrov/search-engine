package searchengine.service.text;

import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class LemmaProcessor {

    private RussianLuceneMorphology ruMorphology;
    private EnglishLuceneMorphology euMorphology;

    public LemmaProcessor() {
        try {
            this.euMorphology = new EnglishLuceneMorphology();
            this.ruMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTitleAndBody(Document doc) {
        return Jsoup.parse(doc.title()).text() + " " + Jsoup.parse(doc.body().text()).text();
    }
    public HashMap<String, Integer> extractLemmasAndRank(Document doc) {
        String txt = getTitleAndBody(doc);
        HashMap<String, Integer> allLemmas = new HashMap<>();
        String[] ruWords = getRuWords(txt);
        String[] euWords = getEuWords(txt);
        HashMap<String, Integer> ruLemmas = getLemmasAndRankRuWords(ruWords);
        allLemmas.putAll(ruLemmas);
        if (euWords.length > 0) {
            HashMap<String, Integer> euLemmas = getLemmasAndRankEuWords(euWords);
            allLemmas.putAll(euLemmas);
        }
        return allLemmas;
    }

    public HashSet<String> extractLemmas(String txt) {
        HashSet<String> allLemmas = new HashSet<>();
        String[] ruWords = getRuWords(txt);
        String[] euWords = getEuWords(txt);
        allLemmas.addAll(getLemmasRuWords(ruWords));
        if (euWords.length > 0) {
            allLemmas.addAll(getLemmasEuWords(euWords));
        }

        for (String str : allLemmas) {
            System.out.println("Ключевое слово: " + str);
        }
        return allLemmas;
    }

    public List<String> getAllNormalizedForms(String word) {
        String euWord = getEuWord(word);
        String ruWord = getRuWord(word);
        if (!ruWord.isEmpty()) {
            return ruMorphology.getNormalForms(ruWord);
        }
        if (!euWord.isEmpty()) {
            return euMorphology.getNormalForms(euWord);
        }
        List<String> list = new ArrayList<>();
        list.add(word);
        return list;
    }

    private String getEuWord(String someWord) {
        return someWord.toLowerCase().replaceAll("[^a-z]", " ").replaceAll("\\s+", "");
    }

    private String getRuWord(String someWord) {
        return someWord.toLowerCase().replaceAll("[^а-яё]", " ").replaceAll("\\s+", "");
    }

    private HashSet<String> getLemmasRuWords(String[] ruWords) {
        HashSet<String> ruLemmas = new HashSet<>();
        for (String oneWord : ruWords) {
            boolean wordAllow = true;
            List<String> wordTypeList = ruMorphology.getMorphInfo(oneWord);
            for (String oneWordType : wordTypeList) {
                if (oneWordType.contains("ПРЕДЛ") || oneWordType.contains(" ЧАСТ") || oneWordType.contains("СОЮЗ") || oneWordType.contains("МЕЖД")) {
                    wordAllow = false;
                    break;
                }
            }
            if (wordAllow) {
                List<String> wordBaseForms = ruMorphology.getNormalForms(oneWord);
                ruLemmas.addAll(wordBaseForms);
            }
        }
        return ruLemmas;
    }

    private HashSet<String> getLemmasEuWords(String[] euWords) {
        HashSet<String> euLemmas = new HashSet<>();
        for (String oneWord : euWords) {
            boolean wordAllow = true;
            List<String> wordTypeList = euMorphology.getMorphInfo(oneWord);
            for (String str : wordTypeList) {
            }
            for (String oneWordType : wordTypeList) {
                if (oneWordType.contains("PN") || oneWordType.contains("ARTICLE")) {
                    wordAllow = false;
                    break;
                }
            }
            if (wordAllow) {
                List<String> wordBaseForms = euMorphology.getNormalForms(oneWord);
                euLemmas.addAll(wordBaseForms);
            }
        }
        return euLemmas;
    }

    private String[] getRuWords(String input) {
        String[] array = input.toLowerCase().replaceAll("[^а-яё]", " ").replaceAll("\\s+", " ").split(" ");
        return Arrays.stream(array)
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);
    }

    private HashMap<String, Integer> getLemmasAndRankRuWords(String[] ruWords) {
        HashMap<String, Integer> ruLemmas = new HashMap<>();
        for (String oneWord : ruWords) {
            boolean wordAllow = true;
            List<String> wordTypeList = ruMorphology.getMorphInfo(oneWord);
            for (String oneWordType : wordTypeList) {
                if (oneWordType.contains("ПРЕДЛ") || oneWordType.contains(" ЧАСТ") || oneWordType.contains("СОЮЗ") || oneWordType.contains("МЕЖД")) {
                    wordAllow = false;
                    break;
                }
            }
            if (wordAllow) {
                List<String> wordBaseForms = ruMorphology.getNormalForms(oneWord);
                for (String str : wordBaseForms) {
                    if (ruLemmas.containsKey(str)) {
                        ruLemmas.put(str, ruLemmas.get(str) + 1);
                    } else {
                        ruLemmas.put(str, 1);
                    }
                }
            }
        }
        return ruLemmas;
    }

    private HashMap<String, Integer> getLemmasAndRankEuWords(String[] ruWords) {
        HashMap<String, Integer> ruLemmas = new HashMap<>();
        for (String oneWord : ruWords) {
            boolean wordAllow = true;
            List<String> wordTypeList = euMorphology.getMorphInfo(oneWord);
            for (String oneWordType : wordTypeList) {
                if (oneWordType.contains("PN") || oneWordType.contains("ARTICLE")) {
                    wordAllow = false;
                    break;
                }
            }
            if (wordAllow) {
                List<String> wordBaseForms = euMorphology.getNormalForms(oneWord);
                for (String str : wordBaseForms) {
                    if (ruLemmas.containsKey(str)) {
                        ruLemmas.put(str, ruLemmas.get(str) + 1);
                    } else {
                        ruLemmas.put(str, 1);
                    }
                }
            }
        }
        return ruLemmas;
    }

    private String[] getEuWords(String input) {
        String[] array = input.toLowerCase().replaceAll("[^a-z]", " ").replaceAll("\\s+", " ").split(" ");
        return Arrays.stream(array)
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);
    }
}
