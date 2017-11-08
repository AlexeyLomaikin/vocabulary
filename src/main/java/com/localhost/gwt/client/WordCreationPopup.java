package com.localhost.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Level;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by AlexL on 07.10.2017.
 */
public class WordCreationPopup extends PopupPanel {
    private List<Level> levels;
    private List<Language> languages;
    private FlexTable paramTable = new FlexTable();

    private LinkedList<Integer> rowIdxs = new LinkedList<Integer>();

    public WordCreationPopup(List<Level> levels, List<Language> languages) {
        super(true, true);
        this.levels = levels;
        this.languages = languages;
    }

    @Override
    public void show() {
        init();
        super.show();
    }

    private void init() {
        VerticalPanel tablePanel = new VerticalPanel();
        paramTable.addStyleName(Constants.Styles.WORD_TABLE);
        paramTable.addStyleName(Constants.Styles.CREATE_WORD_TABLE);
        paramTable.setText(0, 0, "Word Number");
        paramTable.setText(0, 1, "Lang");
        paramTable.setText(0, 2, "Word");
        paramTable.setText(0, 3, "Transcription");
        paramTable.setText(0, 4, "Level");
        createNewWordRow();
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button addWord = new Button("Add word");
        addWord.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                createNewWordRow();
            }
        });
        Button deleteWord = new Button("Delete word");
        deleteWord.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                int wordCount = rowIdxs.size();
                if (wordCount > 1) {
                    int lastWordRowIdx = rowIdxs.getLast();
                    int rowsCount = paramTable.getRowCount();
                    for (int i = lastWordRowIdx; i < rowsCount; i++) {
                        paramTable.removeRow(paramTable.getRowCount() - 1);
                    }
                    rowIdxs.removeLast();
                }
            }
        });
        buttonPanel.add(addWord);
        buttonPanel.add(deleteWord);
        tablePanel.add(paramTable);
        tablePanel.add(buttonPanel);
        add(tablePanel);
    }

    private void createNewLanguageRow(int currentRowIdx) {
        int nextRowIdx = currentRowIdx + 1;
        if (nextRowIdx <= paramTable.getRowCount() - 1) {
            paramTable.insertRow(nextRowIdx);
        }
        ListBox langListBox = new ListBox();
        for (Language language: languages) {
            langListBox.addItem(language.getName(), "" + language.getId());
        }
        HorizontalPanel langPanel = new HorizontalPanel();
        Button deleteLanguage = new Button("-");
        deleteLanguage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                int lastWordRowIdx = rowIdxs.getLast();
                int lastRowIdx = paramTable.getRowCount() - 1;
                if (lastRowIdx != lastWordRowIdx) {
                    paramTable.removeRow(lastRowIdx);
                }
            }
        });
        langPanel.add(langListBox);
        langPanel.add(getAddLangButton(nextRowIdx));
        langPanel.add(deleteLanguage);
        paramTable.setWidget(nextRowIdx, 1, langPanel);
        paramTable.setWidget(nextRowIdx, 2, new TextBox());
        paramTable.setWidget(nextRowIdx, 3, new TextBox());
        ListBox levelList = new ListBox();
        for (Level level: levels) {
            levelList.addItem(level.getName(), String.valueOf(level.getId()));
        }
        paramTable.setWidget(nextRowIdx, 4, levelList);
    }

    private void createNewWordRow() {
        int rowCount = paramTable.getRowCount();
        if (!rowIdxs.isEmpty()) {
            int lastWordRow = rowIdxs.getLast();
            paramTable.getFlexCellFormatter().setRowSpan(lastWordRow, 0, rowCount - lastWordRow);
            paramTable.setText(lastWordRow, 0, "" + (rowIdxs.size() - 1));
        }
        rowIdxs.add(rowCount);
        createNewLanguageRow(rowCount - 1);
    }

    private Button getAddLangButton(final int curRowIdx) {
        Button addLanguage = new Button("+");
        addLanguage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                createNewLanguageRow(curRowIdx);
            }
        });
        return addLanguage;
    }
}
