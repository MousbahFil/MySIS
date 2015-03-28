package com.mousbah.mysis.common;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.mousbah.mysis.login.LoginScreen;

public class BottomContainer extends HBox{
	
	public  void show(){
		Button signOutButton = new Button("Sign out");
		setAlignment(Pos.BOTTOM_RIGHT);
		getChildren().addAll(signOutButton);
		
		signOutButton.setOnAction(e -> {
				((Node)e.getSource()).getScene().getWindow().hide();
				Stage login= new LoginScreen();
				login.show();
		});
	}
}
