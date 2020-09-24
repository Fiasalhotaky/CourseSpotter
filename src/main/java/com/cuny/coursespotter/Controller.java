package com.cuny.coursespotter;

public class Controller {

    private Student model;
    private MainView view;

    public Controller(Student model, MainView view) {
        this.model = model;
        this.view = view;
    }
}
