package org.gwtproject.http.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpClientTest extends GWTTestCase {

    static final Logger logger = Logger.getLogger(HttpClientTest.class.getName());

    @Override
    public String getModuleName() {
        return "org.gwtproject.http.HttpClient";
    }

    public void testGet() throws Exception{
        HttpResponse<JSONObject> response = HttpClient.get("https://httpbin.org/get")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .asJson();
        Window.alert(response.toString());
        JSONObject json = response.getBody();
        JSONString url = json.get("url").isString();
        JSONObject headers = json.get("headers").isObject();
        assertNotNull(json);
        assertNotNull(url);
        assertNotNull(headers);
        Window.alert("Test Done");
    }

    public void testPostFormField() throws Exception{
        HttpResponse<JSONObject> response = HttpClient.post("https://httpbin.org/post")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .queryString("name", "Mark")
                .field("middle", "O")
                .field("last", "Polo")
                .asJson();
        Window.alert(response.toString());
        JSONObject json = response.getBody();
        JSONString url = json.get("url").isString();
        JSONObject headers = json.get("headers").isObject();
        JSONObject args = json.get("args").isObject();
        assertNotNull(json);
        assertNotNull(url);
        assertNotNull(headers);
        assertNotNull(args);
        String name = args.get("name").isString().stringValue();
        assertEquals("Mark", name);
    }

    public void testPostJson() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("hello", new JSONString("world"));

        HttpResponse<JSONObject> response = HttpClient.post("https://httpbin.org/post")
                //.header("accept", "application/json")
                //.header("Content-Type", "application/json")
                .queryString("name", "Mark")
                .body(payload.isObject().toString())
                .asJson();
        Window.alert(response.toString());
        JSONObject json = response.getBody();
        JSONString url = json.get("url").isString();
        JSONObject headers = json.get("headers").isObject();
        JSONObject jsonField = json.get("json").isObject();

        assertNotNull(json);
        assertNotNull(url);
        assertNotNull(headers);
        assertNotNull(jsonField);

        String accept = headers.get("Accept").isString().stringValue();
        String contentType = headers.get("Content-Type").isString().stringValue();
        String hello = jsonField.get("hello").isString().stringValue();

        assertEquals("application/json", accept);
        assertEquals("application/json", contentType);
        assertEquals("world", hello);
    }

    public void testPut() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("hello", new JSONString("world"));

        HttpResponse<JSONObject> response = HttpClient.put("https://httpbin.org/put")
                .queryString("name", "Mark")
                .body(payload.isObject().toString())
                .basicAuth("john", "doe")
                .asJson();
        Window.alert(response.toString());
        JSONObject json = response.getBody();
        JSONString url = json.get("url").isString();
        JSONObject headers = json.get("headers").isObject();
        JSONObject jsonField = json.get("json").isObject();

        assertNotNull(json);
        assertNotNull(url);
        assertNotNull(headers);
        assertNotNull(jsonField);

        String accept = headers.get("Accept").isString().stringValue();
        String authorization = headers.get("Authorization").isString().stringValue();
        String contentType = headers.get("Content-Type").isString().stringValue();
        String hello = jsonField.get("hello").isString().stringValue();

        assertEquals("application/json", accept);
        assertEquals("application/json", contentType);
        assertEquals("world", hello);

        String actual = "Basic " + Base64.btoa("john" + ":" + "doe");
        assertEquals(actual, authorization);
    }

    public void testDelete() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("hello", new JSONString("world"));

        HttpResponse<JSONObject> response = HttpClient.delete("https://httpbin.org/delete")
                .queryString("name", "Mark")
                .body(payload.isObject().toString())
                .basicAuth("john", "doe")
                .asJson();
        Window.alert(response.toString());
        JSONObject json = response.getBody();
        JSONString url = json.get("url").isString();
        JSONObject headers = json.get("headers").isObject();

        assertNotNull(json);
        assertNotNull(url);
        assertNotNull(headers);

        String accept = headers.get("Accept").isString().stringValue();
        String authorization = headers.get("Authorization").isString().stringValue();
        String contentType = headers.get("Content-Type").isString().stringValue();

        assertEquals("application/json", accept);
        assertEquals("application/json", contentType);

        String actual = "Basic " + Base64.btoa("john" + ":" + "doe");
        assertEquals(actual, authorization);
    }

}
