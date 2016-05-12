package pt.upa.Transporter.ws.handler;

import pt.upa.authserver.ws.cli.AuthenticationServerClient;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of outbound messages before they are modified by header handler.
 */
public class TransporterEarlyLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String MESSAGE_ID = "[TransporterEarlyLoggingHandler]";

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    /**
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
     * outgoing or incoming message. Write a brief message to the print stream
     * and output the message. The writeTo() method can throw SOAPException or
     * IOException
     */
    private void logToSystemOut(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        System.out.println("««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««");
                
        if (outbound) {
            System.out.println(MESSAGE_ID + " Outbound SOAP message before header handling:");
        } else {
            return;
        }
        
        SOAPMessage message = smc.getMessage();
        try {
            System.out.print(MESSAGE_ID + " ");
            message.writeTo(System.out);
            System.out.println();
        } catch (Exception e) {
            System.out.printf("Exception in handler: %s%n", e);
        }
    }

}
