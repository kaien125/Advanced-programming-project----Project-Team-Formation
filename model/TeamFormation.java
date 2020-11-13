package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toMap;

public class TeamFormation {
    private static TeamFormation instance;
    //store information collections
    private Map<String, Student> students;
    private Map<String, Project> projects;
    private Map<String, Company> companies;
    private Map<String, ProjectOwner> projectOwners;
    private Set<String> studSelected;
    private Set<String> studAvl;
    private Stack<ArrayList<String>> projectStack;
    private Stack<ArrayList<String>> studentStack;
    private static double originObjective;

    //singleton pattern to ensure class has only one instance
    private TeamFormation() {
        students = new LinkedHashMap<>();
        projects = new TreeMap<>();
        companies = new TreeMap<>();
        projectOwners = new TreeMap<>();
        studSelected = new LinkedHashSet<>();
        studAvl = new TreeSet<>();
        projectStack = new Stack<>();
        studentStack = new Stack<>();
        originObjective = 0.0;
    }

    //return the only instance
    public static TeamFormation getInstance() {
        if (instance == null) {
            instance = new TeamFormation();
        }
        return instance;
    }

    public Map<String, Student> getStudentList() {
        return students;
    }

    public Map<String, Project> getProjectList() {
        return projects;
    }

    public Set<String> getStudents() {
        return studSelected;
    }

    //empty set for storing added and available students
    public void resetStudents() {
        studSelected = new LinkedHashSet<>();
        studAvl = new TreeSet<>();
    }

    //empty the stack
    public void resetStack() {
        projectStack = new Stack<>();
        studentStack = new Stack<>();
    }

    // A handle to the controller
    public void setController() {
    }

