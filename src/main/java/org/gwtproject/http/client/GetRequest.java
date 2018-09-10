package org.gwtproject.http.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GetRequest {

    static final Logger logger = Logger.getLogger(GetRequest.class.getName());

    private String url;
    private Multimap<String,String> headerMap;
    private Map<String, String> queryMap = null;
    private Map<String,Object> fields = null;

    private String authorization = null;
    private int TIMEOUT = 60000;

    private String httpResponse;
    private String httpStatusText = null;
    private Integer httpStatusCode = null;
    private RequestException httpException;

    public GetRequest(String url) {
        setUrl(url);
        headerMap = ArrayListMultimap.create();
    }

    public GetRequest header(String header, String value) {
        if(headerMap == null){
            headerMap = ArrayListMultimap.create();
        }
        if(value != null) {
            headerMap.put(header, value);
        }
        return this;
    }

    public GetRequest queryString(String name, String value){
        if(queryMap == null){
            queryMap = new LinkedHashMap<String,String>();
        }
        queryMap.put(name, value);
        return this;
    }

    public GetRequest basicAuth(String username, String password) {
        authorization = "Basic " + Base64.btoa(username + ":" + password);
        return this;
    }

    public HttpResponse<String> asString() throws RequestException {
        proxyXMLHttpRequestOpen();
        httpException = null;
        httpResponse = null;

        if(queryMap != null && !queryMap.isEmpty()){
            url = url + "?";
            url = url +  queries(queryMap);
        }
        RequestBuilder b = new RequestBuilder(RequestBuilder.GET, url);
        b.setTimeoutMillis(TIMEOUT);
        if(headerMap != null){
            // Set default first
            headerMap.put("Content-Type", "application/json");
            headerMap.put("accept", "application/json");
            for (Map.Entry<String,String> entry : headerMap.entries()) {
                if(entry.getKey() != null && entry.getValue() != null
                        && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                    b.setHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        if(authorization != null){
            b.setHeader("Authorization", authorization);
        }
        b.sendRequest(null, new RequestCallback() {
            public void onResponseReceived(Request request, Response response) {
                String resp = response.getText();
                int statusCode = response.getStatusCode();
                String statusText = response.getStatusText();
                setHttpStatusCode(statusCode);
                setHttpResponse(resp);
                setHttpStatusText(statusText);
                if(response.getStatusCode() >= 400){
                    setHttpException(new HttpRequestException(resp, response.getStatusCode()));
                } else {
                    setHttpResponse(response.getText());
                }
            }
            public void onError(Request request, Throwable exception) {
                httpException = (RequestException) exception;
            }
        });
        if(httpException != null) {
            throw httpException;
        }
        return new StringHttpResponse(getHttpStatusCode(), getHttpStatusText(), getHttpResponse());
    }

    public HttpResponse<JSONObject> asJson() throws RequestException {
        proxyXMLHttpRequestOpen();

        httpException = null;
        httpResponse = null;

        if(queryMap != null && !queryMap.isEmpty()){
            url = url + "?";
            url = url +  queries(queryMap);
        }
        RequestBuilder b = new RequestBuilder(RequestBuilder.GET, url);
        b.setTimeoutMillis(TIMEOUT);
        if(headerMap != null){
            // Set default first
            headerMap.put("Content-Type", "application/json");
            headerMap.put("accept", "application/json");
            for (Map.Entry<String,String> entry : headerMap.entries()) {
                if(entry.getKey() != null && entry.getValue() != null
                        && !entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                    b.setHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        if(authorization != null){
            b.setHeader("Authorization", authorization);
        }
        b.sendRequest(null, new RequestCallback() {
            public void onResponseReceived(Request request, Response response) {
                String resp = response.getText();
                int statusCode = response.getStatusCode();
                String statusText = response.getStatusText();
                setHttpStatusCode(statusCode);
                setHttpResponse(resp);
                setHttpStatusText(statusText);
                if(response.getStatusCode() >= 400){
                    setHttpException(new HttpRequestException(resp, response.getStatusCode()));
                } else {
                    setHttpResponse(response.getText());
                }
            }
            public void onError(Request request, Throwable exception) {
                httpException = (RequestException) exception;
            }
        });
        if(httpException != null) {
            throw httpException;
        }
        return new JsonHttpResponse(getHttpStatusCode(), getHttpStatusText(), getHttpResponse());
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

    private static native void proxyXMLHttpRequestOpen() /*-{
        var proxied = $wnd.XMLHttpRequest.prototype.open;

        (function() {
            $wnd.XMLHttpRequest.prototype.open =
                function() {
                    arguments[2] = false;
                    return proxied.apply(this, [].slice.call(arguments));
                };
        })();
    }-*/;

    private String getHttpResponse() {
        return httpResponse;
    }

    private void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }

    private String getHttpStatusText() {
        return httpStatusText;
    }

    private void setHttpStatusText(String httpStatusText) {
        this.httpStatusText = httpStatusText;
    }

    private Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    private void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    private RequestException getException() {
        return this.httpException;
    }

    private void setHttpException(RequestException exception) {
        this.httpException = exception;
    }
}
