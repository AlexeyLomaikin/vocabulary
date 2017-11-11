package com.localhost.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.localhost.gwt.client.service.VocabularyService;
import com.localhost.gwt.client.service.VocabularyServiceAsync;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.ObjectUtils;
import com.localhost.gwt.shared.transport.PagerItem;
import com.localhost.gwt.shared.transport.ServiceRequest;
import com.localhost.gwt.shared.transport.ServiceResponse;
import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Level;

import java.util.List;
import java.util.Map;

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
    private TextBox searchField;

    private List<Language> languages;
    private List<Level> levels;
    private String lastSearchKey = "";

    public void onModuleLoad() {
        RootPanel.get().add(createMainPanel());
        RootPanel.get().add(createAddBtn());
        RootPanel.get().add(searchField = createSearchField());
        RootPanel.get().add(wordsTable);
        RootPanel.get().add(pagerPanel);
        wordsTable.setVisible(false);
        pagerPanel.setVisible(false);
        asyncService.getLevels(new AsyncCallback<ServiceResponse>() {
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }
            public void onSuccess(ServiceResponse response) {
                levels = response.getLevels();
                for (Level level: levels) {
                    levelChooser.addItem(level.getName(), String.valueOf(level.getId()));
                }
                levelChooser.addChangeHandler(new ChangeHandler() {
                    public void onChange(ChangeEvent changeEvent) {
                        pagerPanel.setPage(1);
                    }
                });
            }
        });
        asyncService.getLanguages(new AsyncCallback<ServiceResponse>() {
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }
            public void onSuccess(ServiceResponse response) {
                languages = response.getLanguages();
                for (Language language: languages) {
                    langChooser.addItem(language.getName(), "" + language.getId());
                }
                langChooser.addChangeHandler(new ChangeHandler() {
                    public void onChange(ChangeEvent changeEvent) {
                        getWords();
                    }
                });
            }
        });
    }

    private TextBox createSearchField() {
        final TextBox searchField = new TextBox();
        searchField.addStyleName(Constants.Styles.SEARCH_FIELD);
        searchField.setVisible(false);
        GQuery.$(searchField).on("input", new Function() {
            @Override
            public void f(Element e) {
                String searchKey = searchField.getValue();
                if (ObjectUtils.isEmpty(searchKey) && !ObjectUtils.isEmpty(lastSearchKey)) {
                    lastSearchKey = "";
                    pagerPanel.setPage(1);
                }
            }
        });
        searchField.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    String searchKey = searchField.getValue();
                    if (!lastSearchKey.equals(searchKey)) {
                        lastSearchKey = searchKey;
                        pagerPanel.setPage(1);
                    }
                }
            }
        });
        searchField.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent blurEvent) {
                String searchKey = searchField.getValue();
                if (!lastSearchKey.equals(searchKey)) {
                    lastSearchKey = searchKey;
                    pagerPanel.setPage(1);
                }
            }
        });
        return searchField;
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

    private void getWords() {
        String levelId = levelChooser.getSelectedValue();
        String landId = langChooser.getSelectedValue();
        if (ObjectUtils.isEmpty(levelId) || ObjectUtils.isEmpty(landId)) {
            wordsTable.setVisible(false);
            pagerPanel.setVisible(false);
            searchField.setVisible(false);
            return;
        }
        String searchKey = searchField.getValue();
        PagerItem pagerItem = new PagerItem(pagerPanel.getCurrentPage(), pagerPanel.getLimit());

        final ServiceRequest request = initRequest(levelId, landId, pagerItem, searchKey, null);
        asyncService.getWords(request, new AsyncCallback<ServiceResponse>() {
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }
            public void onSuccess(ServiceResponse response) {
                searchField.setVisible(!ObjectUtils.isEmpty(request.getSearchKey()) ||
                        !ObjectUtils.isEmpty(response.getWords()));
                pagerPanel.setVisible(!ObjectUtils.isEmpty(response.getWords()));
                wordsTable.setVisible(true);
                wordsTable.redraw(response.getWords());
                String tableSizeString = response.getParam(Constants.TABLE_SIZE);
                if (tableSizeString != null) {
                    pagerPanel.setTableSize(Integer.valueOf(tableSizeString));
                }
            }
        });
    }

    private ServiceRequest initRequest(String levelId, String langId, PagerItem pagerItem,
                                       String searchKey, Map<String, String> params) {
        ServiceRequest request = new ServiceRequest();
        request.setLevelId(levelId);
        request.setLangId(langId);
        request.setPagerItem(pagerItem);
        request.setSearchKey(searchKey);
        if (params != null) {
            request.setParams(params);
        }
        return request;
    }
}
