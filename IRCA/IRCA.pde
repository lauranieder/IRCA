/*I.R.C.A, 2016
 
 Servo motor controller for Processing to Arduino
 ************************************************
 Correct Data Format 
 0_position //only left motor
 1_position // only right motor
 2_positionLeft_positionRight //both motors
 \n //+ End of line 
 ************************************************
 Left : Green
 Right : Blue
 ************************************************
 Serial port is "/dev/cu.usbmodem1411" by default
 
 Marc Dubois + Laura Perrenoud
 */

import processing.serial.*;
Serial myPort;  

Projo projoL, projoR;
ArrayList<Display>displays = new ArrayList<Display>();
Display display;
String thePi[];
String splitPi[];
int cursor = 0;
float dst = 250;
int qtt = 6;
color cRed = color(255, 255, 0);
color cGreen = color(0, 255, 0);
color cMag = color(255, 0, 255);
String piView = "";
String piViewSub = "";
String piViewSubId = "";
String piPreView = "π = 3. (...) "; // From π["+cursor+"] to π["+vCursor+"]: 
String piPreEmpt = "             ";
String piPostView = " (...)";
String piPostEmpt = "      ";
PFont font;
int cursorMin = 0;
int cursorMax = 0;
int nbrDecimalOfPi = 1000000; // 100 or 1000 or 1000000
boolean rewind = false;
boolean isRunning = false;
boolean manualMode = false;
boolean viewActivity = false;
//
int timer = 0;

int servoL_stop = 110 ;
int servoR_stop = 105;
int servoL_rewind = 155;
int servoR_rewind = 150;
int servoL_runNoLight = 65;
int servoR_runNoLight = 60;
int servoL_run = 25;
int servoR_run = 20;
int servoL_current;
int servoR_current;

String endOfLine = "\n";
String separator = "_";

