package com.localhost.gwt.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.utils.CollectionUtils;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Word;
import com.localhost.gwt.shared.model.Translation;

import java.util.List;
import java.util.Map;

/**
 * Created by AlexL on 07.10.2017.
 */
public class WordTable extends FlexTable {
    private static int HEADER_ROW = 0;
    private static int WORD_COLUMN = 0;
    private static int TRANSCRIPTION_COLUMN = 1;
    private static int LISTENING_COLUMN = 2;

    private static final String EMPTY_SECTION_MESSAGE = "No words for this section";
    private static final String EMPTY_RES_MESSAGE = "No results found";

    public WordTable() {
        addStyleName(Constants.Styles.WORD_TABLE);
        GQuery.$(GQuery.document).keydown(new Function() {
            @Override
            public boolean f(Event e) {
                if (!isVisible()) {
                    return true;
                }
                if (e.getShiftKey() && e.getKeyCode() == KeyCodes.KEY_A) {
                    for (int row = 1; row < getRowCount(); row++) {
                        GQuery.$(getWidget(row, TRANSCRIPTION_COLUMN)).click();
                        e.stopPropagation();
                        e.preventDefault();
                    }
                }
                return true;
            }
        });
    }

    public void redraw (List<Word> words, boolean search) {
        removeAllRows();
        if (CollectionUtils.isEmpty(words)) {
            setText(0,0, search ? EMPTY_RES_MESSAGE : EMPTY_SECTION_MESSAGE);
            return;
        }
        setText(HEADER_ROW, WORD_COLUMN, "Word");
        setText(HEADER_ROW, TRANSCRIPTION_COLUMN, "Transcription");
        setText(HEADER_ROW, LISTENING_COLUMN, "Listening");
        int row = 1;
        for (Word word: words) {
            for (Map.Entry<Language, Translation> pair : word.getTranslationsMap().entrySet()) {
                Translation translation = pair.getValue();
                setText(row, WORD_COLUMN, translation.getWord());
                if (translation.getTranscription() != null) {
                    setWidget(row, TRANSCRIPTION_COLUMN, getShowButton(row, translation.getTranscription()));
                }
                setWidget(row, LISTENING_COLUMN, getVoiceBtn(row));
                row++;
            }
        }
    }

    private Button getVoiceBtn(final int row) {
        Button voiceBtn = new Button();
        voiceBtn.addStyleName(Constants.Styles.VOICE_BUTTON);
        voiceBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                speak(getText(row, WORD_COLUMN));
            }
        });
        return voiceBtn;
    }

    private Button getShowButton(final int row, final String transcription) {
        final Button showBtn = new Button();
        showBtn.addStyleName(Constants.Styles.SHOW_BUTTON);
        showBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {

                final Anchor anchor = new Anchor(transcription);
                anchor.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        boolean hasFocus = GQuery.$(anchor).is(":focus");
                        setWidget(row, TRANSCRIPTION_COLUMN, showBtn);
                        if (hasFocus) {
                            showBtn.setFocus(true);
                        }
                    }
                });

                boolean hasFocus = GQuery.$(showBtn).is(":focus");
                setWidget(row, TRANSCRIPTION_COLUMN, anchor);
                if (hasFocus) {
                    anchor.setFocus(true);
                }
            }
        });
        return showBtn;
    }

    private native void speak(String word) /*-{
        var msg = new SpeechSynthesisUtterance(word);
        var voices = window.speechSynthesis.getVoices();
        msg.voice = voices[1]; // Note: some voices don't support altering params
        msg.voiceURI = 'native';
        msg.volume = 1; // 0 to 1
        msg.rate = 0.9; // 0.1 to 10
        msg.pitch = 1; //0 to 2
        speechSynthesis.speak(msg);
    }-*/;
}
