package com.mousbah.mysis;

import javafx.application.Application;
import javafx.stage.Stage;

import com.mousbah.mysis.login.LoginScreen;

public class MySIS  extends Application{
	
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage)  {
		Stage loginScreen = new LoginScreen();
		loginScreen.show();
	}
 
}
