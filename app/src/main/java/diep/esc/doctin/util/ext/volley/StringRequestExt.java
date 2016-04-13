package diep.esc.doctin.util.ext.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diep on 31/03/2016.
 * This Class is the extend of {@link StringRequest}, Change User-agent, and default String encoding
 */
public class StringRequestExt extends StringRequest {


    /**
     * Delegate Constructor.
     *
     * @see StringRequest#StringRequest(int, String, Response.Listener, Response.ErrorListener)
     */
    public StringRequestExt(int method, String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    /**
     * Delegate Constructor.
     *
     * @see StringRequest#StringRequest(String, Response.Listener, Response.ErrorListener)
     */
    public StringRequestExt(String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    /**
     * This method override from {@link StringRequest#parseNetworkResponse(NetworkResponse)},
     * change the default encoding to UTF-8
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * This method override from {@link StringRequest#getHeaders()},
     * change User-agent field to DocTin/by_Diep_Esc,
     * change  accept field to text/xml
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> sHeaders = super.getHeaders();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll(sHeaders);
        headers.put("User-agent", "DocTin/by_Diep_Esc");
        headers.put("accept", "text/xml");
        return headers;
    }
}
