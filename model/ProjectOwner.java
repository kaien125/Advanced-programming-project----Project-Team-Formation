package model;

public class ProjectOwner {
    private String firstName;
    private String surname;
    private String ownerId;
    private String role;
    private String email;
    private String companyId;

    public ProjectOwner(String ownerId, String companyId, String firstName, String surname, String role, String email) {
        this.ownerId = ownerId;
        this.companyId = companyId;
        this.firstName = firstName;
        this.surname = surname;
        this.role = role;
        this.email = email;
    }


    public String getId() {
        return ownerId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
}
