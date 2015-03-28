package com.mousbah.mysis.user;

import javafx.scene.layout.BorderPane;

import com.mousbah.database.service.tables.Student;
import com.mousbah.mysis.common.BottomContainer;

public class NavigationScreen extends BorderPane{
	 
	private Student student;
	
	public NavigationScreen(Student student)  {
		this.student = student;
		drawScreen();
	}
	
	public void drawScreen(){
		CourseRegistration content = new CourseRegistration(student);
		setCenter(content); 
		BottomContainer cont = new BottomContainer();
		setBottom(cont);
		cont.show();
	}

}
