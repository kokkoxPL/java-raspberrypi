package com.adrianpl;

import de.re.easymodbus.modbusclient.ModbusClient;

public class Main {
  public static void main(String[] args) {
    ModbusClient modbusClient = new ModbusClient("127.0.0.1", 502);
    try {
      modbusClient.Connect();
      System.out.println(modbusClient.ReadCoils(0, 1)[0]);
      System.out.println(modbusClient.ReadHoldingRegisters(0, 1)[0]);

    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }
}
