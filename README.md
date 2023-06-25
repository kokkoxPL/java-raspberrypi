gpio - program 1
webServer- program 2
modbusClient - program do testowania modbusa
snmpClient - program do testowania snmp

biblioteka do dodania przez maven:
```mvn install:install-file -Dfile=./lib/EasyModbusJava.jar -DgroupId=de.re.easymodbus -DartifactId=modbus -Dversion=1.0 -Dpackaging=jar```

każdy program kompiluje się za pomocą maven
```mvn package```
i uruchamia się:
```sudo java -jar $plik```
