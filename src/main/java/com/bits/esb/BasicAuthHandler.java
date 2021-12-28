package com.bits.esb;

import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.apache.synapse.rest.Handler;

public class BasicAuthHandler implements Handler {

  @Override
  public boolean handleRequest(MessageContext messageContext) {
    org.apache.axis2.context.MessageContext axis2MessageContext
        = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
    Object headers = axis2MessageContext.getProperty(
        org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

    if (headers instanceof Map) {
      Map headersMap = (Map) headers;
      if (headersMap.get("Authorization") == null) {
        headersMap.clear();
        axis2MessageContext.setProperty("HTTP_SC", "401");
        headersMap.put("WWW-Authenticate", "Basic realm=\"WSO2 ESB\"");
        axis2MessageContext.setProperty("NO_ENTITY_BODY", Boolean.TRUE);
        messageContext.setProperty("RESPONSE", "true");
        messageContext.setTo(null);
        Axis2Sender.sendBack(messageContext);
        return false;

      } else {
        String credentials = (String) headersMap.get("Authorization");
        if (processSecurity(credentials)) {
          return true;
        } else {
          headersMap.clear();
          axis2MessageContext.setProperty("HTTP_SC", "403");
          axis2MessageContext.setProperty("NO_ENTITY_BODY", Boolean.TRUE);
          messageContext.setProperty("RESPONSE", "true");
          messageContext.setTo(null);
          Axis2Sender.sendBack(messageContext);
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public boolean handleResponse(MessageContext messageContext) {
    return true;
  }

  @Override
  public void addProperty(String s, Object o) {

  }

  @Override
  public Map getProperties() {
    return null;
  }

  public boolean processSecurity(String credentials) {
    String decodedCredentials = new String(new Base64().decode(credentials.getBytes()));
    String username = decodedCredentials.split(":")[0];
    String password = decodedCredentials.split(":")[1];
    return "admin".equals(username) && "admin".equals(password);
  }
}
