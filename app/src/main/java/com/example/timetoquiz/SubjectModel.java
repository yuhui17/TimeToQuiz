package com.example.timetoquiz;

public class SubjectModel {

    private String id;
    private String name;
    private String numOfSets;
    private String quizCounter;

    public SubjectModel(String id, String name, String numOfSets, String quizCounter) {
        this.id = id;
        this.name = name;
        this.numOfSets = numOfSets;
        this.quizCounter = quizCounter;
    }

    public String getQuizCounter(){
        return quizCounter;
    }

    public void setQuizCounter(String quizCounter){
        this.quizCounter = quizCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfSets() {
        return numOfSets;
    }

    public void setNumOfSets(String numOfSets) {
        this.numOfSets = numOfSets;
    }
}
