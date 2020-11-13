package controller;

import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import model.*;

public class mainController {
    private TeamFormation teamFormation;

    @FXML
    private CheckBox team1_1;

    @FXML
    private CheckBox team1_2;

    @FXML
    private CheckBox team1_3;

    @FXML
    private CheckBox team1_4;

    @FXML
    private CheckBox team2_1;

    @FXML
    private CheckBox team2_2;

    @FXML
    private CheckBox team2_3;

    @FXML
    private CheckBox team2_4;

    @FXML
    private CheckBox team3_1;

    @FXML
    private CheckBox team3_2;

    @FXML
    private CheckBox team3_3;

    @FXML
    private CheckBox team3_4;

    @FXML
    private CheckBox team4_1;

    @FXML
    private CheckBox team4_2;

    @FXML
    private CheckBox team4_3;

    @FXML
    private CheckBox team4_4;

    @FXML
    private CheckBox team5_1;

    @FXML
    private CheckBox team5_2;

    @FXML
    private CheckBox team5_3;

    @FXML
    private CheckBox team5_4;

    @FXML
    private TextField inputID;

    @FXML
    private BarChart<?, ?> preferBar;

    @FXML
    private BarChart<?, ?> competencyBar;

    @FXML
    private BarChart<?, ?> skillBar;

    @FXML
    private Label preferSd;

    @FXML
    private Label competencySd;

    @FXML
    private Label skillSd;

    @FXML
    private Text projectList;

    @FXML
    private Text studentList;

    @FXML
    private Text showList;

    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<CheckBox> team1;
    private ArrayList<CheckBox> team2;
    private ArrayList<CheckBox> team3;
    private ArrayList<CheckBox> team4;
    private ArrayList<CheckBox> team5;
    private ArrayList<ArrayList<CheckBox>> teams;

    @FXML
    void initialize() {
        //singleton pattern to ensure class has only one instance
        teamFormation = TeamFormation.getInstance();
        teamFormation.setController();
        teamFormation.resetStudents();
        teamFormation.resetStack();
        //load data from files
        teamFormation.loadCompany();
        teamFormation.loadProjectOwner();
        teamFormation.loadProject();
        teamFormation.loadStudent();
        //put check box in array list
        checkBoxes = new ArrayList<>();
        checkBoxes.add(team1_1);
        checkBoxes.add(team1_2);
        checkBoxes.add(team1_3);
        checkBoxes.add(team1_4);
        checkBoxes.add(team2_1);
        checkBoxes.add(team2_2);
        checkBoxes.add(team2_3);
        checkBoxes.add(team2_4);
        checkBoxes.add(team3_1);
        checkBoxes.add(team3_2);
        checkBoxes.add(team3_3);
        checkBoxes.add(team3_4);
        checkBoxes.add(team4_1);
        checkBoxes.add(team4_2);
        checkBoxes.add(team4_3);
        checkBoxes.add(team4_4);
        checkBoxes.add(team5_1);
        checkBoxes.add(team5_2);
        checkBoxes.add(team5_3);
        checkBoxes.add(team5_4);

        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        team3 = new ArrayList<>();
        team4 = new ArrayList<>();
        team5 = new ArrayList<>();

        team1.add(team1_1);
        team1.add(team1_2);
        team1.add(team1_3);
        team1.add(team1_4);

        team2.add(team2_1);
        team2.add(team2_2);
        team2.add(team2_3);
        team2.add(team2_4);

        team3.add(team3_1);
        team3.add(team3_2);
        team3.add(team3_3);
        team3.add(team3_4);

        team4.add(team4_1);
        team4.add(team4_2);
        team4.add(team4_3);
        team4.add(team4_4);

        team5.add(team5_1);
        team5.add(team5_2);
        team5.add(team5_3);
        team5.add(team5_4);

        teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);

        //show project information in text box
        StringBuilder projectBuilder = new StringBuilder();
        projectBuilder.append(" Project Information:" + "\n");
        for (Project p : teamFormation.getProjectList().values()) {
            projectBuilder.append(p.toString()).append("\n");
        }
        projectList.setText(projectBuilder.toString());

        //show student information in text box
        StringBuilder studentBuilder = new StringBuilder();
        studentBuilder.append(" Student Information:" + "\n");
        for (Student s : teamFormation.getStudentList().values()) {
            studentBuilder.append(s.toString()).append("\n");
        }
        studentList.setText(studentBuilder.toString());

        showList.setText("COSC1295 Advanced Programming | S3815738 Wenkai Li");

