package com.mousbah.mysis.admin;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.mousbah.database.service.DatabaseServiceFactory;
import com.mousbah.database.service.api.UserAdminService;
import com.mousbah.database.service.tables.Student;
import com.mousbah.database.service.tables.User;
import com.mousbah.database.service.tables.UserType;
import com.mousbah.mysis.common.CssUtils;
import com.mousbah.mysis.common.ErrorEventHandler;
import com.mousbah.mysis.common.GridPaneHelper;

public class UserCreation  extends Stage{
	
	private static final DatabaseServiceFactory factory = DatabaseServiceFactory.newInstance();
	private UserAdminService userAdministrationService =factory.createUserAdminService();
	private User user;
	private Label userNameLabel = new Label("User Name");
	private TextField userNameTextField = new TextField();
	private Label passwordLabel = new Label("Password");
	private PasswordField password = new PasswordField();
	private Label confirmPasswordLabel = new Label("Confirm Password");
	private PasswordField confirmPassword = new PasswordField();
	private Label userTypeLabel = new Label("User Type:");
	private ComboBox<UserType> userTypeCombo = new ComboBox<UserType>();
	private Label firstNameLabel = new Label("First Name");
	private TextField firstNameTextField = new TextField();
	private Label lastNameLabel = new Label("Last Name");
	private TextField lastNameTextField = new TextField();
	private Label emailLabel = new Label("E-mail");
	private TextField emailTextField = new TextField();
	private Button insertButton = new Button();
	private GridPane gridPane=new GridPane();
	private GridPaneHelper gridPaneHelper=new GridPaneHelper(gridPane);
	
	public UserCreation(User user) {
		this.user = user;
		drawScreen();
	}
	
	public void drawScreen() {
		gridPane.setHgap(15);
		gridPane.setVgap(15);
		gridPane.getStyleClass().add("gridPane");
		gridPaneHelper.addRowToGridPane(userNameLabel,userNameTextField );
		gridPaneHelper.addRowToGridPane(passwordLabel,password );
		gridPaneHelper.addRowToGridPane(confirmPasswordLabel,confirmPassword );
		gridPaneHelper.addRowToGridPane(userTypeLabel,userTypeCombo );
		gridPaneHelper.addRowToGridPane(firstNameLabel,firstNameTextField );
		gridPaneHelper.addRowToGridPane(lastNameLabel,lastNameTextField );
		gridPaneHelper.addRowToGridPane(emailLabel,emailTextField );
		userTypeCombo.getItems().addAll(FXCollections.observableArrayList(UserType.STUDENT,UserType.STAFF, UserType.FACULTY, UserType.ADMINISTRATOR));
		fillScreenData();
		
		userTypeCombo.setMinWidth(170);
		VBox vBox = new VBox();
		HBox buttonHBox=new HBox();
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getStyleClass().add("hbox");
		buttonHBox.getChildren().add(insertButton);
		vBox.getChildren().addAll(gridPane, buttonHBox);
		vBox.getStyleClass().add("vbox");
		
		Scene scene = new Scene(vBox);
		setScene(scene);
		setTitle("Insert User");
		CssUtils.addDefaultStyleSheet(scene);
		show();
		
		insertButton.setOnAction(e -> {insertUserTask();
		});
	}

	private void fillScreenData() {
		if(user !=null){
			userNameTextField.setText(user.getUserName());
			userTypeCombo.getSelectionModel().select(user.getUserType());
			password.setText(user.getPassword());
			emailTextField.setText(user.getEmail());
			firstNameTextField.setText(user.getFirstName());
			lastNameTextField.setText(user.getLastName());
			insertButton.setText("Update");
		}else{
			insertButton.setText("Insert");
		}
	}
	
	private void insertUserTask(){
		Task<Void> task=new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				User userToInsert = new User();
				userToInsert.setUserName(userNameTextField.getText());
				userToInsert.setPassword(password.getText());
				userToInsert.setUserType(userTypeCombo.getSelectionModel().getSelectedItem());
				userToInsert.setEmail(emailTextField.getText());
				userToInsert.setFirstName(firstNameTextField.getText());
				userToInsert.setLastName(lastNameTextField.getText());
				if(user != null){
						userAdministrationService.updateUser(userToInsert);
				} else{
					userAdministrationService.insertUser(userToInsert);
					if(userTypeCombo.getSelectionModel().getSelectedItem() == UserType.STUDENT){
						Student student = new Student();
						student.setId(userAdministrationService.getUser(userToInsert.getUserName()).getId());
						student.setCumulativeGpa(0);
						factory.createStudentRepositoryService().insertStudent(student);
					}
				}
				return null;
			}};
			new Thread(task).start();
			task.setOnFailed(new ErrorEventHandler());
			task.setOnScheduled(e -> close());
		}
}
