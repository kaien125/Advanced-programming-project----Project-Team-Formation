package controller;

public class NotEmptyException extends Exception{
    public NotEmptyException() {
        System.out.println("Check box is not empty.");
    }
}
