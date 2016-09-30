/* Sweep
 by BARRAGAN <http://barraganstudio.com> 
 This example code is in the public domain.

 modified 8 Nov 2013
 by Scott Fitzgerald
 http://arduino.cc/en/Tutorial/Sweep
*/ 

#include <Servo.h> 
 
Servo myservoL;  // create servo object to control a servo 
                // twelve servo objects can be created on most boards
Servo myservoR;
 
int pos = 0;    // variable to store the servo position 

//________SERIAL
char inData[80];
byte index = 0;
String message;

int posServoL;
int posServoR;
 
void setup() 
{ 
  posServoL = 110;
  posServoR = 105;
  myservoR.attach(9); //Bleu
  myservoL.attach(10); //Vert
  myservoL.write(posServoL);
  myservoR.write(posServoR);
  Serial.begin(9600);
} 
 
void loop() 
{ 


  

    myservoL.write(posServoL);
    myservoR.write(posServoR);
  
  
  delay(100);  
  
} 


