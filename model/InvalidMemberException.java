package model;

public class InvalidMemberException extends Exception{
    public InvalidMemberException() {
        System.out.println("Student exists in another team.");
    }
}
