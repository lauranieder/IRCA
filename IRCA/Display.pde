class Display {
  //
  int nbrSec = 540000;
  int nbrMin = 10000;
  int nbrMax = 10000;
  float div = 600;
  PVector pos = new PVector();
  Display prev, other;
  float size;
  float timer = 0;
  float sizeCurrent, sizePrev, sizeMin, sizeAnime;
  float viewMin = 100;
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
  void draw() {
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
  void changeNbr(int n) {
    sizeAnime += n/5f;
    nbrSec += n;
  }
  void step(int diff) {
    //println(diff);
  }
  void Rewind(int diff) {
  }
  void drawText() {
    pushMatrix();
    translate(pos.x, pos.y);
    fill(0);
    ellipse(0, 0, sizeMin, sizeMin);
    fill(255);
    text(nbrSec/1000+"s", 0, 0);
    //text(id+"\nprev is: "+prev.id+" and next is: "+next.id, 0, 20);
    popMatrix();
  }
  void TurnOff() {
  }
  void TurnOn() {
  }
  void setPrev(Display _prev) {
    prev = _prev;
  }
  void setOther(Display _other) {
    other = _other;
  }
  int getDec() {
    return -1;
  }
}