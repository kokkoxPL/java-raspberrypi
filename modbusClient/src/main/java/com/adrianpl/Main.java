package com.adrianpl;

import java.util.Arrays;

import de.re.easymodbus.modbusclient.ModbusClient;

public class Main {
  public static void main(String[] args) {
    ModbusClient modbusClient = new ModbusClient("127.0.0.1", 502);
    try {
      modbusClient.Connect();
      System.out.println(Arrays.toString(modbusClient.ReadHoldingRegisters(0, 1)));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }
}
