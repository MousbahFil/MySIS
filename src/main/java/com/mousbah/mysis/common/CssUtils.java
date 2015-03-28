package com.mousbah.mysis.common;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class CssUtils {

	public static void addDefaultStyleSheet(Scene scene){
		scene.getStylesheets().add(CssUtils.class.getClassLoader().getResource("style.css").toExternalForm());
	}
	
	public static void addDefaultStyleSheet(Parent parent){
		parent.getStylesheets().add(CssUtils.class.getClassLoader().getResource("style.css").toExternalForm());
	}
}
