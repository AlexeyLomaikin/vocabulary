package com.localhost.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.localhost.gwt.client.service.VocabularyService;
import com.localhost.gwt.client.service.VocabularyServiceAsync;
import com.localhost.gwt.shared.transport.ServiceRequest;
import com.localhost.gwt.shared.transport.ServiceResponse;

/**
 * Created by AlexL on 15.11.2017.
 */
public class DataManager {
    private ServiceRequest request;
    private ServiceResponse response;

    private VocabularyServiceAsync asyncService = GWT.create(VocabularyService.class);

    public void loadWords(ServiceRequest request, final AsyncCallback<ServiceResponse> callback) {
        this.request = request;
        asyncService.getWords(request, createInnerCallback(callback));
    }

    public void loadLevels(AsyncCallback<ServiceResponse> callback) {
        asyncService.getLevels(createInnerCallback(callback));
    }

    public void loadLanguages(AsyncCallback<ServiceResponse> callback) {
        asyncService.getLanguages(createInnerCallback(callback));
    }

    public ServiceRequest getRequest() {
        return request;
    }

    public ServiceResponse getResponse() {
        return response;
    }

    private AsyncCallback<ServiceResponse> createInnerCallback (final AsyncCallback<ServiceResponse> outerCallBack) {
        return new AsyncCallback<ServiceResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                outerCallBack.onFailure(throwable);
            }

            @Override
                public void onSuccess(ServiceResponse response) {
                DataManager.this.response = response;
                outerCallBack.onSuccess(response);
            }
        };
    }
}
