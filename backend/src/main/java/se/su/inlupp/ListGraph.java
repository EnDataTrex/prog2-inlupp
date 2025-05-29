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
    if (from.equals(to)) {
      return true;
    }
    if (nodes.containsKey(from)) {
      for (Edge<T> edge : nodes.get(from)) {
        if (!visitedNodes.contains(edge.getDestination())) {
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
      T current = to;
      while (current != null && !current.equals(from)) {
        T next = connectedNodes.get(current);
        Edge<T> edge = getEdgeBetween(next, current);
        //TODO tror det löste sig om man bara la addFirst istället för bara add
        path.addFirst(edge);
        current = next;
      }
      return path;
    }
    return null;
  }

  private void recursiveConnect(T to, T from, Map<T, T> connectedNodes) {
    connectedNodes.put(to, from);
    for(Edge<T> edge : nodes.get(to)) {
      if (!connectedNodes.containsKey(edge.getDestination())) {
        recursiveConnect(edge.getDestination(), to, connectedNodes);
      }
    }
  }
  @Override
  public String toString(){
    String path = "";
    for(T node : nodes.keySet()) {
      Collection<Edge<T>> edgesFrom = getEdgesFrom(node);
      for (Edge<T> e : edgesFrom) {
        if (path.isEmpty()) {
          path += e.toString();
          //path += "[till " + e.getDestination() + " med " + e.getName() + " tar " + e.getWeight();
        }
        else {
          //kan vi inte bara använda oss av edges toString?
          path += ", " + e.toString();
          //path += ", till " + e.getDestination() + " med " + e.getName() + " tar " + e.getWeight();
        }
      }
    }
    for (T node : nodes.keySet()) {
      path += ", " + node;
    }
    path += "]";
    return path;
  }
}
