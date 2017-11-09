package com.localhost.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.localhost.gwt.shared.*;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Word;
import com.localhost.gwt.client.service.VocabularyService;
import com.localhost.gwt.shared.model.Level;
import com.localhost.gwt.shared.model.Translation;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Created by AlexL on 06.10.2017.
 */
public class VocabularyServiceImpl extends RemoteServiceServlet implements VocabularyService {
    private static final String driverName = "org.sqlite.JDBC";
    private static final String connectionString = "jdbc:sqlite::resource:/vocabulary.s3db";
    private static final String fullConnectionString = ("jdbc:sqlite:" + new File(".").getAbsolutePath() +
            "/src/main/resources/vocabulary.s3db").replaceAll("\\\\", "/");
    private static final String GET_WORDS_SQL =
                    "select w.*, l.lang_name, l.lang_short_name from\n" +
                    "  words w, languages l, words_levels wl\n" +
                    "where w.langId = l.langId\n" +
                    "and wl.lvlId = ?\n" +
                    "and w.langId = ?\n" +
                    "and wl.wordId = w.wordId\n" +
                    "order by w.wordId desc\n";
    private static final String GET_WORDS_COUNT_WITHOUT_PAGING =
            "select count(*) from (" + GET_WORDS_SQL + ")";
    private static final String GET_ID_FOR_WORD_SQL =
            "with max_lng_Id as (" +
                    "select max(langId) + 1 as lngId from languages)\n" +
                    "select \n" +
                    "case max(wordId)\n" +
                    "  when null then (select lngId from max_lng_id)\n" +
                    "  else max((select lngId from max_lng_id), max(wordId) + 1)\n" +
                    "end as newId\n" +
                    "from words";

    private static Connection connection;

    private static VocabularyServiceImpl INSTANCE = new VocabularyServiceImpl();
    public static VocabularyServiceImpl getInstance() {
        return INSTANCE;
    }

    public ServiceResponse getLevels() throws SharedRuntimeException {
        try {

            Dao<Level, String> dao = DaoManager.createDao(new JdbcConnectionSource(connectionString), Level.class);
            ServiceResponse response = new ServiceResponse();
            response.setLevels(dao.queryForAll());
            return response;
        } catch (SQLException ex) {
            throw new SharedRuntimeException(createTraceString(ex));
        }
    }

    public void addWords(List<Word> words) throws SharedRuntimeException {
        try {
            words = getIdsForWords(words);
            updateWordsTable(words);
            updateLevelsTable(words);
        } catch (Exception e) {
            throw new SharedRuntimeException(e.getMessage());
        }
    }

    private void updateWordsTable(List<Word> words) throws Exception{
        StringBuilder wordQuery = new StringBuilder("WITH r(lngId, word, wordId, tr) AS( \n");
        for (Word word: words) {
            for (Language language: word.getTranslationsMap().keySet()) {
                Translation translation = word.getTranslation(language);
                wordQuery.append("SELECT ");
                wordQuery.append(language.getId());
                wordQuery.append(", ");
                wordQuery.append("'" + translation.getWord() + "'");
                wordQuery.append(", ");
                wordQuery.append(word.getWordId());
                wordQuery.append(", ");
                wordQuery.append((translation.getTranscription() == null) ? null :
                        "'" + translation.getTranscription() + "'");
                wordQuery.append(" FROM dual \n");
                wordQuery.append("UNION ALL \n");
            }
        }
        wordQuery.append("SELECT null, null, null, null FROM dual");
        wordQuery.append(")\nINSERT INTO words \nSELECT * from r WHERE wordId IS NOT NULL");
        wordQuery.append("");
        executeUpdate(wordQuery.toString());
    }

    private void updateLevelsTable(List<Word> words) throws Exception {
        StringBuilder levelsQuery = new StringBuilder("WITH l(lvlId, wordId) AS (\n");
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            levelsQuery.append("SELECT ");
            levelsQuery.append(word.getLevel().getId());
            levelsQuery.append(", ");
            levelsQuery.append(word.getWordId());
            levelsQuery.append(" FROM dual \n");
            if (i != words.size() - 1) {
                levelsQuery.append("UNION ALL \n");
            }
        }
        levelsQuery.append(") INSERT INTO words_levels SELECT * FROM l");
        executeUpdate(levelsQuery.toString());
    }

    private void executeUpdate(String sql) throws Exception {
        initConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.close();
    }

    private List<Word> getIdsForWords(List<Word> words) throws Exception {
        int nextId = getNewWordId() - 1;
        for (Word word: words) {
            word.setWordId(++nextId);
        }
        return words;
    }

    public ServiceResponse getWords(String levelId, String langId, PagerItem pagerItem) throws SharedRuntimeException {
        ServiceResponse response = new ServiceResponse();
        if (ObjectUtils.isEmpty(langId) || ObjectUtils.isEmpty(levelId)) {
            return response;
        }
        PreparedStatement statement;
        Map<String, Word> wordMap = new LinkedHashMap<String, Word>();
        try {
            initConnection();
            int pageNumber = pagerItem.getPageNum();
            int pageLimit = pagerItem.getPageLimit();
            int offset = (pageNumber - 1) * pageLimit;
            String sql = GET_WORDS_SQL + "limit " + pageLimit + " offset " + offset;
            statement = connection.prepareStatement(sql);
            statement.setString(1, levelId);
            statement.setString(2, langId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String wordId = rs.getString(Constants.FieldNames.WORD_ID);
                Word word = wordMap.get(wordId);
                if (word == null) {
                    word = new Word();
                }
                Language language = new Language(rs.getInt(Constants.FieldNames.LANGUAGE_ID),
                        rs.getString(Constants.FieldNames.LANGUAGE_NAME), rs.getString(Constants.FieldNames.LANGUAGE_SHORT_NAME));
                Translation translation = new Translation(rs.getString(Constants.FieldNames.WORD),
                        rs.getString(Constants.FieldNames.TRANSCRIPTION));
                word.addTranslation(language, translation);
                wordMap.put(wordId, word);
            }
            response.setWords(new ArrayList<Word>(wordMap.values()));

            statement = connection.prepareStatement(GET_WORDS_COUNT_WITHOUT_PAGING);
            statement.setString(1, levelId);
            statement.setString(2, langId);
            response.addParam(Constants.TABLE_SIZE, statement.executeQuery().getString(1));
            return response;
        } catch (Exception e) {
            throw new SharedRuntimeException(e.getMessage());
        }
    }

    public ServiceResponse getLanguages() throws SharedRuntimeException {
        try {
            ServiceResponse response = new ServiceResponse();
            Dao<Language, String> dao = DaoManager.createDao(new JdbcConnectionSource(connectionString), Language.class);
            response.setLanguages(dao.queryForAll());
            return response;
        }catch (Exception ex) {
            throw new SharedRuntimeException(ex.getMessage());
        }
    }

    private int getNewWordId() throws Exception {
        initConnection();
        ResultSet rs = connection.createStatement().executeQuery(GET_ID_FOR_WORD_SQL);
        return rs.getInt(1);
    }

    private void initConnection() throws Exception {
        if (connection != null) {
            return;
        }
        Class.forName(driverName);
        try {
            connection = DriverManager.getConnection(connectionString);
        }catch (SQLException ex) {
            connection = DriverManager.getConnection(fullConnectionString);
        }
    }

    private String createTraceString(Exception ex) {
        String s = "";
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        for (StackTraceElement stackTraceElement: stackTraceElements) {
            s += stackTraceElement.getClassName() + stackTraceElement.getMethodName() + "\n";
        }
        return s;
    }
}
