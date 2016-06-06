class Projo extends Display  {
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
  void draw() {
    if (actif) {
      stroke(cGreen);
      if (frameCount%2 == 0) {
        fill(255, 127);
      } else {
        noFill();
      }
    }  else {
      stroke(cRed);
      noFill();
    }
    pushMatrix();
    translate(pos.x, pos.y);
    size = 50+viewMin*1.6;
    rect(0, 0, size, size);
    popMatrix();
    line(pos.x, pos.y, prev.pos.x, prev.pos.y);
    //
    //
  }
  void drawText() {
    super.drawText();
    pushMatrix();
    translate(pos.x, pos.y);
    fill(0);
    ellipse(0, 0, sizeMin*1.6, sizeMin*1.6);
    fill(255);
    text("π["+decimal+"]:\n"+wait/1000+"\n\n"+nbrSec/1000+"s", 0, 0);
    fill(cMag);
    text("\n^", 0, 0);
    //text(id+" use decimal: "+wait+"\nprev is: "+prev.id+" and next is: "+next.id, 0, 20);
    popMatrix();
  }
  //
  void Rewind(int diff) {
    //
  }
  //
  void step(int diff) {
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
              if (random(1)< 0.5) {
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
  void ProjOn() {
    actif = true;
    // SERIAL ON
    /*if(id == 1){
     servoL_current = servoL_run;
     }else if(id == 2){
     servoR_current = servoR_run;
     }
     
     sendServoCommand();*/
  }
  void ProjOff()  {
    actif = false;
    // SERIAL OFF
    /*if(id == 1){
     servoL_current = servoL_stop;
     }else if(id == 2){
     servoR_current = servoR_stop;
     }
     
     sendServoCommand();*/
  }
  void TurnOff() {
    turnedOn = false;
    wait = 0;
    ProjOff();
  }
  void TurnOn() {
    turnedOn = true;
  }
  int getDec() {
    return decimal;
  }
}