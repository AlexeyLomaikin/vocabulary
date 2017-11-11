package com.localhost.gwt.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.localhost.gwt.shared.transport.ServiceRequest;
import com.localhost.gwt.shared.transport.ServiceResponse;
import com.localhost.gwt.shared.SharedRuntimeException;
import com.localhost.gwt.shared.model.Word;

import java.util.List;

/**
 * Created by AlexL on 06.10.2017.
 */
public interface VocabularyServiceAsync {

    void getLevels(AsyncCallback<ServiceResponse> callback) throws SharedRuntimeException;
    void getWords(ServiceRequest request, AsyncCallback<ServiceResponse> callback) throws SharedRuntimeException;
    void addWords(List<Word> words, AsyncCallback<Void> callback) throws SharedRuntimeException;
    void getLanguages(AsyncCallback<ServiceResponse> callback) throws SharedRuntimeException;
}
