package org.gwtproject.http.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import org.gwtproject.http.client.exceptions.BadRequestException;
import org.gwtproject.http.client.exceptions.NotFoundRequestException;
import org.gwtproject.http.client.exceptions.UnauthorizedRequestException;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HttpRequestWithBody {

    static final Logger logger = Logger.getLogger(HttpRequestWithBody.class.getName());

    private String url;
    private Multimap<String,String> headerMap;
    private Map<String, String> queryMap;
    private Map<String,Object> fields;
    private Object body = null;
    private RequestBuilder.Method method;
    private int TIMEOUT = 60000;
    private String authorization;

    public HttpRequestWithBody(String url, RequestBuilder.Method method) {
        setUrl(url);
        this.method = method;
        headerMap = ArrayListMultimap.create();
    }

    public HttpRequestWithBody header(String header, String value) {
        if(headerMap == null){
            headerMap = ArrayListMultimap.create();
        }
        if(value != null) {
            headerMap.put(header, value);
        }
        return this;
    }

    public HttpRequestWithBody body(Object body){
        this.body = body;
        return this;
    }

    public HttpRequestWithBody queryString(String name, String value){
        if(queryMap == null){
            queryMap = new LinkedHashMap<String,String>();
        }
        queryMap.put(name, value);
        return this;
    }

    public HttpRequestWithBody field(String name, String value){
        if(fields == null){
            fields = new LinkedHashMap<String,Object>();
        }
        fields.put(name, value);
        return this;
    }

    public HttpRequestWithBody basicAuth(String username, String password) {
        authorization = "Basic " + Base64.btoa(username + ":" + password);
        return this;
    }

    public Single<HttpResponse<String>> asString() {
        return Single.create(new SingleOnSubscribe<HttpResponse<String>>() {
            @Override
            public void subscribe(SingleEmitter<HttpResponse<String>> emitter) throws RequestException {
                if(queryMap != null && !queryMap.isEmpty()){
                    url = url + "?";
                    url = url +  queries(queryMap);
                }
                RequestBuilder b = new RequestBuilder(method, url);
                b.setTimeoutMillis(TIMEOUT);
                if(headerMap != null){
                    // Check first if Content-Type and accept headers are already set else set defaults
                    boolean hasContentType = false;
                    boolean hasAccept = false;
                    for (Map.Entry<String,String> entry : headerMap.entries()) {
                        if(entry.getKey() != null && entry.getValue() != null
                                && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                            if(entry.getKey().equals("Content-Type")) {
                                hasContentType = true;
                            } else if (entry.getKey().equals("accept")) {
                                hasAccept = true;
                            }
                        }
                    }
                    if(!hasAccept) {
                        headerMap.put("accept", "application/json");
                    }
                    if(!hasContentType) {
                        headerMap.put("Content-Type", "application/json");
                    }
                    for (Map.Entry<String,String> entry : headerMap.entries()) {
                        if(entry.getKey() != null && entry.getValue() != null
                                && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                            b.setHeader(entry.getKey(), entry.getValue());
                        }
                    }
                }
                Object payload = body;
                if(fields != null && !fields.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    Iterator<Map.Entry<String,Object>> it = fields.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String,Object> entry = it.next();
                        if(entry.getValue() instanceof String){
                            if(!it.hasNext()){
                                sb.append(entry.getKey()).append("=").append(URL.encodeComponent((String.valueOf(entry.getValue()))));
                            } else {
                                sb.append(entry.getKey()).append("=").append(URL.encodeComponent((String.valueOf(entry.getValue())))).append("&");
                            }
                        }
                    }
                    payload = sb.toString();
                    b.setHeader("Content-Type","application/x-www-form-urlencoded");
                }
                if(body != null){
                    if(body instanceof JavaScriptObject){
                        // TODO for Sending File
                    }
                }
                if(authorization != null){
                    b.setHeader("Authorization", authorization);
                }
                if(payload != null) {
                    b.sendRequest(String.valueOf(payload), new RequestCallback() {
                        public void onResponseReceived(Request request, Response response) {
                            String resp = response.getText();
                            int statusCode = response.getStatusCode();
                            String statusText = response.getStatusText();
                            emitter.onSuccess(new StringHttpResponse(statusCode, statusText, resp));
                        }
                        public void onError(Request request, Throwable exception) {
                            emitter.onError(exception);
                        }
                    });
                } else {
                    b.sendRequest("", new RequestCallback() {
                        public void onResponseReceived(Request request, Response response) {
                            String resp = response.getText();
                            int statusCode = response.getStatusCode();
                            String statusText = response.getStatusText();
                            emitter.onSuccess(new StringHttpResponse(statusCode, statusText, resp));
                        }
                        public void onError(Request request, Throwable exception) {
                            emitter.onError(exception);
                        }
                    });
                }
            }
        });
    }

    public Single<HttpResponse<JsonNode>> asJson() {
        return Single.create(new SingleOnSubscribe<HttpResponse<JsonNode>>() {
            @Override
            public void subscribe(SingleEmitter<HttpResponse<JsonNode>> emitter) throws Exception {
                if(queryMap != null && !queryMap.isEmpty()){
                    url = url + "?";
                    url = url +  queries(queryMap);
                }
                RequestBuilder b = new RequestBuilder(method, url);
                b.setTimeoutMillis(TIMEOUT);
                if(headerMap != null){
                    // Check first if Content-Type and accept headers are already set else set defaults
                    boolean hasContentType = false;
                    boolean hasAccept = false;
                    for (Map.Entry<String,String> entry : headerMap.entries()) {
                        if(entry.getKey() != null && entry.getValue() != null
                                && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                            if(entry.getKey().equals("Content-Type")) {
                                hasContentType = true;
                            } else if (entry.getKey().equals("accept")) {
                                hasAccept = true;
                            }
                        }
                    }
                    if(!hasAccept) {
                        headerMap.put("accept", "application/json");
                    }
                    if(!hasContentType) {
                        headerMap.put("Content-Type", "application/json");
                    }
                    for (Map.Entry<String,String> entry : headerMap.entries()) {
                        if(entry.getKey() != null && entry.getValue() != null
                                && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                            b.setHeader(entry.getKey(), entry.getValue());
                        }
                    }
                }
                Object payload = body;
                if(fields != null && !fields.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    Iterator<Map.Entry<String,Object>> it = fields.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String,Object> entry = it.next();
                        if(entry.getValue() instanceof String){
                            if(!it.hasNext()){
                                sb.append(entry.getKey()).append("=").append(URL.encodeComponent((String.valueOf(entry.getValue()))));
                            } else {
                                sb.append(entry.getKey()).append("=").append(URL.encodeComponent((String.valueOf(entry.getValue())))).append("&");
                            }
                        }
                    }
                    payload = sb.toString();
                    b.setHeader("Content-Type","application/x-www-form-urlencoded");
                }
                if(body != null){
                    if(body instanceof JavaScriptObject){
                        // TODO for Sending File
                    }
                }
                if(authorization != null){
                    b.setHeader("Authorization", authorization);
                }
                if(payload != null) {
                    b.sendRequest(String.valueOf(payload), new RequestCallback() {
                        public void onResponseReceived(Request request, Response response) {
                            String resp = response.getText();
                            int statusCode = response.getStatusCode();
                            String statusText = response.getStatusText();
                            emitter.onSuccess(new JsonHttpResponse(statusCode, statusText, resp));
                        }
                        public void onError(Request request, Throwable exception) {
                            emitter.onError(exception);
                        }
                    });
                } else {
                    b.sendRequest("", new RequestCallback() {
                        public void onResponseReceived(Request request, Response response) {
                            String resp = response.getText();
                            int statusCode = response.getStatusCode();
                            String statusText = response.getStatusText();
                            emitter.onSuccess(new JsonHttpResponse(statusCode, statusText, resp));
                        }
                        public void onError(Request request, Throwable exception) {
                            emitter.onError(exception);
                        }
                    });
                }
            }
        });
    }

    public Single<HttpResponse<InputStream>> asBinary() {
        return null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String queries(Map<String,String> parmsRequest){
        StringBuilder sb = new StringBuilder();
        for ( String k: parmsRequest.keySet() ) {
            String vx = URL.encodeComponent( parmsRequest.get(k));
            if ( sb.length() > 0 ) {
                sb.append("&");
            }
            sb.append(k).append("=").append(vx);
        }
        return sb.toString();
    }

    public void setTimeout(int timeout) {
        this.TIMEOUT = timeout;
    }

}
