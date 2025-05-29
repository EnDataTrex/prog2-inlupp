//PROG2 VT2025, Inlämningsuppgift, del 1
//Grupp 228
//Maja Johnsson majo9305
//Isabelle Johansson isjo5153

package se.su.inlupp;

public interface Edge<T> {

  int getWeight();

  void setWeight(int weight);

  T getDestination();

  String getName();
}