    //create a new company object
    public void loadCompany() {
        Stream<String> rows = null;
        try {
            rows = Files.lines(Paths.get("companies.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        companies = rows.map(x -> x.split(","))
                .collect(toMap(x -> x[0],
                        x -> new Company(x[0], x[1], x[2], x[3], x[4])));
        rows.close();
    }

    //create a projectOwner object
    public void loadProjectOwner() {
        Stream<String> rows = null;
        try {
            rows = Files.lines(Paths.get("project owners.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        projectOwners = rows.map(x -> x.split(","))
                .collect(toMap(x -> x[0],
                        x -> new ProjectOwner(x[0], x[1], x[2], x[3], x[4], x[5])));
        rows.close();
    }

    //create a project object
    public void loadProject() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("projects.csv"));
            String projectEntry;
            while ((projectEntry = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(projectEntry, ",");
                while (st.countTokens() >= 11) {
                    String projectId = st.nextToken();
                    String ownerId = st.nextToken();
                    String title = st.nextToken();
                    String description = st.nextToken();
                    //read skills to map
                    HashMap<String, Integer> skillNeed = new HashMap<>();
                    for (int i = 0; i < 4; i++) {
                        skillNeed.put(st.nextToken(), Integer.parseInt(st.nextToken()));
                    }

                    Project newProject = new Project(projectId, ownerId, title, description, skillNeed);
                    this.projects.put(projectId, newProject);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file projects.csv");
        } catch (IOException e) {
            System.out.println("Something wrong.");
        }
    }

    public void loadStudent() {
        //read student information from file
        //create student objects
        try {
            BufferedReader reader = new BufferedReader(new FileReader("students.csv"));
            String studentEntry;
            while ((studentEntry = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(studentEntry, ",");
                while (st.countTokens() >= 1) {
                    String studentId = st.nextToken();
                    String firstName = st.nextToken();
                    String surname = st.nextToken();

                    //save student skills to map
                    HashMap<String, Integer> skills = new HashMap<>();
                    for (int i = 0; i < 4; i++) {
                        skills.put(st.nextToken(), Integer.parseInt(st.nextToken()));
                    }

                    String personality = st.nextToken();

                    //save student preferences to map
                    LinkedHashMap<String, Integer> preferences = new LinkedHashMap<>();
                    for (int i = 4; i > 0; i--) {
                        preferences.put(st.nextToken(), i);
                    }

                    //save conflicts to array list
                    ArrayList<String> conflicts = new ArrayList<>();
                    conflicts.add(studentId);
                    for (int i = 0; i < 2; i++) {
                        if (st.hasMoreTokens()) {
                            conflicts.add(st.nextToken());
                        }
                    }

                    //create student objects and save to a map
                    Student newStudent = new Student(studentId, firstName, surname,
                            skills, personality, preferences, conflicts);
                    this.students.put(newStudent.getId(), newStudent);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file students.csv");
        } catch (IOException e) {
            System.out.println("Something wrong.");
        }
    }


    public void addStudent(String studentId, String projectId) throws StudentConflictException,
            RepeatedMemberException, InvalidMemberException, PersonalityImbalanceException,
            NoLeaderException, NoInputException, StudentNotInTheListException {
        if (studentId.equals("")) {
            throw new NoInputException();
        }
        Project project = projects.get(projectId);
        ArrayList<String> members = project.getMembers();
        Student student = students.get(studentId);
        //check exceptions
        inListCheck(students.keySet(), studentId);
        confCheck(student, members);
        repeatCheck(studentId, members);
        unvalibCheck(studSelected, studentId);
        personalCheck(student, project);
        //add student to project
        project.addMember(studentId);
        //update personality types in project
        setPersonality(project);
        //update student lists
        studSelected.add(studentId);
        studAvl.removeAll(studSelected);
    }

    public void removeStudent(String studentId, String projectId) {
        Project project = projects.get(projectId);
        //remove member from project
        project.removeMember(studentId);
        ArrayList<String> members = project.getMembers();
        //update personality types according to new members
        Set<String> types = new HashSet<>();
        project.resetLeader();
        for (String s : members) {
            types.add(students.get(s).getPersonality());
            if (students.get(s).getPersonality().equals("A")) {
                project.addLeader();
            }
        }
        project.updateType(types);
    }

    //swap students and put to stack for undo
    public void swap(ArrayList<String> studentSwap, ArrayList<String> projectSwap)
            throws NoLeaderException, StudentConflictException, PersonalityImbalanceException {
        swapStudent(studentSwap, projectSwap);
        toStack(studentSwap, projectSwap);
        System.out.println("Swapping " + studentSwap.get(0) + " and " + studentSwap.get(1));
    }

    public void swapStudent(ArrayList<String> studentSwap, ArrayList<String> projectSwap)
            throws StudentConflictException, PersonalityImbalanceException, NoLeaderException {
        //get students and projects from list
        String studentId1 = studentSwap.get(0);
        String studentId2 = studentSwap.get(1);
        String projectId1 = projectSwap.get(0);
        String projectId2 = projectSwap.get(1);
        Project project1 = projects.get(projectId1);
        Project project2 = projects.get(projectId2);
        //use temp sets of members for checking
        ArrayList<String> members1 = new ArrayList<>();
        ArrayList<String> members2 = new ArrayList<>();
        //create test project objects for checking
        Project test1 = new Project();
        Project test2 = new Project();
        //add studentId to test project objects except the one to be swapped
        for (String s : project1.getMembers()) {
            if (!s.equals(studentId1))
                members1.add(s);
        }
        for (String s : project2.getMembers()) {
            if (!s.equals(studentId2))
                members2.add(s);
        }
        test1.setMembers(members1);
        test2.setMembers(members2);
        Student student1 = students.get(studentId1);
        Student student2 = students.get(studentId2);
        //set personality types for testing project objects
        setPersonality(test1);
        setPersonality(test2);
        //check conflict
        confCheck(student1, members2);
        confCheck(student2, members1);
        //check personality types
        personalCheck(student1, test2);
        personalCheck(student2, test1);
        //remove students from projects
        removeStudent(studentId1, projectId1);
        removeStudent(studentId2, projectId2);
        //add students to projects
        project1.addMember(studentId2);
        project2.addMember(studentId1);
    }

    //set project members' personality
    public void setPersonality(Project project) {
        Set<String> typesSet = new HashSet<>();
        project.resetLeader();
        for (String s : project.getMembers()) {
            typesSet.add(students.get(s).getPersonality());
            if (students.get(s).getPersonality().equals("A")) {
                project.addLeader();
            }
        }
        project.updateType(typesSet);
    }

    //put array lists to stacks for undo
    public void toStack(ArrayList<String> studentSwap, ArrayList<String> projectSwap) {
        studentStack.push(studentSwap);
        projectStack.push(projectSwap);
    }

    //undo swap, return swapped students list
    public ArrayList<String> undo() {
        ArrayList<String> selectedProject;
        ArrayList<String> selectedStudentId = new ArrayList<>();
        if (studentStack.size() > 0 && projectStack.size() > 0) {
            selectedStudentId = studentStack.pop();
            selectedProject = projectStack.pop();
            //swap students' position to the position of after swapping
            itemSwap(selectedStudentId);
            //swap students
            try {
                swapStudent(selectedStudentId, selectedProject);
                System.out.println("Swapping back " + selectedStudentId.get(0) +
                        " and " + selectedStudentId.get(1));

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Situation changed, cannot undo.");
            }
        } else {
            System.out.println("No swaps can be undone.");
        }
        return selectedStudentId;
    }

    //check whether there is a leader or at least 3 different personality types in a project
    public void personalCheck(Student student, Project project)
            throws NoLeaderException, PersonalityImbalanceException {
        String type = student.getPersonality();
        HashSet<String> types = (HashSet<String>) project.getTypes();
        //check when adding the 4th student
        if (project.getMembers() != null && project.getMembers().size() == 3) {
            //check if there will be a leader in team
            if (project.getLeader() == 0 && !type.equals("A")) {
                throw new NoLeaderException();
            }
            //check if there will be at least 3 types in team
            if (types.size() <= 2 && types.contains(type)) {
                throw new PersonalityImbalanceException();
            }
        }
    }

    //check conflicts
    public void confCheck(Student student, ArrayList<String> members)
            throws StudentConflictException {
        ArrayList<String> conf = student.getConf();
        for (String id : members) {
            if (conf.contains(id) || students.get(id).getConf().contains(student.getId())) {
                throw new StudentConflictException();
            }
        }
    }

    //methods to check exceptions
    public void repeatCheck(String studentId, ArrayList<String> members)
            throws RepeatedMemberException {
        if (members != null && members.contains(studentId)) {
            throw new RepeatedMemberException();
        }
    }

    //check if student id exists
    public void inListCheck(Set<String> students, String id) throws StudentNotInTheListException {
        if (!students.contains(id)) {
            throw new StudentNotInTheListException();
        }
    }

    //check whether the student has been added
    public void unvalibCheck(Set<String> students, String id) throws InvalidMemberException {
        if (students.contains(id)) {
            throw new InvalidMemberException();
        }
    }

    public interface TeamAvg {
        double calAvg (String projectId);
    }

    //count project id occurrences in team members' 1st and 2nd preference
    TeamAvg preferPerc = (String project) -> {
        int count = 0;
        ArrayList<String> members = projects.get(project).getMembers();
        for (String studId : members) {
            HashMap<String, Integer> preference = students.get(studId).getPreferences();

            if (preference.get(project) != null && preference.get(project) > 2) {
                count++;
            }
        }
        return (Math.round((double) count / members.size() * 100))/100.00;
    };

    public TeamAvg getPreferPerc(){
        return preferPerc;
    }


    //calculate average skill score of project
    TeamAvg skillAvgTeam = (String projectId) -> {
        HashMap<String, Double> skillAvg = skillAvg(projectId);
        double sum = 0;
        for (double i : skillAvg.values()) {
            sum += i;
        }
        return (sum / 4);
    };

    public TeamAvg getSkillAvgTeam(){
        return skillAvgTeam;
    }

    //average skills scores of student
    public double skillAvgStud(Student student) {
        HashMap<String, Integer> skills = student.getSkills();
        double sum = 0.0;
        for (int i : skills.values()) {
            sum += i;
        }
        return sum / 4;
    }

    //average skills scores of team members
    public HashMap<String, Double> skillAvg(String projectId) {
        Project project = projects.get(projectId);

        HashMap<String, Double> avgSkill = new HashMap<>();
        double totalP = 0;
        double totalA = 0;
        double totalN = 0;
        double totalW = 0;

        ArrayList<String> members = project.getMembers();
        for (String studId : members) {
            HashMap<String, Integer> skills = students.get(studId).getSkills();
            totalP += skills.get("P");
            totalA += skills.get("A");
            totalN += skills.get("N");
            totalW += skills.get("W");
        }

        avgSkill.put("P", (double) Math.round(totalP / 4 * 100) / 100);
        avgSkill.put("A", (double) Math.round(totalA / 4 * 100) / 100);
        avgSkill.put("N", (double) Math.round(totalN / 4 * 100) / 100);
        avgSkill.put("W", (double) Math.round(totalW / 4 * 100) / 100);

        return avgSkill;
    }


    //calculate total short fall score of a project
    TeamAvg shortFalSco = (String projectId) -> {
        Project project = projects.get(projectId);
        HashMap<String, Integer> skillNeed = project.getSkillNeed();
        HashMap<String, Double> skillsupp = skillAvg(projectId);
        ArrayList<Double> skillShortList = new ArrayList<>();
        double skillShort = 0;

        double shortP = (double) skillNeed.get("P") - skillsupp.get("P");
        double shortA = (double) skillNeed.get("A") - skillsupp.get("A");
        double shortN = (double) skillNeed.get("N") - skillsupp.get("N");
        double shortW = (double) skillNeed.get("W") - skillsupp.get("W");

        skillShortList.add(shortP);
        skillShortList.add(shortA);
        skillShortList.add(shortN);
        skillShortList.add(shortW);

        for (double i : skillShortList) {
            if (i >= 0) {
                skillShort += i;
            }
        }
        return skillShort;
    };

    public TeamAvg getShortFalSco() {
        return shortFalSco;
    }

    //calculate short fall score of a student in a project
    public int studShortFalSco(Student student, Project project) {
        HashMap<String, Integer> skillNeed = project.getSkillNeed();
        HashMap<String, Integer> skillsupp = student.getSkills();
        ArrayList<Integer> skillShortList = new ArrayList<>();
        int skillShort = 0;

        int shortP = skillNeed.get("P") - skillsupp.get("P");
        int shortA = skillNeed.get("A") - skillsupp.get("A");
        int shortN = skillNeed.get("N") - skillsupp.get("N");
        int shortW = skillNeed.get("W") - skillsupp.get("W");

        skillShortList.add(shortP);
        skillShortList.add(shortA);
        skillShortList.add(shortN);
        skillShortList.add(shortW);

        for (int i : skillShortList) {
            if (i >= 0) {
                skillShort += i;
            }
        }
        return skillShort;
    }

    //calculate standard deviation
    public double SdCalculator(double sum, int n, ArrayList<Double> list) {
        double avg = sum / n;
        double sumSqr = 0;
        for (double i : list) {
            sumSqr += (i - avg) * (i - avg);
        }
        return Math.sqrt(sumSqr / n);
    }


    //calculate preference standard deviation
    public double preferSd(){
        return teamSd(preferPerc);
    }

    //calculate standard deviation of skill competency
    public double skillSd() {
        return teamSd(skillAvgTeam);
    }

    //calculate standard deviation of skill shortfall
    public double shortFalSd() {
        return teamSd(shortFalSco);
    }


    public double teamSd(TeamAvg teamAvg) {
        double sum = 0;
        ArrayList<Double> list = new ArrayList<>();
        for (String projectId : projects.keySet()) {
            list.add(teamAvg.calAvg(projectId));
            sum += teamAvg.calAvg(projectId);
        }
        return Math.round(SdCalculator(sum, 5, list) * 10000.0) / 10000.0;
    }


    //save data to database
    public void save() {
        //assign address of database
        String url = "jdbc:sqlite:database.db";
        try {
            //build connection to database
            Connection conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement();
            //save companies to database
            company2db(st);
            //save project owners to database
            projectOwner2db(st);
            //save projects to database
            project2db(st);
            //save students to database
            student2db(conn, st);
            //close connection and statement
            st.close();
            conn.close();
        } catch (SQLException se) {
            System.out.println("SQLError: " + se.getMessage() + " code: " +
                    se.getErrorCode());
        }
    }

    //save company data to database
    public void company2db(Statement st) throws SQLException {
        //save companies to database
        st.executeUpdate("drop table if exists Companies");
        //set companyId primary key
        st.executeUpdate("create table Companies (companyId text primary key, " +
                "companyName text, abnNum integer, url text, address text);");
        for (Company c : companies.values()) {
            st.executeUpdate("insert into Companies values('" + c.getId() + "','"
                    + c.getCompanyName() + "','" + c.getAbnNum() + "','"
                    + c.getUrl() + "','" + c.getAddress() + "');");
        }

    }

    //save project owner data to database
    public void projectOwner2db(Statement st) throws SQLException {
        //save project owners to database
        st.executeUpdate("drop table if exists ProjectOwners");
        //set ownerId primary key, companyId foreign key
        st.executeUpdate("create table ProjectOwners (ownerId text primary key, " +
                "companyId text, firstName text, surname text, role text, " +
                "email,foreign key (companyId) references Companies(companyId));");
        for (ProjectOwner p : projectOwners.values()) {
            st.executeUpdate("insert into ProjectOwners values('" + p.getId() + "','"
                    + p.getCompanyId() + "','" + p.getFirstName() + "','"
                    + p.getSurname() + "','" + p.getRole() + "','" + p.getEmail() + "');");
        }
    }

    //save project data to database
    public void project2db(Statement st) throws SQLException {
        //save projects to database
        st.executeUpdate("drop table if exists Projects");
        //set projectId primary key, ownerId foreign key
        st.executeUpdate("create table Projects (projectId text primary key, " +
                "ownerId text, title text, description text, " +
                "foreign key (ownerId) references ProjectOwners(ownerId));");
        for (Project p : projects.values()) {
            st.executeUpdate("insert into Projects values('" + p.getId() + "','"
                    + p.getOwnerId() + "','" + p.getTitle() + "','"
                    + p.getDescription() + "');");
        }
    }

    //save student data to database
    public void student2db(Connection conn, Statement st) throws SQLException {
        //save students to database
        st.executeUpdate("drop table if exists Students");
        //set studentId primary key, projectId foreign key
        st.executeUpdate("create table Students (studentId text primary key, " +
                "firstName text, surname text, team text, " +
                "foreign key (team) references Projects(projectId));");
        //use precompiled SQL statements
        PreparedStatement pst = conn.prepareStatement("insert into Students (studentId, " +
                "firstName, surname, team) values(?,?,?,?);");
        for (Project p : projects.values()) {
            ArrayList<String> members = p.getMembers();
            for (String sId : members) {
                Student s = students.get(sId);
                pst.setString(1, s.getId());
                pst.setString(2, s.getFirstName());
                pst.setString(3, s.getSurname());
                pst.setString(4, p.getId());
                pst.executeUpdate();
            }
        }
        pst.close();
    }

    //swap item positions in the array list
    public void itemSwap(ArrayList list) {
        if (list.size() == 2) {
            Object temp = list.get(0);
            list.set(0, list.get(1));
            list.set(1, temp);
        } else {
            System.out.println("List size must be two.");
        }
    }


    //heuristic algorithm for improving sum of standard deviations
    public void autoSwap() {
        //do ten rounds of swap
        for (int i = 0; i < 10; i++) {
            int round = i + 1;
            System.out.println();
            System.out.println("*** round " + round + " ***");
            //objective function is the sum of three standard deviations
            originObjective = skillSd() + preferSd() + shortFalSd();

            //check originObjective, return if equals to 0
            if (studSelected.size() == 0) {
                System.out.println("current objective score is: " + originObjective);
                System.out.println("No student found. Add students first.");
                return;
            }

            System.out.println("current objective score is: " + originObjective);
            //put standard deviations and names in a map
            HashMap<String, Double> objMap = new HashMap<>();
            objMap.put("skillGap", shortFalSd());
            objMap.put("preference", preferSd());
            objMap.put("competency", skillSd());
            System.out.println(objMap.toString());
            System.out.println();

            //find the largest standard deviation
            double maxSd = 0.0;
            String maxMetric = null;
            for (Map.Entry<String, Double> entry : objMap.entrySet()) {
                if (maxSd < entry.getValue()) {
                    maxSd = entry.getValue();
                    maxMetric = entry.getKey();
                }
            }

            //improve from the largest standard deviation
            if (maxMetric.equals("skillGap")) {
                System.out.println("improving skillGap");
                System.out.println("--------------------");
                //put project id and shortfall score in a map
                HashMap<String, Double> skillMap = new HashMap<>();
                for (String project : projects.keySet()) {
                    skillMap.put(project, shortFalSco.calAvg(project));
                }

                //get array of min and max scores projects
                ArrayList<String> projectSwap = getProjectSwap(skillMap);
                //get the projects of min and max scores with id
                Project minProj = projects.get(projectSwap.get(0));
                Project maxProj = projects.get(projectSwap.get(1));

                //put students in min shortfall project and scores in map
                LinkedHashMap<String, Integer> minShortFalMap = new LinkedHashMap<>();
                for (String s : minProj.getMembers()) {
                    Student student = students.get(s);
                    int shortfall = studShortFalSco(student, minProj);
                    minShortFalMap.put(student.getId(), shortfall);
                }
                //sort min map by value in ascending order
                //https://www.java67.com/2017/07/how-to-sort-map-by-values-in-java-8.html#ixzz6ZiLvFFeT
                minShortFalMap = minShortFalMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue())
                        .collect(toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                //put max shortfall project students and score in map
                LinkedHashMap<String, Integer> maxShortFalMap = new LinkedHashMap<>();
                for (String s : maxProj.getMembers()) {
                    Student student = students.get(s);
                    int shortfall = studShortFalSco(student, minProj);
                    maxShortFalMap.put(student.getId(), shortfall);
                }
                //sort max map by value in descending order
                maxShortFalMap = maxShortFalMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                //try swap min and max
                //if improved, keep the change, otherwise swap back
                trySwap(minShortFalMap.keySet(), maxShortFalMap.keySet(), projectSwap);
            } else if (maxMetric.equals("competency")) {
                System.out.println("improving competency");
                System.out.println("--------------------");
                //put project id and competency score in a map
                HashMap<String, Double> competMap = new HashMap<>();
                for (String project : projects.keySet()) {
                    competMap.put(project, skillAvgTeam.calAvg(project));
                }

                //get array of min and max scores projects
                ArrayList<String> projectSwap = getProjectSwap(competMap);
                //get the projects of min and max scores with id
                Project minProj = projects.get(projectSwap.get(0));
                Project maxProj = projects.get(projectSwap.get(1));

                //put min shortfall project students and score in map
                LinkedHashMap<String, Double> minCompetMap = new LinkedHashMap<>();
                for (String s : minProj.getMembers()) {
                    Student student = students.get(s);
                    double skillAvgStud = skillAvgStud(student);
                    minCompetMap.put(student.getId(), skillAvgStud);
                }
                //sort min map by value in ascending order
                minCompetMap = minCompetMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue())
                        .collect(toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                //put max shortfall project students and score in map
                LinkedHashMap<String, Double> maxCompetMap = new LinkedHashMap<>();
                for (String s : maxProj.getMembers()) {
                    Student student = students.get(s);
                    double skillAvgStud = skillAvgStud(student);
                    maxCompetMap.put(student.getId(), skillAvgStud);
                }
                //sort max map by value in descending order
                maxCompetMap = maxCompetMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .collect(toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                //try swap min and max
                //if improved, keep the change, otherwise swap back
                trySwap(minCompetMap.keySet(), maxCompetMap.keySet(), projectSwap);
            } else {
                System.out.println("improving preference");
                System.out.println("--------------------");
                //put project id and preference score in a map
                HashMap<String, Double> perfMap = new HashMap<>();
                for (String project : projects.keySet()) {
                    perfMap.put(project, preferPerc.calAvg(project));
                }
                //get array of min and max scores projects
                ArrayList<String> projectSwap = getProjectSwap(perfMap);
                //get the projects of min and max scores with id
                Project minProj = projects.get(projectSwap.get(0));
                Project maxProj = projects.get(projectSwap.get(1));

                //in min preference score team, find students who didn't choose this project as
                //1st and 2nd preference
                Set<String> minStudents = new LinkedHashSet<>();
                for (String minStud : minProj.getMembers()) {
                    Student s = students.get(minStud);
                    //preference score of this project
                    if (s.getPreferences().containsKey(minProj.getId())) {
                        int p = s.getPreferences().get(minProj.getId());
                        if (p != 4 && p != 3) {
                            minStudents.add(minStud);
                        }
                    }
                }
                //in max preference score team, find students who chose this project as
                //1st or 2nd preference
                Set<String> maxStudents = new LinkedHashSet<>();
                for (String maxStud : maxProj.getMembers()) {
                    Student s = students.get(maxStud);
                    //preference score of this project
                    if (s.getPreferences().containsKey(maxProj.getId())) {
                        int p = s.getPreferences().get(maxProj.getId());
                        if (p == 4 || p == 3) {
                            maxStudents.add(maxStud);
                        }
                    }
                }

                //try swap min and max
                //if improved, keep the change, otherwise swap back
                trySwap(minStudents, maxStudents, projectSwap);
            }
        }
        System.out.println("Auto improving finished.");
        System.out.println("------------------------");
    }

    //get array of projects with min and max value
    public ArrayList<String> getProjectSwap(Map<String, Double> map) {
        double min = 100000.0, max = 0.0;
        String maxId = null, minId = null;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (max < entry.getValue()) {
                max = entry.getValue();
                maxId = entry.getKey();
            }
            if (min > entry.getValue()) {
                min = entry.getValue();
                minId = entry.getKey();
            }
        }
        //find the projects with id
        Project minProj = projects.get(minId);
        Project maxProj = projects.get(maxId);
        //add projects to array lists for swapping
        ArrayList<String> projectSwap = new ArrayList<>();
        projectSwap.add(minProj.getId());
        projectSwap.add(maxProj.getId());

        return projectSwap;
    }

    //try swap min and max. If improved, keep the change, otherwise swap back
    public void trySwap(Set<String> minStudents, Set<String> maxStudents,
                        ArrayList<String> projectSwap) {
        if (minStudents.size() > 0 && maxStudents.size() > 0) {
            loop:
            for (String minStud : minStudents) {
                for (String maxStud : maxStudents) {
                    System.out.println("min is " + minStud + " and max is " + maxStud);
                    ArrayList<String> studentSwap = new ArrayList<>();
                    studentSwap.add(minStud);
                    studentSwap.add(maxStud);
                    try {
                        //swap min and max students
                        swap(studentSwap, projectSwap);
                        //calculate new objective
                        double newObjective = skillSd() + preferSd() + shortFalSd();
                        System.out.println("Origin objective is: " + originObjective);
                        //if objective improves, keep the swap
                        if (newObjective < originObjective) {
                            System.out.println("After swapping, new objective is: " + newObjective);
                            System.out.println("--------------------");
                            originObjective = newObjective;
                            //if swapping succeed, break the outer loop of max and min students
                            //do next round of swap
                            break loop;
                        } else {
                            //if objective doesn't improve, swap back
                            System.out.println("new objective is " + newObjective +
                                    ". objective doesn't improve, swap back");
                            undo();
                            System.out.println("Try next students.");
                            System.out.println("--------------------");
                        }
                    } catch (StudentConflictException | PersonalityImbalanceException
                            | NoLeaderException e) {
                        System.out.println("Try next students.");
                        System.out.println("--------------------");
                    }
                }
            }
        }
    }
}