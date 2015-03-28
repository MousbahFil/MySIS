package com.mousbah.mysis.user;

import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.mousbah.database.service.DatabaseServiceFactory;
import com.mousbah.database.service.api.CourseRepositoryService;
import com.mousbah.database.service.api.StudentRepositoryService;
import com.mousbah.database.service.tables.Course;
import com.mousbah.database.service.tables.Student;
import com.mousbah.mysis.common.CssUtils;
import com.mousbah.mysis.common.ErrorEventHandler;

public class CourseRegistration extends BorderPane{
	
	private static final  DatabaseServiceFactory factory = DatabaseServiceFactory.newInstance();
	private static StudentRepositoryService studentRepositoryService;
	private static CourseRepositoryService courseRepositoryService ;
	private static TableView<Course> table = new TableView<Course>();
	private TableColumn<Course, String> crnColumn = new TableColumn<Course, String>("CRN");
	private TableColumn<Course, String> courseNameColumn = new TableColumn<Course, String>("Course Name");
	private TableColumn<Course, String> courseCredits = new TableColumn<Course, String>("Credits");
	private TableColumn<Course, String> courseDescription = new TableColumn<Course, String>("Course Description");
	private TableColumn<Course, Boolean> dropCourse = new TableColumn<Course, Boolean>("Drop Course");
	private Student student;
	private ObservableList<Course> tableData=FXCollections.observableArrayList();
	
	public CourseRegistration(Student student )  {
		this.student = student;
		studentRepositoryService = factory.createStudentRepositoryService();
		courseRepositoryService = factory.createCourseRepositoryService();
		drawScreen();
	}
	
	public void drawScreen() {
		CssUtils.addDefaultStyleSheet(this);
		 Label enterCrnLabel = new Label("Please enter course number: ");
		 TextField courseNumberTxt = new TextField();
		 HBox topHBox = new HBox(); 
		 topHBox.getChildren().addAll(enterCrnLabel, courseNumberTxt);
		 HBox buttonHBox = new HBox();
		 Button registerButton = new Button("Register");
		 buttonHBox.setAlignment(Pos.CENTER);
		 buttonHBox.getChildren().addAll(registerButton);
		 topHBox.getStyleClass().add("hbox");
		 
		 Label coursesLabel = new Label("Course Schedule");
		 VBox mainVBox = new VBox();
		 mainVBox.getChildren().addAll(topHBox, buttonHBox,coursesLabel);
		 mainVBox.getStyleClass().add("vbox");
		 setTop(mainVBox);
		 setCenter(table);
		 initializeTable();
		 registerButton.setOnAction( e ->registerCourse(courseNumberTxt));
	}
	
	@SuppressWarnings("unchecked")
	private void initializeTable() {
		 crnColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("crn"));
		 courseNameColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("courseName"));
		 courseCredits.setCellValueFactory(new PropertyValueFactory<Course, String>("credits"));
		 courseDescription.setCellValueFactory(new PropertyValueFactory<Course, String>("description"));
		 dropCourse.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Course, Boolean>, ObservableValue<Boolean>>() {
		      public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Course, Boolean> features) {
		        return new SimpleBooleanProperty(features.getValue() != null);
		      } 
		    });

		 crnColumn.setMinWidth(50);
		 courseNameColumn.setMinWidth(150);
		 courseCredits.setMinWidth(100);
		 courseDescription.setMinWidth(150);
		 dropCourse.setMinWidth(100);
		Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>> callBack = createButtonCell();
		dropCourse.setCellFactory(callBack);
		table.getColumns().addAll(crnColumn,courseNameColumn,courseCredits,courseDescription,dropCourse);
		table.setItems(tableData);
		fillTableData();
	}

	private void dropCourse(Course course){
		Task<Void> task= new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Course c = courseRepositoryService.getCourseByCrn(course.getCrn());
        		studentRepositoryService.dropStudentfromCourse(student, c);
        		fillTableData();
				return null;
			}
		};
		new Thread(task).start();
		task.setOnFailed(showErrordialog());
	}
	
	private void fillTableData(){
		Task<Void> task=new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				tableData.clear();
				List<Course> RegisteredCourses = studentRepositoryService.getRegisteredCourses(student);
				tableData.addAll(RegisteredCourses);
				return null;
			}
		};
		new Thread(task).start();
		task.setOnFailed(showErrordialog());
	}
	
	private void registerCourse(TextField courseNumberTxt) {
		Task<Void> task=new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Course course = courseRepositoryService.getCourseByCrn(Integer.valueOf(courseNumberTxt.getText()));
				studentRepositoryService.registerStudentInCourse(student, course);
				tableData.add(course);
				return null;
			}
		};
		new Thread(task).start();
		task.setOnFailed(showErrordialog());
	};
	
	private Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>> createButtonCell() {
		Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>> callBack = new Callback<TableColumn<Course,Boolean>, TableCell<Course,Boolean>>() {
			public TableCell<Course, Boolean> call(TableColumn<Course, Boolean> arg0) {
				TableCell<Course, Boolean> cell = new TableCell<Course, Boolean>(){
					public void updateItem(Boolean item, boolean empty) {
						super.updateItem(item, empty);
						if(!empty){
							Button button = new Button("Drop");
							setDropCourseButtonHandler(button, getIndex());
                        	setGraphic(button);
                    		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						} else {
		                            setText(null);
		                            setGraphic(null);
		                        }
							}
					};
				return cell;
			}
		 };
		return callBack;
	}
	private void setDropCourseButtonHandler(Button button, int index) {
		button.setOnAction(e ->{
				Action response = Dialogs.create().title("Drop Ccourse").
		      			message("Are you sure you want to delete course?").showConfirm();
		            	if(response==Dialog.Actions.YES){
		            		dropCourse(table.getItems().get(index));
		            	}
					});
	}
	private EventHandler<WorkerStateEvent> showErrordialog() {
		return new ErrorEventHandler();
	}
	
}
