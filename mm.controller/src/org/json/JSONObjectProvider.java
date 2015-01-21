package org.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.utils.MediaTypeUtils;
import org.apache.wink.common.utils.ProviderUtils;  

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

@Provider
@Consumes(value = {MediaType.APPLICATION_JSON, "application/java"})
@Produces(value = {MediaType.APPLICATION_JSON, "application/java"})
public class JSONObjectProvider implements MessageBodyWriter<JSONObject>, 
    MessageBodyReader<JSONObject>{


  public boolean isReadable(Class<?> classs, 
                              Type type, 
                              Annotation[] annotations,
                              MediaType mediatype) {
       
    return JSONObject.class == classs;
  }

  @Override
    public JSONObject readFrom(Class<JSONObject> classs, 
                               Type type,
                               Annotation[] annotations, 
                               MediaType mediatype,
                               MultivaluedMap<String, String> headers, 
                               InputStream inputstream)
                                       throws IOException, WebApplicationException {
    try {
      return new JSONObject(
                  new InputStreamReader(inputstream, ProviderUtils.getCharset(mediatype)));
    } catch (JSONException e) {
      throw new WebApplicationException(e, Status.BAD_REQUEST);
    }
  }

  @Override
    public long getSize(JSONObject arg0, Class<?> arg1, Type arg2,
            Annotation[] arg3, MediaType arg4) {

    return -1;
  }

  @Override
    public boolean isWriteable(Class<?> classs, 
                               Type type, 
                               Annotation[] annotations,
                               MediaType mediatype) {
    return JSONObject.class.isAssignableFrom(classs);
  }

  @Override
    public void writeTo(JSONObject jo, 
                        Class<?> classs, 
                        Type type,
                        Annotation[] annotations, 
                        MediaType mediatype,
                        MultivaluedMap<String, Object> header, 
                        OutputStream os)
            throws IOException, WebApplicationException {
    mediatype = MediaTypeUtils.setDefaultCharsetOnMediaTypeHeader(header, mediatype);
    OutputStreamWriter writer = new OutputStreamWriter(os, ProviderUtils.getCharset(mediatype));
    try {
      Writer json4jWriter = jo.write(writer);
      json4jWriter.flush();
      writer.flush();
    } catch (JSONException e) {
      throw new WebApplicationException(e, 500);
    }
        
  }

}
