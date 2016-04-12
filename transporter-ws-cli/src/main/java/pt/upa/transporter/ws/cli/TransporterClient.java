package pt.upa.transporter.ws.cli;

import java.util.List;

import pt.upa.transporter.ws.TransporterPortType;

//Classe que e usada pelo broker-ws para fazer invocaçoes remotas indirectamente.
public class TransporterClient
{
        private String _name; //i.e "UpaTransporter1"
        private TransporterPortType _port;
        
        public TransporterClient(String name, TransporterPortType port){
            _port = port;
            _name = name;
        }
        
        public String ping(String name)
        {
            return _port.ping(name);
        }

}
