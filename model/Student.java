package model;

import java.util.*;

public class Student {
    private String studentId;
    private String firstName;
    private String surname;
    private String personality;
    private ArrayList<String> conflicts;
    private HashMap<String, Integer> skills;
    private LinkedHashMap<String, Integer> preferences;

    public Student(String studentId, String firstName, String surname,
                   HashMap<String, Integer> skills, String personality,
                   LinkedHashMap<String, Integer> preferences, ArrayList<String> conflicts) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.surname = surname;
        this.personality = personality;
        this.skills = skills;
        this.preferences = preferences;
        this.conflicts = conflicts;
    }

    public String getId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public HashMap<String, Integer> getSkills() {
        return skills;
    }

    public String getPersonality() {
        return personality;
    }

    public HashMap<String, Integer> getPreferences() {
        return preferences;
    }

    public ArrayList<String> getConf() {
        return conflicts;
    }

    public ArrayList<String> getConfNonSelf() {
        ArrayList<String> confNonSelf = new ArrayList<>();
        if (conflicts.size() == 2) {
            confNonSelf.add(conflicts.get(1));
        } else if (conflicts.size() == 3) {
            confNonSelf.add(conflicts.get(1));
            confNonSelf.add(conflicts.get(2));
        }
        return confNonSelf;
    }

    public String toString() {
        if (getConfNonSelf().size() == 0) {
            return " " + studentId + " " + firstName + " " + surname +
                    " |Personality Type: " + personality + " |Skills: "
                    + skills + " |Preferences: " +
                    preferences;
        } else {
            return " " + studentId + " " + firstName + " " + surname +
                    " |Personality Type: " + personality + " |Skills: "
                    + skills + " |Preferences: " +
                    preferences + " |Conflicts: " + getConfNonSelf();
        }
    }
}

