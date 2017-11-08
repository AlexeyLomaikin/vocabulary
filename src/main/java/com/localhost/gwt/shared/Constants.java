package com.localhost.gwt.shared;

/**
 * Created by AlexL on 21.10.2017.
 */
public interface Constants {
    interface FileImport {
        String SEPARATOR = "separator";
        String IGNORE_LINE_SYMBOLS = "ignoreSymbols";
        String FILE_NAME = "fileName";
        String COLUMNS = "columns";
    }

    interface FieldNames {
        String WORD_ID = "wordId";
        String WORD = "word";
        String TRANSCRIPTION = "transcription";

        String LEVEL_ID = "lvlId";

        String LANGUAGE_ID = "langId";
        String LANGUAGE_NAME = "lang_name";
        String LANGUAGE_SHORT_NAME = "lang_short_name";
    }

    interface Styles {
        String MAIN_LIST_PANEL = "main_list_panel";
        String ADD_BUTTON = "add_button";
        String SHOW_BUTTON = "show_btn";
        String VOICE_BUTTON = "voice_btn";
        String LIST_CHOOSER = "list_chooser";
        String LIST_LABEL = "list_label";
        String WORD_TABLE = "word_table";
        String CREATE_WORD_TABLE = "create_word_table";
        String LIST_PANEL = "list_panel";
        String PAGER_PANEL = "pager_panel";
    }

    interface LevelIds {
        int BEGINNER = 1;
        int ELEMENTARY = 2;
        int PRE_INTERMEDIATE = 3;
    }

    interface LanguageIds {
        int ENGLISH = 1;
        int RUSSIAN = 2;
    }

    String TABLE_SIZE = "tableSize";
}
