package com.adrianpl;

import java.io.IOException;

import de.re.easymodbus.server.ModbusServer;

public class ModbusTCPServer {
    private ModbusServer modbusServer;

    public ModbusTCPServer() {
        modbusServer = new ModbusServer();
        modbusServer.setPort(502);
        try {
            modbusServer.Listen();
        } catch (IOException e) {
            System.out.println("There was problem with modbus server");
            e.printStackTrace();
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
