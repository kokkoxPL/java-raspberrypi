package com.adrianpl;

import java.io.IOException;

import de.re.easymodbus.server.ModbusServer;

public class ModbusTCPServer {
    private final int MODBUS_PORT;
    private ModbusServer modbusServer;

    public ModbusTCPServer(int MODBUS_PORT) {
        this.MODBUS_PORT = MODBUS_PORT;

        listen();
    }

    public void listen() {
        modbusServer = new ModbusServer();
        modbusServer.setPort(MODBUS_PORT);
        try {
            modbusServer.Listen();
        } catch (IOException e) {
            System.out.println("There was problem with modbus server");
            e.printStackTrace();
        }
    }

    // wartości w modbusie za pomocą paczki EasyModbus zaczynają
    // od 1 ale tylko w serwerze, bo w kliencie zaczynają się od 0
    // dziwne

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
