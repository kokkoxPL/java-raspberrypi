# Program do testowania gpio w raspberry pi

## Foldery:

- Gpio - główny program do testowania gpio
- WebServer- program do komunikacji z głównym programem
- ModbusClient - program do testowania modbusa
- SnmpServer - program do testowania snmp

### Gpio, WebServer, ModbusClient używają biblioteki którą można dodać do mavena za pomocą tego skryptu (skrypt uruchom będąc w tym folderze):

`mvn install:install-file -Dfile=./lib/EasyModbusJava.jar -DgroupId=de.re.easymodbus -DartifactId=modbus -Dversion=1.0 -Dpackaging=jar`

każdy program kompiluje się za pomocą maven

`mvn package`

i uruchamia się:

`sudo java -jar target/$plik`
