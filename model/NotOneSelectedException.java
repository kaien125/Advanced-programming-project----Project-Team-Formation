package model;

public class NotOneSelectedException extends Exception{
    public NotOneSelectedException() {
        System.out.println("At least one leader in the team.");
    }
}
