package com.adrianpl;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpController {
    private final String SNMP_ADDRESS;
    private final String VARIABLE_BINDING;

    private Snmp snmp;
    private PDU pdu;
    private Target<Address> target;

    public SnmpController(String SNMP_ADDRESS, String VARIABLE_BINDING) {
        this.SNMP_ADDRESS = SNMP_ADDRESS;
        this.VARIABLE_BINDING = VARIABLE_BINDING;

        listen();
    }

    public void listen() {
        pdu = new PDU();
        pdu.add(new VariableBinding(new OID(VARIABLE_BINDING)));
        pdu.setType(PDU.GETNEXT);

        target = new CommunityTarget<Address>();
        ((CommunityTarget<Address>) target).setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse(SNMP_ADDRESS));
        target.setRetries(2);
        target.setTimeout(500);
        target.setVersion(SnmpConstants.version2c);

        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
        } catch (IOException e) {
            System.out.println("There was a problem connecting to snmp");
            e.printStackTrace();
        }
    }

    public void sendResponse() {
        try {
            var response = snmp.send(pdu, target);

            if (response.getResponse() == null) {
                System.out.println("No response");
            } else {
                System.out.println("Received response from: " + response.getPeerAddress());
                System.out.println(response.getResponse().toString());
            }
        } catch (IOException e) {
            System.out.println("There was a problem sending a snmp message");
            e.printStackTrace();
        }
    }
}