        update();
    }

    @FXML
    public void addStudent() {
        String id = inputID.getText();
        int count = 0;
        CheckBox selected = new CheckBox();
        //check how many check box is selected
        for (CheckBox c : checkBoxes) {
            if (c.isSelected())
                count++;
        }
        //only one box can be selected
        if (count != 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Check Box Error");
            alert.setContentText("When adding new student, only one check box can be selected");
            alert.showAndWait();
        } else {

            //check which check box is selected
            for (CheckBox c : checkBoxes) {
                if (c.isSelected())
                    selected = c;
            }

            //check the select box belongs to which project
            int i = 1;
            for (ArrayList<CheckBox> team : teams) {
                if (team.contains(selected)) {
                    break;
                }
                i++;
            }
            String projectId = "Pr" + i;
            //add input student id to selected project
            try {
                checkEmpty(selected);
                teamFormation.addStudent(id, projectId);
                //set student id as check box text
                selected.setText(id);
                selected.setSelected(false);
                inputID.setText("");
            } catch (NotEmptyException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Checkbox Error");
                alert.setContentText("Select an empty checkbox to add student.");
                alert.showAndWait();
            } catch (NoInputException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Input Error");
                alert.setContentText("No input student id.");
                alert.showAndWait();
            } catch (StudentNotInTheListException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Input Error");
                alert.setContentText("Student id not exists.");
                alert.showAndWait();
            } catch (StudentConflictException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Conflict");
                alert.setContentText("Input student have conflict in team.");
                alert.showAndWait();
            } catch (RepeatedMemberException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Repeated Member");
                alert.setContentText("Input student is in the team.");
                alert.showAndWait();
            } catch (InvalidMemberException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Invalid Member");
                alert.setContentText("Input student is not available.");
                alert.showAndWait();
            } catch (PersonalityImbalanceException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Personality Imbalanced");
                alert.setContentText("Personality types cannot be less than three.");
                alert.showAndWait();
            } catch (NoLeaderException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("No Leader");
                alert.setContentText("Need at least one leader in the team.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                e.printStackTrace();
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Error");
                alert.setContentText("Something wrong.");
                alert.showAndWait();
            }
        }

        update();
    }

    public void checkEmpty(CheckBox checkBox) throws NotEmptyException {
        if (!checkBox.getText().equals("Empty")) {
            checkBox.setSelected(false);
            throw new NotEmptyException();
        }
    }

    public void checkNotEmpty(ArrayList<CheckBox> checkBoxes) throws EmptyException {
        for (CheckBox c : checkBoxes) {
            if (c.getText().equals("Empty")) {
                c.setSelected(false);
                throw new EmptyException();
            }
        }
    }

    public void swap() {
        int count = 0;
        ArrayList<String> selectedProject = new ArrayList<>();
        ArrayList<String> selectedStudentId = new ArrayList<>();
        ArrayList<CheckBox> selectedStudent = new ArrayList<>();
        //check how many check box is selected
        for (CheckBox c : checkBoxes) {
            if (c.isSelected())
                count++;
        }
        //only two box can be selected
        if (count != 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Check Box Error");
            alert.setContentText("When swapping students, only two check boxes can be selected");
            alert.showAndWait();
        } else {
            //check which check boxes are selected
            //add selected check boxes in list
            //add selected student id in list
            for (CheckBox c : checkBoxes) {
                if (c.isSelected()) {
                    selectedStudent.add(c);
                    selectedStudentId.add(c.getText());
                }
            }

            //check the selected box belongs to which project
            for (CheckBox b : selectedStudent) {
                int i = 1;
                for (ArrayList<CheckBox> team : teams) {
                    if (team.contains(b)) {
                        break;
                    }
                    i++;
                }
                String projectId = "Pr" + i;
                selectedProject.add(projectId);
            }

            try {
                checkNotEmpty(selectedStudent);
                //swap students
                teamFormation.swap(selectedStudentId, selectedProject);
                //swap checkbox texts
                swapText(selectedStudent);
                //set checkbox unselected
                setUnselected(checkBoxes);
                //update bar charts
                update();
            } catch (EmptyException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Checkbox Error");
                alert.setContentText("Select two nonempty checkboxes to swap students.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            } catch (StudentConflictException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Conflict");
                alert.setContentText("Input student have conflict in team.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            } catch (PersonalityImbalanceException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Personality Imbalanced");
                alert.setContentText("Personality types cannot be less than three.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            } catch (NoLeaderException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("No Leader");
                alert.setContentText("Need at least one leader in the team.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                e.printStackTrace();
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Error");
                alert.setContentText("Something wrong.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            }
        }
    }

    //swap checkbox texts
    public void swapText(ArrayList<CheckBox> selectedStudent) {
        String temp = selectedStudent.get(0).getText();
        selectedStudent.get(0).setText(selectedStudent.get(1).getText());
        selectedStudent.get(1).setText(temp);
    }

    public void swapShow() {
        ArrayList<String> selectedStudentId = new ArrayList<>();
        StringBuilder studentSelect = new StringBuilder();
        studentSelect.append(" Selected Student Information:" + "\n");

        //check which check boxes are selected
        for (CheckBox c : checkBoxes) {
            if (c.isSelected() && !c.getText().equals("Empty")) {
                selectedStudentId.add(c.getText());
            }
        }

        //show selected student information in text box
        if (selectedStudentId.size() > 0) {
            for (int i = 0; i < selectedStudentId.size(); i++) {
                Student student = teamFormation.getStudentList().get(selectedStudentId.get(i));
                if (student != null)
                    studentSelect.append(student.toString()).append("\n");
            }

            showList.setText(studentSelect.toString());
        }
    }

    public void undo() {
        try {
            //get swapped student id and undo swap
            ArrayList<String> selectedStudentId = teamFormation.undo();
            if (!(selectedStudentId.size() == 0)) {
                //undo checkbox text swap
                ArrayList<CheckBox> selectedStudent = new ArrayList<>();
                for (CheckBox c : checkBoxes) {
                    if (c.getText().equals(selectedStudentId.get(0))
                            || c.getText().equals(selectedStudentId.get(1))) {
                        selectedStudent.add(c);
                    }
                }
                swapText(selectedStudent);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setContentText("No swaps can be undone.");
                setUnselected(checkBoxes);
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText("Undo failed");
            alert.setContentText("Situation changed, cannot undo.");
            setUnselected(checkBoxes);
            alert.showAndWait();
        }

        //update bar chart
        update();
    }

    //set checkboxes unselected
    public void setUnselected(ArrayList<CheckBox> checkBoxes) {
        for (CheckBox c : checkBoxes) {
            c.setSelected(false);
        }
    }

    public void autoFill() throws PersonalityImbalanceException,
            StudentNotInTheListException, InvalidMemberException,
            RepeatedMemberException, StudentConflictException,
            NoLeaderException, NoInputException {
        boolean empty = true;
        for (CheckBox b : checkBoxes) {
            if (!b.getText().equals("Empty")) {
                empty = false;
                break;
            }
        }

        if (empty) {
            teamFormation.addStudent("S1", "Pr1");
            teamFormation.addStudent("S6", "Pr1");
            teamFormation.addStudent("S11", "Pr1");
            teamFormation.addStudent("S16", "Pr1");
            teamFormation.addStudent("S3", "Pr2");
            teamFormation.addStudent("S7", "Pr2");
            teamFormation.addStudent("S12", "Pr2");
            teamFormation.addStudent("S17", "Pr2");
            teamFormation.addStudent("S4", "Pr3");
            teamFormation.addStudent("S9", "Pr3");
            teamFormation.addStudent("S14", "Pr3");
            teamFormation.addStudent("S19", "Pr3");
            teamFormation.addStudent("S2", "Pr4");
            teamFormation.addStudent("S5", "Pr4");
            teamFormation.addStudent("S10", "Pr4");
            teamFormation.addStudent("S15", "Pr4");
            teamFormation.addStudent("S8", "Pr5");
            teamFormation.addStudent("S13", "Pr5");
            teamFormation.addStudent("S18", "Pr5");
            teamFormation.addStudent("S20", "Pr5");

            ArrayList<String> students = new ArrayList<>(teamFormation.getStudents());
            for (int i = 0; i < 20; i++) {
                checkBoxes.get(i).setText(students.get(i));
            }
            update();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("All students were assigned.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText("Error");
            alert.setContentText("Auto fill can only be used when no student added.");
            alert.showAndWait();
        }
    }

    public void initialData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);
        alert.setContentText("All data will be reset. Press OK to continue.");
        //ask user to select continue or not
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            initialize();
            for (CheckBox b : checkBoxes) {
                b.setText("Empty");
            }
        } else {
            return;
        }
    }

    public void update() {
        //data for barchart1
        Map<String, Double> series1 = new TreeMap<>();
        series1.put("Team1", teamFormation.getPreferPerc().calAvg("Pr1"));
        series1.put("Team2", teamFormation.getPreferPerc().calAvg("Pr2"));
        series1.put("Team3", teamFormation.getPreferPerc().calAvg("Pr3"));
        series1.put("Team4", teamFormation.getPreferPerc().calAvg("Pr4"));
        series1.put("Team5", teamFormation.getPreferPerc().calAvg("Pr5"));

        XYChart.Series dataSeries1 = new XYChart.Series();

        for (String c : series1.keySet())
            dataSeries1.getData().add(new XYChart.Data(c, series1.get(c)));
        preferBar.getData().clear();
        preferBar.getData().add(dataSeries1);

        preferSd.setText("Std Dev= " + teamFormation.preferSd());

        //data for barchart2
        Map<String, Double> series2 = new TreeMap<>();
        series2.put("Team1", teamFormation.getSkillAvgTeam().calAvg("Pr1"));
        series2.put("Team2", teamFormation.getSkillAvgTeam().calAvg("Pr2"));
        series2.put("Team3", teamFormation.getSkillAvgTeam().calAvg("Pr3"));
        series2.put("Team4", teamFormation.getSkillAvgTeam().calAvg("Pr4"));
        series2.put("Team5", teamFormation.getSkillAvgTeam().calAvg("Pr5"));

        XYChart.Series dataSeries2 = new XYChart.Series();

        for (String c : series2.keySet())
            dataSeries2.getData().add(new XYChart.Data(c, series2.get(c)));
        competencyBar.getData().clear();
        competencyBar.getData().add(dataSeries2);

        competencySd.setText("Std Dev= " + teamFormation.skillSd());

        //data for barchart3
        Map<String, Double> series3 = new TreeMap<>();
        series3.put("Team1", teamFormation.getShortFalSco().calAvg("Pr1"));
        series3.put("Team2", teamFormation.getShortFalSco().calAvg("Pr2"));
        series3.put("Team3", teamFormation.getShortFalSco().calAvg("Pr3"));
        series3.put("Team4", teamFormation.getShortFalSco().calAvg("Pr4"));
        series3.put("Team5", teamFormation.getShortFalSco().calAvg("Pr5"));

        XYChart.Series dataSeries3 = new XYChart.Series();

        for (String c : series3.keySet())
            dataSeries3.getData().add(new XYChart.Data(c, series3.get(c)));
        skillBar.getData().clear();
        skillBar.getData().add(dataSeries3);

        skillSd.setText("Std Dev= " + teamFormation.shortFalSd());
    }

    public void saveToDatabase() {
        teamFormation.save();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Data has been saved to database.");
        alert.showAndWait();
    }

    public void preferShow() {
        String preferenceList = " %1st and 2nd Preferences:" + "\n" +
                "Team1: " + teamFormation.getPreferPerc().calAvg("Pr1") + "\n" +
                "Team2: " + teamFormation.getPreferPerc().calAvg("Pr2") + "\n" +
                "Team3: " + teamFormation.getPreferPerc().calAvg("Pr3") + "\n" +
                "Team4: " + teamFormation.getPreferPerc().calAvg("Pr4") + "\n" +
                "Team5: " + teamFormation.getPreferPerc().calAvg("Pr5") + "\n" +
                "Std Dev= " + teamFormation.preferSd();
        showList.setText(preferenceList);
    }

    public void competencyShow() {
        String competencyList = " Average Competency Level:" + "\n" +
                "Team1: " + teamFormation.getSkillAvgTeam().calAvg("Pr1") + "\n" +
                "Team2: " + teamFormation.getSkillAvgTeam().calAvg("Pr2") + "\n" +
                "Team3: " + teamFormation.getSkillAvgTeam().calAvg("Pr3") + "\n" +
                "Team4: " + teamFormation.getSkillAvgTeam().calAvg("Pr4") + "\n" +
                "Team5: " + teamFormation.getSkillAvgTeam().calAvg("Pr5") + "\n" +
                "Std Dev= " + teamFormation.skillSd();
        showList.setText(competencyList);
    }

    public void skillGapShow() {
        String skillGapList = " Skill Gap:" + "\n" +
                "Team1: " + teamFormation.getShortFalSco().calAvg("Pr1") + "\n" +
                "Team2: " + teamFormation.getShortFalSco().calAvg("Pr2") + "\n" +
                "Team3: " + teamFormation.getShortFalSco().calAvg("Pr3") + "\n" +
                "Team4: " + teamFormation.getShortFalSco().calAvg("Pr4") + "\n" +
                "Team5: " + teamFormation.getShortFalSco().calAvg("Pr5") + "\n" +
                "Std Dev= " + teamFormation.shortFalSd();
        showList.setText(skillGapList);
    }

    public void autoSwap() {
        for (CheckBox b : checkBoxes) {
            if (b.getText().equals("Empty")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("Error");
                alert.setContentText("Auto swap can only be used when all students added.");
                alert.showAndWait();
                return;
            }
        }
        teamFormation.autoSwap();
        //reset text of check boxes
        resetCheckbox();
        //update bar charts
        update();
    }

    //reset text of check boxes
    public void resetCheckbox() {
        ArrayList<Project> projects = new ArrayList<>(teamFormation.getProjectList().values());
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            ArrayList<String> studId = p.getMembers();
            ArrayList<CheckBox> teamCheckbox = teams.get(i);
            for (int j = 0; j < studId.size(); j++) {
                CheckBox box = teamCheckbox.get(j);
                String studentId = studId.get(j);
                box.setText(studentId);
            }
        }
    }
}
