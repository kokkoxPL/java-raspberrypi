package com.adrianpl;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpController {
    private Address address = GenericAddress.parse("udp:127.0.0.1/161");
    private Snmp snmp;
    private PDU pdu;
    private Target<Address> target;

    public SnmpController() throws Exception {
        pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1")));
        pdu.setType(PDU.GETNEXT);

        target = new CommunityTarget<Address>();
        ((CommunityTarget<Address>) target).setCommunity(new OctetString("public"));
        target.setAddress(address);
        target.setRetries(2);
        target.setTimeout(500);
        target.setVersion(SnmpConstants.version2c);

        snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();
    }

    public void sendResponse() throws Exception {
        ResponseEvent<Address> response = snmp.send(pdu, target);
        if (response.getResponse() == null) {
            System.out.println("No response");
        } else {
            System.out.println("Received response from: " + response.getPeerAddress());
            System.out.println(response.getResponse().toString());
        }
    }
}
