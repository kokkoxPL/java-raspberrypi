sudo mkdir /opt/gpio-testing

mvn install:install-file -Dfile=./lib/EasyModbusJava.jar -DgroupId=de.re.easymodbus -DartifactId=modbus -Dversion=1.0 -Dpackaging=jar

cd ./ModbusAndSnmp
mvn clean install
cd ..

cd ./Gpio
mvn clean package
sudo cp ./target/gpio.jar /opt/gpio-testing
cd ..

cd ./WebServer
mvn clean package
sudo cp ./target/web-server.jar /opt/gpio-testing
cd ..

sudo mkdir /etc/gpio-testing
cd install

cd gpio
sudo cp ./gpio.service /etc/systemd/system
sudo cp ./gpio.sh /usr/local/sbin
sudo cp ./gpio.properties /etc/gpio-testing
cd ..

cd web-server
sudo cp ./web-server.service /etc/systemd/system
sudo cp ./web-server.sh /usr/local/sbin
sudo cp ./web-server.properties /etc/gpio-testing

exit 0