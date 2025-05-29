package se.su.inlupp;

public class EdgeClass<T> implements Edge<T>{
    private final T destination;
    private int weight;
    private final String name;


    public EdgeClass(T destination, String name, int weight ) {
        this.weight = weight;
        this.name = name;
        this.destination = destination;
    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void setWeight(int weight){
        this.weight = weight;
        if (weight < 0) {
        throw new IllegalArgumentException("No negative numbers allowed");
        }
    }

    @Override
     public T getDestination(){
        return destination;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return "Till " + destination + " med " + name + " tar " + weight;
    }
}
