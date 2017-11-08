package com.localhost.gwt.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.localhost.gwt.shared.ServiceResponse;
import com.localhost.gwt.shared.PagerItem;
import com.localhost.gwt.shared.SharedRuntimeException;
import com.localhost.gwt.shared.model.Word;

import java.util.List;

/**
 * Created by AlexL on 06.10.2017.
 */
public interface VocabularyServiceAsync {

    void getLevels(AsyncCallback<ServiceResponse> callback) throws SharedRuntimeException;
    void getWords(String levelId, String langId, PagerItem pagerItem, AsyncCallback<ServiceResponse> callback)
            throws SharedRuntimeException;
    void addWords(List<Word> words, AsyncCallback<Void> callback) throws SharedRuntimeException;
    void getLanguages(AsyncCallback<ServiceResponse> callback) throws SharedRuntimeException;
}
