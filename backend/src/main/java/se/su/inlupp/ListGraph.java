//PROG2 VT2025, Inlämningsuppgift, del 1
//Grupp 228
//Maja Johnsson majo9305
//Isabelle Johansson isjo5153

package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private final Map<T, HashSet<Edge<T>>> nodes = new HashMap<>();
  @Override
  public void add(T node) {
    nodes.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {

    if (!nodes.containsKey(node2) || !nodes.containsKey(node1)) {
      throw new NoSuchElementException ("Element is missing");
    }
    else if (weight < 0) {
      throw new IllegalArgumentException ("No negative values allowed");
    }

    add(node1);
    add(node2);
    Set<Edge<T>> fromNodes = nodes.get(node1);
    Set<Edge<T>> toNodes = nodes.get(node2);

    if(getEdgeBetween(node1, node2) != null) {
      throw new IllegalStateException("One edge between " + node1 + " and " + node2 + " already exists");
    }
    fromNodes.add(new EdgeClass<>(node2, name, weight));
    toNodes.add(new EdgeClass<>(node1, name, weight));
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (!nodes.containsKey(node2) || !nodes.containsKey(node1)) {
      throw new NoSuchElementException ("Element is missing");
    }
    if (weight < 0) {
      throw new IllegalArgumentException ("No negative values allowed");
    }
    Edge<T> edge1= getEdgeBetween(node1, node2);
    Edge<T> edge2= getEdgeBetween(node2, node1);
    edge1.setWeight(weight);
    edge2.setWeight(weight);
  }

  @Override
  public Set<T> getNodes() {
      return new HashSet<>(nodes.keySet());
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if (!nodes.containsKey(node)) {
      throw new NoSuchElementException ("Element is missing");
    }
    Collection<Edge<T>> edgesFrom = nodes.get(node);
    return edgesFrom;
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    //går igenom listan med noder
    for (T node : nodes.keySet()) {
      //om noden finns i listan, fortsätt
      if (node.equals(node1)) {
        //tar ut de edges som finns kopplade till noden
        Set<Edge<T>> edges = nodes.get(node);
        //går igenom listan med egdes
        for (Edge<T> edge : edges) {
          //kollar om destinationen för edgen stämmer överens med nod2,
          //isåfall returneras edgen
          if(edge.getDestination().equals(node2)) {
            return edge;
          }
        }
      }
    }
    if (!nodes.containsKey(node2) || !nodes.containsKey(node1)) {
      throw new NoSuchElementException ("Element is missing");
    }
    return null;
  }

  @Override
  public void disconnect(T node1, T node2) {
    if (!nodes.containsKey(node2) || !nodes.containsKey(node1)) {
      throw new NoSuchElementException ("Element is missing");
    }
    if (getEdgeBetween(node1, node2) == null) {
      throw new IllegalStateException("No edge between " + node1 + " and " + node2 + " exists");
    }
    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);
    for(T node : nodes.keySet()) {
      if (node.equals(node1)) {
        nodes.get(node).remove(edge1);
      }
      if (node.equals(node2)) {
        nodes.get(node).remove(edge2);
      }
    }
  }

  @Override
  public void remove(T node) {
    if (!nodes.containsKey(node)) {
      throw new NoSuchElementException ("Element is missing");
    }
    for (T other : nodes.keySet()) {
      if (getEdgeBetween(node, other) != null){
        disconnect(other, node);
      }
    }
    nodes.remove(node);
  }

  @Override
  public boolean pathExists(T from, T to) {
      Set<T> visitedNodes = new HashSet<>();
      return recursiveVisitAll(from, to, visitedNodes);
  }

  private boolean recursiveVisitAll(T from, T to, Set<T> visitedNodes) {
    visitedNodes.add(from);
    //om from är lika med to så har den hittat det den ska
    if (from.equals(to)) {
      return true;
    }
    //om from finns i listan
    if (nodes.containsKey(from)) {
      //går igenom alla edges från from
      for (Edge<T> edge : nodes.get(from)) {
        //om visistedNodes inte innehåller edgens destination
        if (!visitedNodes.contains(edge.getDestination())) {
          //om den är sann, returneras true
          if (recursiveVisitAll(edge.getDestination(), to, visitedNodes)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    List<Edge<T>> path = new ArrayList<>();
    if (pathExists(from, to)) {
      Map<T, T> connectedNodes = new HashMap<>();
      recursiveConnect(from, null, connectedNodes);
      //sätter current till to
      T current = to;
      //medans current inte är null och inte lika med from, kör
      while (current != null && !current.equals(from)) {
        //next sätts till connectedNodes.get(current)
        T next = connectedNodes.get(current);
        //tar fram edges från next till current
        Edge<T> edge = getEdgeBetween(next, current);
        //lägger till edge
        path.addFirst(edge);
        //sätter current till next
        current = next;
      }
      return path;
    }
    return null;
  }

  private void recursiveConnect(T to, T from, Map<T, T> connectedNodes) {
    connectedNodes.put(to, from);
    //tar fram alla edges från to
    for(Edge<T> edge : nodes.get(to)) {
      //om connectedNodes inte innehåller edgens destination
      if (!connectedNodes.containsKey(edge.getDestination())) {
        //skicka den till recursiveConnect med edge.getDestination() som to och to som from
        recursiveConnect(edge.getDestination(), to, connectedNodes);
      }
    }
  }

  @Override
  public String toString(){
    StringBuilder path = new StringBuilder();
    for(T node : nodes.keySet()) {
      Collection<Edge<T>> edgesFrom = getEdgesFrom(node);
      for (Edge<T> e : edgesFrom) {
        if (path.isEmpty()) {
          path.append(e.toString());
        }
        else {
          path.append(", ").append(e.toString());
        }
      }
    }
    for (T node : nodes.keySet()) {
      path.append(", ").append(node);
    }
    path.append("]");
    return path.toString();
  }
}
