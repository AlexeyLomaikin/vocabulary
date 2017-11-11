package com.localhost.gwt.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.localhost.gwt.shared.transport.ServiceRequest;
import com.localhost.gwt.shared.transport.ServiceResponse;
import com.localhost.gwt.shared.SharedRuntimeException;
import com.localhost.gwt.shared.model.Word;

import java.util.List;

/**
 * Created by AlexL on 06.10.2017.
 */
@RemoteServiceRelativePath("vocabularyService")
public interface VocabularyService extends RemoteService {
    ServiceResponse getLevels() throws SharedRuntimeException;
    ServiceResponse getWords(ServiceRequest request) throws SharedRuntimeException;
    void addWords(List<Word> words) throws SharedRuntimeException;
    ServiceResponse getLanguages() throws SharedRuntimeException;
}
