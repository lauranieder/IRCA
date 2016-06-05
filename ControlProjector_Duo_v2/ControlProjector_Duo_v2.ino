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


  message = "";
  while(Serial.available() > 0){
     char aChar = Serial.read();
     //______________________PARSING DATA_________________________
     if(aChar == '\n'){
        for(int i = 0;i<index;i++){
          message += inData[i]; 
        }
        
        Serial.flush();
        //Serial.println(message);//debug

        int separatorIndex = message.indexOf('_');
        int secondSeparatorIndex = message.indexOf('_', separatorIndex+1);
        
        //Data not correct
        if(separatorIndex == -1){
          //Serial.println("Wrong data format");
          //Correct data format

          
        }if(separatorIndex != -1 && secondSeparatorIndex ==-1){
          //Serial.println("1 motor instruction");
          //Correct data format
          String firstValue = message.substring(0, separatorIndex);
          String secondValue = message.substring(separatorIndex+1);
          int motorValue = firstValue.toInt(); 
          int rotationValue = secondValue.toInt(); 
          if(rotationValue >= 0 && rotationValue <= 180){
            if(motorValue == 0){
              posServoL = rotationValue;
 
              //myservoL.write(rotationValue);      
            }else if(motorValue == 1){
               posServoR = rotationValue;
              //myservoR.write(rotationValue);  
            }
          }
        }if(separatorIndex != -1 && secondSeparatorIndex !=-1){
          //Serial.println("2 motor instruction");
          //Correct data format
          String firstValue = message.substring(0, separatorIndex);
          String secondValue = message.substring(separatorIndex+1);
          String thirdValue = message.substring(secondSeparatorIndex+1);
          int motorValue = firstValue.toInt(); 
          int rotationValueL = secondValue.toInt(); 
          int rotationValueR = thirdValue.toInt(); 
          if(rotationValueL >= 0 && rotationValueL <= 180 && rotationValueR >= 0 && rotationValueR <= 180){
            if(motorValue == 2){
              posServoL = rotationValueL;
              posServoR = rotationValueR;
              //myservoL.write(rotationValueL);
              //myservoR.write(rotationValueR);
                   
            }
          }
        }else{ 
          
        }
        index = 0;
        inData[index] = NULL;
     //______________________READING DATA_________________________
     }else{
      //Serial.println("..");
        inData[index] = aChar;
        index++;
        inData[index] = '\0'; // Keep the string NULL terminated
     }
     //____________________________________________________________
  }

    myservoL.write(posServoL);
    myservoR.write(posServoR);
  
  
  delay(100);  
  
} 


