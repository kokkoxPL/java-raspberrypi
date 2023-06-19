package com.adrianpl;

import de.re.easymodbus.server.ModbusServer;

public class ModbusTCPServer {
    private ModbusServer modbusServer;

    public ModbusTCPServer() {
        modbusServer = new ModbusServer();
        modbusServer.setPort(502);
        try {
            modbusServer.Listen();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void writeCoils(int position, boolean value) {
        modbusServer.coils[position + 1] = value;
    }

    public void writeDiscreteInputs(int position, boolean value) {
        modbusServer.discreteInputs[position + 1] = value;
    }

    public void writeHoldingRegisters(int position, int value) {
        modbusServer.holdingRegisters[position + 1] = value;
    }

    public void writeInputRegisters(int position, int value) {
        modbusServer.inputRegisters[position + 1] = value;
    }
}
