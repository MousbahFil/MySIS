package com.mousbah.mysis.admin;

import java.util.List;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.mousbah.database.service.DatabaseServiceFactory;
import com.mousbah.database.service.api.UserAdminService;
import com.mousbah.database.service.tables.Course;
import com.mousbah.database.service.tables.User;
import com.mousbah.database.service.tables.UserType;
import com.mousbah.mysis.common.CssUtils;
import com.mousbah.mysis.common.ErrorEventHandler;

@SuppressWarnings({"unused", "unchecked"})
public class AdministrationScreen extends BorderPane {
	
	private DatabaseServiceFactory factory=DatabaseServiceFactory.newInstance();
	private UserAdminService userAdminService= factory.createUserAdminService();
	private User user;
	private TableView<User> usersTable = new TableView<User>();
	private TableColumn<User, String> userNamecolumn = new TableColumn<User, String>("User Name");
	private TableColumn<User, UserType> userTypecolumn = new TableColumn<User, UserType>("User Type");
	private TableColumn<User, String> firstNamecolumn = new TableColumn<User, String>("First Name");
	private TableColumn<User, String> lastNamecolumn = new TableColumn<User, String>("Last Name");
	private TableColumn<User, String> emailcolumn = new TableColumn<User, String>("E-mail");
	private TableColumn<User, String> idcolumn = new TableColumn<User, String>("Id");
	private ObservableList<User> tableData=FXCollections.observableArrayList();
	private Button editButton = new Button("Edit");
	private Button deleteButton = new Button("Delete");
	private Button newButton = new Button("New");

	public AdministrationScreen(User user)  {
		this.user = user;
		drawScreen();
	}
	
	public void drawScreen() {
		CssUtils.addDefaultStyleSheet(this);
		userNamecolumn.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
		userTypecolumn.setCellValueFactory(new PropertyValueFactory<User, UserType>("userType"));
		firstNamecolumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
		lastNamecolumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
		emailcolumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
		idcolumn.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
		
		usersTable.getColumns().addAll(userNamecolumn, userTypecolumn, firstNamecolumn, lastNamecolumn, idcolumn, emailcolumn);
		
		HBox buttonsHBox = new HBox();
		buttonsHBox.getChildren().addAll(newButton, editButton, deleteButton);
		buttonsHBox.getStyleClass().add("hbox");
		
		setCenter(usersTable);
		setBottom(buttonsHBox);
		usersTable.setItems(tableData);
		fillTableData();
		
		deleteButton.setOnAction(e -> {
			Action response = Dialogs.create().title("Delete User").
	      			message("Are you sure you want to delete the user?").showConfirm();
	            	if(response==Dialog.Actions.YES){
	            		deleteUserTask();
	            	}
		});
		newButton.setOnAction(e -> new UserCreation(null));
		editButton.setOnAction(e -> new UserCreation(usersTable.getSelectionModel().getSelectedItem()));
		}
	
	private void  deleteUserTask(){
		Task<Void> task=new Task<Void>() {
			@Override
			protected Void call() throws Exception {
		            		User user = usersTable.getSelectionModel().getSelectedItem();
		            		userAdminService.deleteUser(user.getId());
				return null;
			}
		};
		
		task.setOnFailed(new ErrorEventHandler());
		new Thread(task).start();;
		task.setOnSucceeded(e -> fillTableData());
	}
	
	private void fillTableData(){
		Task<List<User>> task=new Task<List<User>>() {
			@Override
			protected List<User> call() throws Exception {
				return userAdminService.getRegisteredUsers();
			}
		};
		new Thread(task).start();
		task.setOnFailed(new ErrorEventHandler());
		task.setOnSucceeded(e ->{
			tableData.clear();
			tableData.addAll(task.getValue());
		});
	}
}
