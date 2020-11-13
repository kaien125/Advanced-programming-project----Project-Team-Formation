package model;

public class PersonalityImbalanceException extends Exception{
    public PersonalityImbalanceException() {
        System.out.println("Different personality types cannot be less than three in the team.");
    }
}
