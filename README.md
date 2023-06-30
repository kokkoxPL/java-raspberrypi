# Program do testowania gpio w raspberry pi

## Główne Foldery:

- Gpio - główny program do testowania gpio
- WebServer- program do komunikacji z głównym programem

## Foldery do osobistych testów

- ModbusClient - program do testowania modbusa
- SnmpServer - program do testowania snmp

## Instalacja (wymagane są uprawnienia root)

#### Pliki instaluje się jako serwis. Przed instalacją można zmienić właściwości serwisu w folderze install. Skrypt urachamia się będąc w tym folderze.

```bash
chmod +x ./install.sh`
./install.sh
sudo systemctl daemon-reload
```

## Serwisy są pod nazwami gpio i web-server

```bash
sudo systemctl start gpio
sudo systemctl start web-server
```

## Odinstalowanie (wymagane są uprawnienia root)

```bash
chmod +x ./uninstall.sh`
./uninstall.sh
```
