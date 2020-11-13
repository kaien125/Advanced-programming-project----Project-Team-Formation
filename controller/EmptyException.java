package controller;

public class EmptyException extends Exception{
    public EmptyException() {
        System.out.println("Check box is empty.");
    }
}