void keyPressed() {
  if (key == CODED) {
    if (!manualMode) {
      if (keyCode == UP) {
        isRunning = !isRunning;
        if (!isRunning) {
          projoL.TurnOff();
          projoR.TurnOff();
        } else {
          projoL.TurnOn();
          projoR.TurnOn();
        }
      }

      if (keyCode == RIGHT) {
        rewind = false;
        projoL.needRewind = false;
        projoR.needRewind = false;
      }
      if (keyCode == LEFT) {
        rewind = true;
        projoL.needRewind = true;
        projoR.needRewind = true;
      }
    }
  }
  if (key == ' ') {
    if (!isRunning) {
      manualMode = !manualMode;
    }
  }
  if (key == 'v') {
    viewActivity = !viewActivity;
  }
  if (manualMode) {
    if (key == 'q') {
      servoL_current = servoL_rewind;
      sendServoCommand();
    }
    if (key == 'w') {
      servoL_current = servoL_stop;
      sendServoCommand();
    }
    if (key == 'e') {
      servoL_current = servoL_run;
      sendServoCommand();
    }
    if (key == 'a') {
      servoR_current = servoR_rewind;
      sendServoCommand();
    }
    if (key == 's') {
      servoR_current = servoR_stop;
      sendServoCommand();
    }
    if (key == 'd') {
      servoR_current = servoR_run;
      sendServoCommand();
    }
  }
}
//
void setup() {
  //fullScreen();
  size(1600, 1280);
  noSmooth();
  rectMode(CENTER);
  frameRate(25);
  //textSize(40);
  //font = loadFont("Monaco-48.vlw");
  font = loadFont("SourceCodePro-Regular-14.vlw");
  textFont(font, 30);
  //
  thePi = loadStrings(nbrDecimalOfPi+".txt");
  splitPi = thePi[0].split("");
  //
  projoL = new Projo(-600, 0, 1);
  projoR = new Projo(600, 0, 2);
  display = new Display(0, -200, 0);
  projoL.setPrev(display);
  projoR.setPrev(display);
  projoL.setOther(projoR);
  projoR.setOther(projoL);
  //
  displays.add(projoL);
  displays.add(projoR);
  displays.add(display);//

  // List all the available serial ports:
  printArray(Serial.list());

  // Open the port you are using at the rate you want VERIFY !
  //myPort = new Serial(this, Serial.list()[1], 9600);
  myPort = new Serial(this, "/dev/cu.usbmodem1411", 9600);

  //stop by default
  servoL_current = servoL_stop;
  servoR_current = servoR_stop;
  sendServoCommand();
}
//
int[] nextPi() {
  int r = parseInt(splitPi[cursor]);
  int infos[] = {r, cursor};
  cursor ++;
  if (cursor>=splitPi.length) {
    cursor = 0;
  }
  //println("DECIMAL: "+infos[0]+" "+infos[1]);
  return(infos);
}
void launch() {
}
//
void draw() {
  //
  piView = "";
  piViewSub = "";
  piViewSubId = "";
  int tempCursor = 0;
  int vCursor = 0;
  cursorMax = cursor;
  cursorMin = cursor;
  for (int i=-20; i<=20; i++) {
    tempCursor = cursor+i;
    vCursor = tempCursor;
    //

    //
    if (tempCursor<splitPi.length && tempCursor>= 0) {
      boolean empty = true;
      piView += splitPi[tempCursor];
      for (Display d : displays) {
        if (d.getDec() == tempCursor) {
          piViewSub += "^";
          piViewSubId += d.id/2;
          empty = false;
        }
      }
      if (empty) {
        piViewSub += " ";
        piViewSubId += " ";
      }
      // min et max
      //println("tempCursor: "+tempCursor);
      if (tempCursor > cursorMax) { 
        cursorMax = tempCursor;
      }
      if (tempCursor < cursorMin) { 
        cursorMin = tempCursor;
      }
    }
  }
  /*if(cursorMax < splitPi.length) {
   cursorMax = tempCursor;
   } else {
   cursorMax = splitPi.length-1;
   }
   if(cursorMin >= 0) {
   cursorMin = tempCursor;
   } else {
   cursorMin = 0;
   }*/
  float perc = (cursor+0f)/splitPi.length;
  piView = piPreView+piView+piPostView;

  piViewSub = piPreEmpt+piViewSub+piPostEmpt;
  //piView += "\n "+cursor+" of "+splitPi.length+" decimals of π used ("+round(perc*100)+"%)";
  //
  background(0);
  //
  if (viewActivity) {
    textAlign(LEFT, TOP);
    text("rewind: "+rewind+"\nisRunning: "+isRunning+"\nmanualMode: "+manualMode, 0, 40);
  }
  //
  textAlign(CENTER, TOP);
  text("π decimals from π["+cursorMin+"] to π["+cursorMax+"]\n"+piView, round(width/2), 40);
  fill(cMag);
  text("\n\n"+piViewSub, round(width/2), 20);
  fill(255);
  stroke(cMag);
  line(perc*width, 0, perc*width, height);

  pushMatrix();
  translate(perc*width, height);
  rotate(-PI/2);
  String percent = round(perc*100)+"% - "+cursor+" of "+nbrDecimalOfPi+" decimals of π used";
  if (perc<0.5) {
    textAlign(LEFT, TOP);
    text(percent, 0, 1);
  } else {
    textAlign(LEFT, BOTTOM);
    text(percent, 0, -1);
  }
  popMatrix();
  textAlign(CENTER, CENTER);
  //
  //text("", width/2, 20);
  //
  /*if(timer != 0 && millis() > timer) {
   timer += 1000;
   launch();
   }*/
  //
  fill(255);
  textAlign(CENTER, BOTTOM);
  text("A project by Sophie Dascal & Fragment.in", width/2, height-20);
  textAlign(CENTER, CENTER);
  //
  translate(width/2, height/2);
  //
  int diff = millis()-timer;
  timer = millis();
  //
  //
  noFill();
  for (Display d : displays) {
    d.step(diff);
    stroke(255);
    d.draw();
  }
  if (projoL.needRewind && projoR.needRewind) {
    rewind = true;
  } else if (!projoL.needRewind && !projoR.needRewind) {
    rewind = false;
  }
  //
  if (!manualMode) {
    if (isRunning) {
      if (projoL.actif) {
        servoL_current = servoL_run;
      } else {
        servoL_current = servoL_stop;
      }
      if (projoR.actif) {
        servoR_current = servoR_run;
      } else {
        servoR_current = servoR_stop;
      }
      if (rewind) {
        if (projoR.needRewind) {
          servoR_current = servoR_rewind;
        }
        if (projoL.needRewind) {
          servoL_current = servoL_rewind;
        }
      }
    } else {
      servoL_current = servoL_stop;
      servoR_current = servoR_stop;
    }
    sendServoCommand();
  }
  //
  for (Display d : displays) {
    fill(255);
    d.drawText();
  }
  text("IRCA\n\nInfinite Rewind\nCinematographic Apparatus\n\n"+(millis()/1000)+"s elapsed from launch", 0, 200);
  //
  //text(frameRate+"FR" ,50, 50);
}