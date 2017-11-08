package com.localhost.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.localhost.gwt.client.service.VocabularyService;
import com.localhost.gwt.client.service.VocabularyServiceAsync;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.ObjectUtils;
import com.localhost.gwt.shared.PagerItem;
import com.localhost.gwt.shared.ServiceResponse;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Level;

import java.util.List;

/**
 * Created by AlexL on 06.10.2017.
 */
public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

    private VocabularyServiceAsync asyncService = GWT.create(VocabularyService.class);

    private ListBox levelChooser = new ListBox();
    private ListBox langChooser = new ListBox();
    private WordTable wordsTable = new WordTable();
    private PagerPanel pagerPanel = new PagerPanel(new Runnable() {
        @Override
        public void run() {
            getWords();
        }
    });

    private List<Language> languages;
    private List<Level> levels;

    public void onModuleLoad() {
        RootPanel.get().add(createMainPanel());
        RootPanel.get().add(createAddBtn());
        RootPanel.get().add(wordsTable);
        RootPanel.get().add(pagerPanel);
        wordsTable.setVisible(false);
        pagerPanel.setVisible(false);
        asyncService.getLevels(new AsyncCallback<ServiceResponse>() {
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }
            public void onSuccess(ServiceResponse response) {
                EntryPoint.this.levels = response.getLevels();
                initLevelList(levels);
            }
        });
        asyncService.getLanguages(new AsyncCallback<ServiceResponse>() {
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            public void onSuccess(ServiceResponse response) {
                EntryPoint.this.languages = response.getLanguages();
                initLanguageListBox(languages);
            }
        });
    }

    private Button createAddBtn() {
        final Button addWordBtn = new Button("Add new word");
        addWordBtn.addStyleName(Constants.Styles.ADD_BUTTON);
        addWordBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                final WordCreationPopup popup = new WordCreationPopup(levels, languages);
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback(){
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = addWordBtn.getAbsoluteLeft() + 100;
                        int top = addWordBtn.getAbsoluteTop();
                        popup.setPopupPosition(left, top);
                    }
                });
            }
        });
        return addWordBtn;
    }

    private void initLanguageListBox(List<Language> languages) {
        for (Language language: languages) {
            langChooser.addItem(language.getName(), "" + language.getId());
        }
        langChooser.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent changeEvent) {
                getWords();
            }
        });
    }

    private Panel createMainPanel() {
        HorizontalPanel mainPanel = new HorizontalPanel();
        mainPanel.addStyleName(Constants.Styles.MAIN_LIST_PANEL);
        mainPanel.add(createListBoxPanel(levelChooser, "Your language level"));
        mainPanel.add(createListBoxPanel(langChooser,  "Language"));
        return mainPanel;
    }

    private Panel createListBoxPanel(ListBox listBox, String labelText) {
        VerticalPanel listBoxPanel = new VerticalPanel();
        listBoxPanel.addStyleName(Constants.Styles.LIST_PANEL);

        Label label = new Label(labelText);
        label.addStyleName(Constants.Styles.LIST_LABEL);
        listBoxPanel.add(label);

        listBox.clear();
        listBox.addItem("");
        listBox.addStyleName(Constants.Styles.LIST_CHOOSER);

        listBoxPanel.add(listBox);
        return listBoxPanel;
    }

    private void initLevelList(List<Level> levels) {
        for (Level level: levels) {
            levelChooser.addItem(level.getName(), String.valueOf(level.getId()));
        }
        levelChooser.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent changeEvent) {
                pagerPanel.setPage(1);
            }
        });
    }

    private void getWords() {
        PagerItem pagerItem = new PagerItem(pagerPanel.getPageNumber(), pagerPanel.getLimit());
        asyncService.getWords(levelChooser.getSelectedValue(), langChooser.getSelectedValue(), pagerItem,
                new AsyncCallback<ServiceResponse>() {
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getMessage());
                    }
                    public void onSuccess(ServiceResponse response) {
                        wordsTable.redraw(response.getWords(), ObjectUtils.isEmpty(langChooser.getSelectedValue()) ||
                                ObjectUtils.isEmpty(levelChooser.getSelectedValue()));
                        pagerPanel.setVisible(!ObjectUtils.isEmpty(response.getWords()));
                        String tableSizeString = response.getParam(Constants.TABLE_SIZE);
                        if (tableSizeString != null) {
                            pagerPanel.setTableSize(Integer.valueOf(tableSizeString));
                        }
                    }
                });
    }
}
