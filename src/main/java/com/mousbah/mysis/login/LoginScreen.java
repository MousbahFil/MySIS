package com.mousbah.mysis.login;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.mousbah.database.service.DatabaseServiceFactory;
import com.mousbah.database.service.api.StudentRepositoryService;
import com.mousbah.database.service.api.UserAdminService;
import com.mousbah.database.service.tables.Student;
import com.mousbah.database.service.tables.User;
import com.mousbah.database.service.tables.UserType;
import com.mousbah.mysis.admin.AdministrationScreen;
import com.mousbah.mysis.common.CssUtils;
import com.mousbah.mysis.common.GridPaneHelper;
import com.mousbah.mysis.user.NavigationScreen;

public class LoginScreen extends Stage {
	
	private static final String MY_SIS = "MySIS";
	private TextField userNameTextField = new TextField();
	private PasswordField passwordTextField = new PasswordField();
	private ProgressBar progressBar = new ProgressBar();
	private Label userNameLabel = new Label("User name:");
	private Label passwordLabel = new Label("Password:");
	private Button loginButton = new Button("Login");
	private GridPane gridPane=new GridPane();
	private GridPaneHelper gridPaneHelper=new GridPaneHelper(gridPane);

	
	public LoginScreen( )  {
		drawScreen();
	}
	
	public void drawScreen() {
		gridPaneHelper.addRowToGridPane(userNameLabel, userNameTextField);
		gridPaneHelper.addRowToGridPane(passwordLabel, passwordTextField);
		gridPane.setHgap(15);
		gridPane.setVgap(15);
		
		HBox loginButtonHBox = new HBox();
		loginButtonHBox.setAlignment(Pos.CENTER);
		loginButtonHBox.getChildren().addAll(loginButton);
		loginButton.setPrefWidth(70);
		VBox content = new VBox();
		content.getChildren().addAll(gridPane, loginButtonHBox);

		
		progressBar.setVisible(false);
		HBox progressHBox = new HBox();
		progressHBox.setAlignment(Pos.BOTTOM_RIGHT);
		progressHBox.getChildren().addAll(progressBar);
		
		BorderPane borderPane=new BorderPane();
		borderPane.setTop(content);
		borderPane.setBottom(progressHBox);
		
		Scene scene=new Scene(borderPane,300,300);
		setScene(scene);
		setTitle(MY_SIS);
		CssUtils.addDefaultStyleSheet(scene);
		content.getStyleClass().add("vbox");
		setResizable(false);

		loginButton.setOnAction(e -> doLogin());
		passwordTextField.setOnAction(e -> doLogin());
	}
	private void doLogin() {
		Task<Void> task=new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				DatabaseServiceFactory factory = DatabaseServiceFactory.newInstance();
				StudentRepositoryService service= factory.createStudentRepositoryService();
				UserAdminService admin = factory.createUserAdminService();
				if(admin.authenticateUser(userNameTextField.getText(), passwordTextField.getText())){
					User user = admin.getUser(userNameTextField.getText());
					if(user.getUserType() == UserType.STUDENT){
					Student student= service.getStudentfromUser(user);
					displayNavigationScreen(student, user);
					}else if (user.getUserType() == UserType.ADMINISTRATOR){
						displayAdministrationScreen(user);
					}
					return null;
				}else{
					throw new IllegalArgumentException("Wrong user name or password!");
				}
			}};
			
			new Thread(task).start();
			task.setOnFailed(e -> {
				Dialogs.create().owner(this).title("Error").message("An Error has occured!")
		        .showException(e.getSource().getException());
			});
			progressBar.visibleProperty().bind(task.runningProperty());
	}
	
	private void displayNavigationScreen(Student student, User user){
		Platform.runLater(()->{
			displayScene(new NavigationScreen(student),user);
		});
	}
	
	private void displayAdministrationScreen( User user){
		Platform.runLater(()->{
			displayScene(new AdministrationScreen(user),user);
		});
	}
	
	private void displayScene(Parent parent, User user){
		setResizable(true);
			Scene scene=new Scene(parent);
			setScene(scene);
			setTitle("Welcome " + user.getFirstName() + "!");
	}
	
}

