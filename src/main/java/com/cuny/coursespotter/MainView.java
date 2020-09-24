package com.cuny.coursespotter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route
@CssImport("style.css")
public class MainView extends VerticalLayout {

    private TextField courseNumber = new TextField();
    private TextField phoneNumber = new TextField();
    private EmailField emailAddress = new EmailField();
    private Select<String> college = new Select<>();
    private Select<String> subject = new Select<>();
    private Grid<CourseInfo> courses = new Grid<>(CourseInfo.class);

    private Button submit = new Button("submit");
    private FormLayout formLayout = new FormLayout();

    private LinkedHashMap<String, String> map = Parser.acquireSessionInfo();

    private Student student = new Student();

    private Binder<Student> binder = new Binder<>();


    public MainView() {
        VerticalLayout main = new VerticalLayout();

        Controller controller = new Controller(student, this);
        main.setSpacing(true);

        Image image = new Image("./logo.png", "CourseSpotter");
        image.setWidth("100px");
        image.setHeight("100px");

        H2 h2 = new H2("CourseSpotter");
        main.add(image);
        main.add(h2);
        initForm();

        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        SerializablePredicate<String> verifyPhoneAndEmail = value -> !phoneNumber
                .getValue().trim().isEmpty()
                || !emailAddress.getValue().trim().isEmpty();

        SerializablePredicate<String> checkPhone = value -> phoneNumber.getValue().length() == 10 || StringUtils.isNumeric(phoneNumber.getValue());

        Binder.Binding<Student, String> emailBinding = binder.forField(emailAddress)
                .withValidator(verifyPhoneAndEmail,
                        "Phone or Email must be filled out.")
                .withValidator(new EmailValidator("Incorrect Email"))
                .bind(Student::getEmail, Student::setEmail);

        Binder.Binding<Student, String> phoneBinding = binder.forField(phoneNumber)
                .withValidator(verifyPhoneAndEmail,
                        "Phone or Email must be filled out.")
                .withValidator(checkPhone, "Invalid Phone Number.")
                .bind(Student::getPhoneNumber, Student::setPhoneNumber);

        emailAddress.addValueChangeListener(event -> phoneBinding.validate());
        phoneNumber.addValueChangeListener(event -> emailBinding.validate());

        subject.addValueChangeListener(
                event -> {
                    student.setCourseName(event.getValue().split("-")[0].trim());
                }
        );

        courseNumber.addValueChangeListener(
                event -> {
                    student.setCourseNumber(Integer.parseInt(event.getValue()));
                }
        );
        phoneNumber.addValueChangeListener(
                event -> {
                    if (event.getValue().length() > 10 || !StringUtils.isNumeric(event.getValue())) {
                        Notification.show("Invalid Phone Number.");
                    } else {
                        student.setPhoneNumber(event.getValue());
                    }
                }
        );

        emailAddress.addValueChangeListener(
                event -> {
                    student.setEmail(event.getValue());
                }
        );


        college.addValueChangeListener(
                event -> {
                    student.setCollege(Parser.colleges.get(event.getValue()));
                    subject.setItems(Parser.fetchSubjects(map.get("ICSID"), Integer.parseInt(map.get("ICStateNum")), Parser.colleges.get(event.getValue()), "1202"));
                }
        );

        phoneNumber.addFocusListener(event -> {
                    List<CourseInfo> fetchedCourses = Parser.fetchCourseInfo(map.get("ICSID"), Integer.parseInt(map.get("ICStateNum")), student.getCollege(), "1202", student.getCourseName(), student.getCourseNumber());

                    if (!fetchedCourses.isEmpty()) courses.setItems(fetchedCourses);
                }
        );

        SingleSelect<Grid<CourseInfo>, CourseInfo> courseSelection = courses.asSingleSelect();

        courseSelection.addValueChangeListener(e -> {
            student.setClassID(Integer.parseInt(e.getValue().getId()));
        });

        main.add(formLayout);
        main.add(submit);
        add(main);

    }

    private void initForm() {
        emailAddress.setClearButtonVisible(true);
        emailAddress.setErrorMessage("Please provide a valid Email Address.");
        List<String> Colleges = new ArrayList<>(Parser.colleges.keySet());
        college.setItems(Colleges);

        formLayout.addFormItem(college, "College");
        formLayout.addFormItem(subject, "Subject");
        formLayout.addFormItem(courseNumber, "Course Number");
        Dialog dialog = new Dialog();
        dialog.add(new Label("Select the course you would like to receive updates for."));

        Button confirmButton = new Button("Confirm", event -> {
            System.out.println(student.getCollege() + "\t" + student.getClassID() + "\t" + student.getCourseName() + "\t" + student.getCourseNumber() + "\t" + student.getEmail() + "\t" + student.getPhoneNumber());

            int result = DBWorker.insertData(student.getEmail(), student.getPhoneNumber(), student.getCollege(), student.getCourseName(), student.getCourseNumber(), student.getClassID());
            if (result == 1)
                Notification.show("Success!");
            else {
                Notification.show("Failed to insert information into database.");
            }
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> {
            Notification.show("Cancelled.");
            dialog.close();
        });
        dialog.add(courses, confirmButton, cancelButton);

        dialog.setWidth("1280px");
        dialog.setHeight("720px");

        submit.addClickListener(event -> {
                    BinderValidationStatus<Student> validate = binder.validate();
                    String errorText = validate.getFieldValidationStatuses()
                            .stream().filter(BindingValidationStatus::isError)
                            .map(BindingValidationStatus::getMessage)
                            .map(Optional::get).distinct()
                            .collect(Collectors.joining(", "));

                    if (validate.hasErrors()) {
                        Notification.show(errorText);
                    } else {
                        dialog.open();
                    }
                }
        );
        formLayout.addFormItem(phoneNumber, "Phone");
        formLayout.addFormItem(emailAddress, "Email");

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
    }
}