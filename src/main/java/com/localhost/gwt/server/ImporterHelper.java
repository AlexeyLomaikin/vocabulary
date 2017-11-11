package com.localhost.gwt.server;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.ObjectUtils;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Level;
import com.localhost.gwt.shared.model.Translation;
import com.localhost.gwt.shared.model.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by AlexL on 07.11.2017.
 */
public class ImporterHelper {
    private ImporterHelper(){}

    private static final String EMPTY_TRANSCRIPTION = "[]";

    public static void main(String... arg) throws Exception {
        importFile(new HashMap<String, Object>() { {
            put(Constants.FileImport.FILE_NAME, "Phrases2.txt");
            put(Constants.FileImport.IGNORE_LINE_SYMBOLS, Collections.singletonList("//"));
            put(Constants.FileImport.SEPARATOR, "\\s*\\|\\s*");
            put(Constants.FileImport.COLUMNS, new ArrayList<Column>() {
                {
                    add(new Column(ColumnType.WORD, Constants.LanguageIds.ENGLISH));
                    add(new Column(ColumnType.WORD, Constants.LanguageIds.RUSSIAN));
                    add(new Column(ColumnType.TRANSCRIPTION, Constants.LanguageIds.ENGLISH));
                }
            });
            put(Constants.FieldNames.LEVEL_ID, Constants.LevelIds.PRE_INTERMEDIATE);
        }
        });
    }

    @SuppressWarnings("unchecked")
    private static void importFile(Map<String, Object> params) throws IOException {
        String fileName = String.valueOf(params.get(Constants.FileImport.FILE_NAME));
        List<String> ignoredLineSymbols = (List<String>)params.get(Constants.FileImport.IGNORE_LINE_SYMBOLS);
        String separator = (String)params.get(Constants.FileImport.SEPARATOR);
        List<Column> columns = (List<Column>)params.get(Constants.FileImport.COLUMNS);
        Integer levelId = (Integer)params.get(Constants.FieldNames.LEVEL_ID);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            List<Word> words = new ArrayList<Word>();
            while ((line = reader.readLine()) != null) {
                if (!ignoreLine(line, ignoredLineSymbols)) {
                    String[] colValues = line.split(separator);
                    Word nextWord = getNextWord(columns, colValues);
                    nextWord.setLevel(new Level(levelId));
                    words.add(nextWord);
                }
            }
            VocabularyServiceImpl.getInstance().addWords(words);
        }
    }

    private static Word getNextWord(List<Column> columns, String[] columnValues) {
        Word word = new Word();
        for (int i = 0; i < columnValues.length; i++) {
            Column column = columns.get(i);
            Language language = new Language(column.getLangId());
            Translation translation = word.getTranslation(language);
            if (translation == null) {
                translation = new Translation();
            }
            if (column.type == ColumnType.WORD) {
                translation.setWord(columnValues[i].replaceAll("'", "''"));
            } else if (column.type == ColumnType.TRANSCRIPTION && !EMPTY_TRANSCRIPTION.equals(columnValues[i])) {
                translation.setTranscription(columnValues[i].replaceAll("'", "''"));
            }
            word.addTranslation(language, translation);
        }
        return word;
    }

    private static boolean ignoreLine(String line, List<String> ignoreSymbols) {
        if (ObjectUtils.isEmpty(line) || line.matches("\\s+")) {
            return true;
        }
        if (ObjectUtils.isEmpty(ignoreSymbols)) {
            return false;
        }
        for (String ignoreSymbol: ignoreSymbols) {
            if (line.startsWith(ignoreSymbol)) {
                return true;
            }
        }
        return false;
    }

    private enum ColumnType {
        WORD,
        TRANSCRIPTION;
    }

    private static class Column {
        int langId;
        ColumnType type;

        Column(ColumnType type, int langId) {
            this.langId = langId;
            this.type = type;
        }
        public int getLangId() {
            return langId;
        }
        public ColumnType getType() {
            return type;
        }
    }
}
