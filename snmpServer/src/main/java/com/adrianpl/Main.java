package com.adrianpl;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

class Main {

    public static void main(String[] args) throws Exception {
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/161"));

        try (Snmp snmp = new Snmp(transport)) {
            CommunityTarget<Address> target = new CommunityTarget<Address>();
            target.setCommunity(new OctetString("public"));
            target.setVersion(SnmpConstants.version2c);

            snmp.addCommandResponder(new CommandResponder() {
                @SuppressWarnings("rawtypes")
                public void processPdu(CommandResponderEvent event) {
                    PDU request = event.getPDU();
                    if (request != null) {
                        System.out.println("Received SNMP request: " + request.toString());

                        PDU response = new PDU(request);
                        response.setType(PDU.RESPONSE);

                        response.setErrorIndex(0);
                        response.setErrorStatus(0);
                        response.setRequestID(request.getRequestID());
                        response.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0"),
                                new OctetString("New response just dropped!")));

                        try {
                            event.getMessageDispatcher().returnResponsePdu(event.getMessageProcessingModel(),
                                    event.getSecurityModel(), event.getSecurityName(), event.getSecurityLevel(),
                                    response, event.getMaxSizeResponsePDU(),
                                    (StateReference<?>) event.getStateReference(), new StatusInformation());
                            System.out.println("Sent SNMP response: " + response.toString());
                        } catch (MessageException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        transport.listen();
        System.out.println("SNMPv2c server is running and listening for requests...");

        while (true) {
            Thread.sleep(1000);
        }
    }
}