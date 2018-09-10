package org.gwtproject.http.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class JsonHttpResponse implements HttpResponse<JSONObject> {

    private int status;
    private String statusText;
    private String rawBody;

    public JsonHttpResponse(int status, String statusText, String rawBody) {
        this.status = status;
        this.statusText = statusText;
        this.rawBody = rawBody;
    }

    @Override
    public JSONObject getBody() {
        if(rawBody != null && !rawBody.isEmpty()) {
            JSONObject jsonObject = JSONParser.parseStrict(rawBody).isObject();
            return jsonObject;
        }
        return null;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getStatusText() {
        return null;
    }
}
