package model;

import java.util.*;

public class Project {
    private String title;
    private String projectId;
    private String description;
    //project belongs to a project owner
    private HashMap<String, Integer> skillNeed;
    private ArrayList<String> members;
    private int leaderCount;
    private Set<String> types;
    private String ownerId;

    public Project(String projectId, String ownerId, String title,
                   String description, HashMap<String, Integer> skillNeed) {
        this.projectId = projectId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.skillNeed = skillNeed;
        leaderCount = 0;
        types = new HashSet<>();
        members = new ArrayList<>();
    }

    //constructor for testing swap
    public Project() {
    }

    public String getId() {
        return projectId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<String, Integer> getSkillNeed() {
        return skillNeed;
    }

    public ArrayList<String> getMembers() {
        return this.members;
    }

    public Set<String> getTypes() {
        return this.types;
    }

    public void addMember(String student) {
        members.add(student);
    }

    public void removeMember(String student) {
        members.remove(student);
    }

    public void updateType(Set<String> types) {
        this.types = types;
    }

    public void addLeader() {
        this.leaderCount++;
    }

    public void resetLeader() {
        this.leaderCount = 0;
    }

    public int getLeader() {
        return leaderCount;
    }

    public String toString() {
        return " " + projectId + " |" + title +
                " |" + description + " |Skills Need: " + skillNeed;
    }
}
