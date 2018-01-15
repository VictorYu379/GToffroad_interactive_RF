#include <SoftwareSerial.h>

const byte HC12RxdPin = 2;                  // Recieve Pin on HC12
const byte HC12TxdPin = 12;                  // Transmit Pin on HC12
int counter = 97;

SoftwareSerial HC12(HC12TxdPin,HC12RxdPin); // Create Software Serial Port

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  HC12.begin(9600);
//  Serial.println("begiy begin");
}

void loop() {
  // put your main code here, to run repeatedly:
  while (HC12.available()) {
    Serial.print((char) HC12.read());
  }

  if (Serial.available()) {
    char a = Serial.read();
    Serial.print("Write: ");
    Serial.println(a);
    HC12.write(a);   
  }

  while (true) {
    Serial.print("Write: ");
    Serial.println(counter);
    HC12.write(counter);
    counter = (counter - 96) % 26 + 97; 
    delay(50);
  }
}


