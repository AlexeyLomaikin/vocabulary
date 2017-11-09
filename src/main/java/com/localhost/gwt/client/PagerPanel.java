package com.localhost.gwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.localhost.gwt.shared.Constants;

/**
 * Created by AlexL on 08.11.2017.
 */
public class PagerPanel extends HorizontalPanel {
    private ListBox pageNumChooser;
    private TextBox pageNumber;
    private Button prevBtn;
    private Button nextBtn;

    private double tableSize;
    private Runnable onChange;
    private final static String[] AVAILABLE_LIMITS = {"5", "10", "25", "50"};

    public PagerPanel(final Runnable onChange) {
        this.onChange = onChange;
        pageNumChooser = new ListBox();
        pageNumChooser.clear();
        for (int i = 0; i < AVAILABLE_LIMITS.length; i++) {
            pageNumChooser.addItem(AVAILABLE_LIMITS[i], AVAILABLE_LIMITS[i]);
        }
        pageNumChooser.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                setPage(1);
            }
        });

        pageNumber = new TextBox();
        pageNumber.setTitle("Page");
        pageNumber.setEnabled(false);
        pageNumber.setText("1");
        pageNumber.getElement().getStyle().setWidth(20, Style.Unit.PX);

        prevBtn = new Button("prev");
        prevBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                prevPage();
            }
        });

        nextBtn = new Button("next");
        nextBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                nextPage();
            }
        });

        setStyleName(Constants.Styles.PAGER_PANEL);
        add(pageNumChooser);
        add(pageNumber);
        add(prevBtn);
        add(nextBtn);
        setButtonVisible(prevBtn, false);
        addDocumentListener();
    }

    public void setTableSize(double tableSize) {
        this.tableSize = tableSize;
    }

    public void setPage(int page) {
        pageNumber.setText(String.valueOf(page));
        setButtonVisible(prevBtn, page != 1);
        setButtonVisible(nextBtn, page != Math.ceil(tableSize / getLimit()));
        onChange.run();
    }

    private void setButtonVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        if (visible) {
            btn.getElement().getParentElement().removeClassName("td_hidden");
        } else {
            btn.getElement().getParentElement().addClassName("td_hidden");
        }
    }

    public int getPageNumber() {
        return Integer.parseInt(pageNumber.getText());
    }

    public int getLimit() {
        return Integer.parseInt(pageNumChooser.getSelectedValue());
    }

    private void nextPage() {
        int nextPage = Integer.parseInt(pageNumber.getText()) + 1;
        int lastPage = (int)Math.ceil(tableSize / getLimit());
        if (nextPage > lastPage) {
            return;
        }
        setButtonVisible(nextBtn, nextPage != lastPage);
        setButtonVisible(prevBtn, true);
        pageNumber.setText(String.valueOf(nextPage));
        onChange.run();
    }

    private void prevPage() {
        int prevPage = Integer.parseInt(pageNumber.getText()) - 1;
        if (prevPage <= 0) {
            return;
        }
        setButtonVisible(prevBtn, prevPage != 1);
        setButtonVisible(nextBtn, true);
        pageNumber.setText(String.valueOf(prevPage));
        onChange.run();
    }

    private void addDocumentListener() {
        GQuery.$(GQuery.document).keydown(new Function() {
            @Override
            public boolean f(Event e) {
                if (e.getCtrlKey() && e.getKeyCode() == KeyCodes.KEY_RIGHT) {
                    nextPage();
                    stopEvent(e);
                } else if (e.getCtrlKey() && e.getKeyCode() == KeyCodes.KEY_LEFT) {
                    prevPage();
                    stopEvent(e);
                }
                return true;
            }
        });
    }

    private void stopEvent(Event e) {
        e.preventDefault();
        e.stopPropagation();
    }
}
