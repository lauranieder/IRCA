String lastCommande = "";
void sendServoCommand() {
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
void sendServoCommandDirect() {
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