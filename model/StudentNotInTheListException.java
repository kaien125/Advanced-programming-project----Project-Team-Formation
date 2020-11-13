package model;

public class StudentNotInTheListException extends Exception{
    public StudentNotInTheListException() {
        System.out.println("Student is not in the list.");
    }
}
