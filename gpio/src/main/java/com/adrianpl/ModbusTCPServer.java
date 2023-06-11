package com.adrianpl;

import de.re.easymodbus.server.ModbusServer;

public class ModbusTCPServer {
    ModbusServer modbusServer;

    public ModbusTCPServer() {
        modbusServer = new ModbusServer();
        modbusServer.setPort(502);
        try {
            modbusServer.Listen();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void writeCoils(boolean coil) {
        modbusServer.coils[1] = coil;
    }

    public void writeDiscreteInputs(boolean inputs) {
        modbusServer.discreteInputs[1] = inputs;
    }

    public void writeHoldingRegisters(int registers) {
        modbusServer.holdingRegisters[1] = registers;
    }

    public void writeInputRegisters(int inputRegisters) {
        modbusServer.inputRegisters[1] = inputRegisters;
    }
}
