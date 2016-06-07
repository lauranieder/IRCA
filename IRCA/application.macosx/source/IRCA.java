import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class IRCA extends PApplet {

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


Serial myPort;  

Projo projoL, projoR;
ArrayList<Display>displays = new ArrayList<Display>();
Display display;
String thePi[];
String splitPi[];
int cursor = 0;
float dst = 250;
int qtt = 6;
int cRed = color(255, 255, 0);
int cGreen = color(0, 255, 0);
int cMag = color(255, 0, 255);
String piView = "";
String piViewSub = "";
String piViewSubId = "";
String piPreView = "\u03c0 = 3. (...) "; // From \u03c0["+cursor+"] to \u03c0["+vCursor+"]: 
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

public void keyPressed() {
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
public void setup() {
  //fullScreen();
  
  
  rectMode(CENTER);
  frameRate(25);
  //textSize(40);
  //font = loadFont("Monaco-48.vlw");
  font = loadFont("SourceCodePro-Regular-14.vlw");
  textFont(font, 25);
  //
  thePi = loadStrings(nbrDecimalOfPi+".txt");
  splitPi = thePi[0].split("");
  //
 
  projoL = new Projo(-600, 100, 1);
  projoR = new Projo(600, 100, 2);
  display = new Display(0, -160, 0);
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
  myPort = new Serial(this, Serial.list()[2], 9600);
  //myPort = new Serial(this, "/dev/cu.usbmodem1411", 9600);

  //stop by default
  servoL_current = servoL_stop;
  servoR_current = servoR_stop;
  sendServoCommand();
}
//
public int[] nextPi() {
  int r = parseInt(splitPi[cursor]);
  int infos[] = {r, cursor};
  cursor ++;
  if (cursor>=splitPi.length) {
    cursor = 0;
  }
  //println("DECIMAL: "+infos[0]+" "+infos[1]);
  return(infos);
}
public void launch() {
}
//
public void draw() {
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
  //piView += "\n "+cursor+" of "+splitPi.length+" decimals of \u03c0 used ("+round(perc*100)+"%)";
  //
  background(0);
  //
  if (viewActivity) {
    textAlign(LEFT, TOP);
    text("rewind: "+rewind+"\nisRunning: "+isRunning+"\nmanualMode: "+manualMode, 0, 40);
  }
  //
  textAlign(CENTER, TOP);
  text("\u03c0 decimals from \u03c0["+cursorMin+"] to \u03c0["+cursorMax+"]\n"+piView, round(width/2), 40);
  fill(cMag);
  text("\n\n"+piViewSub, round(width/2), 40);
  fill(255);
  stroke(cMag);
  line(perc*width, 0, perc*width, height);

  pushMatrix();
  translate(perc*width, height);
  rotate(-PI/2);
  String percent = round(perc*100)+"% - "+cursor+" of "+nbrDecimalOfPi+" decimals of \u03c0 used";
  if (perc<0.5f) {
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
  text("A project by Sophie Dascal & Fragment.in", width/2, height-40);
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
  text("IRCA\n\nInfinite Rewind\nCinematographic Apparatus\n\n"+(millis()/1000)+"s elapsed from launch", 0, 340);
  //
  //text(frameRate+"FR" ,50, 50);
}
class Display {
  //
  int nbrSec = 540000;
  int nbrMin = 10000;
  int nbrMax = 10000;
  float div = 600*1.6f;
  PVector pos = new PVector();
  Display prev, other;
  float size;
  float timer = 0;
  float sizeCurrent, sizePrev, sizeMin, sizeAnime;
  float viewMin = 100*1.6f;
  boolean needRewind = false;
  boolean turnedOn = false;
  int id;
  //
  Display(float _x, float _y, int _id) {
    pos = new PVector(_x, _y);
    id = _id;
    //
    sizeMin = nbrMin/div+viewMin;
  }
  //
  //
  public void draw() {
      noFill();
    pushMatrix();
    translate(pos.x, pos.y);
    sizeCurrent = nbrSec/div+viewMin;
    ellipse(0, 0, sizeCurrent, sizeCurrent);
    //stroke(255, 0, 0);
    sizeMin = nbrMin/div+viewMin;
    ellipse(0, 0, sizeMin, sizeMin);
    //
    //sizeAnime +=1;
    if(sizeAnime>sizeCurrent) {
      sizeAnime = sizeMin;
    }
    if(sizeAnime<sizeMin) {
      sizeAnime = sizeCurrent;
    }
    ellipse(0, 0, sizeAnime, sizeAnime);
    //
    popMatrix();
    //
  }
  public void changeNbr(int n) {
    sizeAnime += n/5f;
    nbrSec += n;
  }
  public void step(int diff) {
    //println(diff);
  }
  public void Rewind(int diff) {
  }
  public void drawText() {
    pushMatrix();
    translate(pos.x, pos.y);
    fill(0);
    ellipse(0, 0, sizeMin, sizeMin);
    fill(255);
    text(nbrSec/1000+"s", 0, 0);
    //text(id+"\nprev is: "+prev.id+" and next is: "+next.id, 0, 20);
    popMatrix();
  }
  public void TurnOff() {
  }
  public void TurnOn() {
  }
  public void setPrev(Display _prev) {
    prev = _prev;
  }
  public void setOther(Display _other) {
    other = _other;
  }
  public int getDec() {
    return -1;
  }
}
class Projo extends Display  {
  //
  int diffTot = 0;
  int wait = 0;
  int decimal = 0;
  boolean actif = false;
  int limiteReserve = 20000;
  //
  Projo(float _x, float _y, int _id) {
    super(round(_x), round(_y), _id);
    nbrSec = 0;
  }
  //
  public void draw() {
    if (actif) {
      stroke(cGreen);
      if (frameCount%2 == 0) {
        fill(255, 127);
      } else {
        noFill();
      }
    }  else {
      stroke(cRed);
      noFill();
    }
    pushMatrix();
    translate(pos.x, pos.y);
    size = 50+viewMin*1.6f;
    rect(0, 0, size, size);
    popMatrix();
    line(pos.x, pos.y, prev.pos.x, prev.pos.y);
    //
    //
  }
  public void drawText() {
    super.drawText();
    pushMatrix();
    translate(pos.x, pos.y);
    fill(0);
    ellipse(0, 0, sizeMin*1.6f, sizeMin*1.6f);
    fill(255);
    text("\u03c0["+decimal+"]:\n"+wait/1000+"\n\n"+nbrSec/1000+"s", 0, 0);
    fill(cMag);
    text("\n^", 0, 0);
    //text(id+" use decimal: "+wait+"\nprev is: "+prev.id+" and next is: "+next.id, 0, 20);
    popMatrix();
  }
  //
  public void Rewind(int diff) {
    //
  }
  //
  public void step(int diff) {
    if (turnedOn) {
      if (rewind) {
        if (nbrSec >= diff) {
          prev.changeNbr(diff);
          changeNbr(-diff);
        } else {
          needRewind = false;
        }
      } else {
        if (actif) {
          //println(id+" PREV("+prev.id+") - ="+diff);
          //println(id+" NEXT("+next.id+") + ="+diff);
          prev.changeNbr(-diff);
          changeNbr(diff);
          //nbrSec-=diff;
        }
        //
        if (diffTot < wait) {
          diffTot+=diff;
        } else {
          diffTot = 0;
          //
          //
          int diffOther = other.nbrSec-nbrSec; // other = 60 ici = 50, diff = 10 // other = 50 ici = 60, diff = -10
          //
          if (prev.nbrSec > limiteReserve) {
            //println(id+" "+diffOther+" possible ?");
            //
            if (diffOther>=5000) {
              println(id+" "+diffOther+" devient inactif");
              ProjOn();
            } else if (diffOther <=-5000) {
              println(id+" "+diffOther+" devient actif");
              ProjOff();
            } else {
              println(id+" "+diffOther+" random actif");
              if (random(1)< 0.5f) {
                ProjOn();
              } else {
                ProjOff();
              }
            }
          } else {
            needRewind = true;
            ProjOff();
          }
          int infos[] = nextPi();
          wait = infos[0]*1000;
          decimal = infos[1];
        }
      }
    }
  }
  //
  //
  public void ProjOn() {
    actif = true;
    // SERIAL ON
    /*if(id == 1){
     servoL_current = servoL_run;
     }else if(id == 2){
     servoR_current = servoR_run;
     }
     
     sendServoCommand();*/
  }
  public void ProjOff()  {
    actif = false;
    // SERIAL OFF
    /*if(id == 1){
     servoL_current = servoL_stop;
     }else if(id == 2){
     servoR_current = servoR_stop;
     }
     
     sendServoCommand();*/
  }
  public void TurnOff() {
    turnedOn = false;
    wait = 0;
    ProjOff();
  }
  public void TurnOn() {
    turnedOn = true;
  }
  public int getDec() {
    return decimal;
  }
}

String lastCommande = "";
public void sendServoCommand() {
  String servoCommande = "2";
  servoCommande += separator;
  servoCommande += servoL_current;
  servoCommande += separator;
  servoCommande += servoR_current;
  servoCommande += endOfLine;
  //
  if (!lastCommande.equals(servoCommande)) {
    println(servoCommande+"  oldone: "+lastCommande);
    myPort.write(servoCommande);
    //myPort.write("2_110_105\n");
    lastCommande = servoCommande;
  }
}
// MOCHEEEEE
public void sendServoCommandDirect() {
  String servoCommande = "2";
  //
  servoCommande += separator;
  servoCommande += servoL_current;
  servoCommande += separator;
  servoCommande += servoR_current;
  servoCommande += endOfLine;
  //
  myPort.write(servoCommande);
}
  public void settings() {  size(1600, 1280);  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "IRCA" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
