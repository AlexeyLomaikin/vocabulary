package com.localhost.gwt.client;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.localhost.gwt.shared.Constants;
import com.localhost.gwt.shared.ObjectUtils;

/**
 * Created by AlexL on 08.11.2017.
 */
public class PagerPanel extends HorizontalPanel {
    private final static String[] AVAILABLE_LIMITS = {"5", "10", "25", "50"};

    private ListBox limitChooser;
    private TextBox pageNumber;
    private Button prevBtn;
    private Button nextBtn;

    private double tableSize;
    private int currentPage = 1;
    private Runnable callBack;

    public PagerPanel(final Runnable callBack) {
        this.callBack = callBack;
        setStyleName(Constants.Styles.PAGER_PANEL);
        addHotKeys();

        limitChooser = new ListBox();
        limitChooser.clear();
        for (int i = 0; i < AVAILABLE_LIMITS.length; i++) {
            limitChooser.addItem(AVAILABLE_LIMITS[i], AVAILABLE_LIMITS[i]);
        }
        limitChooser.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                setPage(1);
            }
        });

        pageNumber = createPageNumberTextBox();

        prevBtn = new Button();
        prevBtn.addStyleName(Constants.Styles.PREV_PAGE_BTN);
        prevBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                boolean hasFocus = GQuery.$(prevBtn).is(":focus");
                prevPage();
                if (hasFocus && !prevBtn.isVisible()) {
                    nextBtn.setFocus(true);
                }
            }
        });

        nextBtn = new Button();
        nextBtn.addStyleName(Constants.Styles.NEXT_PAGE_BTN);
        nextBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                boolean hasFocus = GQuery.$(nextBtn).is(":focus");
                nextPage();
                if (hasFocus && !nextBtn.isVisible()) {
                    prevBtn.setFocus(true);
                }
            }
        });

        add(limitChooser);
        add(prevBtn);
        add(pageNumber);
        add(nextBtn);
        setButtonVisible(prevBtn, false);
    }

    private TextBox createPageNumberTextBox() {
        final TextBox pageNumber = new TextBox();
        pageNumber.setTitle("Page");
        pageNumber.setText("1");
        pageNumber.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent blurEvent) {
                pageNumber.setText(String.valueOf(currentPage));
            }
        });
        pageNumber.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                String value = pageNumber.getText();
                if (ObjectUtils.isEmpty(value)) {
                    return;
                }
                char lastSymbol = value.charAt(value.length() - 1);
                if (!Character.isDigit(lastSymbol)) {
                    pageNumber.setText(value.substring(0, value.length() - 1));
                }
                int page = Integer.parseInt(value);
                if (page != currentPage && page < getLastPage() + 1) {
                    setPage(currentPage = page);
                }
            }
        });
        return pageNumber;
    }

    public void setTableSize(double tableSize) {
        this.tableSize = tableSize;
        int lastPage = getLastPage();
        setButtonVisible(nextBtn, currentPage != lastPage);
    }

    public void setPage(int page) {
        currentPage = page;
        pageNumber.setText(String.valueOf(page));
        setButtonVisible(prevBtn, page != 1);
        setButtonVisible(nextBtn, page != Math.ceil(tableSize / getLimit()));
        callBack.run();
    }

    private void nextPage() {
        int nextPage = Integer.parseInt(pageNumber.getText()) + 1;
        int lastPage = getLastPage();
        if (nextPage > lastPage) {
            return;
        }
        setButtonVisible(nextBtn, nextPage != lastPage);
        setButtonVisible(prevBtn, true);
        currentPage = nextPage;
        pageNumber.setText(String.valueOf(nextPage));
        callBack.run();
    }

    private void prevPage() {
        int prevPage = Integer.parseInt(pageNumber.getText()) - 1;
        if (prevPage <= 0) {
            return;
        }
        setButtonVisible(prevBtn, prevPage != 1);
        setButtonVisible(nextBtn, true);
        currentPage = prevPage;
        pageNumber.setText(String.valueOf(prevPage));
        callBack.run();
    }

    private void addHotKeys() {
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

    private void setButtonVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        if (visible) {
            btn.getElement().getParentElement().removeClassName("td_hidden");
        } else {
            btn.getElement().getParentElement().addClassName("td_hidden");
        }
    }

    private int getLastPage() {
        return (int)Math.ceil(tableSize / getLimit());
    }

    private void stopEvent(Event e) {
        e.preventDefault();
        e.stopPropagation();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLimit() {
        return Integer.parseInt(limitChooser.getSelectedValue());
    }
}
