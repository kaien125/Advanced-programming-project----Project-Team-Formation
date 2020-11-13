package model;

public class NoLeaderException extends Exception{
    public NoLeaderException() {
        System.out.println("At least one leader in the team.");
    }
}
