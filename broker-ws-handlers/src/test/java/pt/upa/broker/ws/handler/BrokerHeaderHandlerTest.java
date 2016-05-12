package pt.upa.broker.ws.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;


/**
 *  Handler test suite
 */
public class BrokerHeaderHandlerTest extends BrokerAbstractHandlerTest {

    private static final String BROKER_NAME = "UpaBroker";
    // tests

//     @Test
    public void testHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST;
        // System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};

        // Unit under test is exercised.
        BrokerHeaderHandler handler = new BrokerHeaderHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        // assert header element
        Name name = soapEnvelope.createName("senderName", "broker-ws", "http://localhost:8091/broker-ws/endpoint");
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());

        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        assertEquals(BROKER_NAME, valueString);

        //soapMessage.writeTo(System.out);
    }

    @Test
    public void testHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header>" +
            "<d:myHeader xmlns:d=\"http://demo\">22</d:myHeader>" +
            "</SOAP-ENV:Header>");
        //System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;

            soapMessageContext.put(BrokerHeaderHandler.CONTEXT_PROPERTY, 22);
            soapMessageContext.setScope(BrokerHeaderHandler.CONTEXT_PROPERTY, Scope.APPLICATION);
        }};

        // Unit under test is exercised.
        BrokerHeaderHandler handler = new BrokerHeaderHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);

        //soapMessage.writeTo(System.out);
    }

}
