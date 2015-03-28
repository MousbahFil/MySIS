package com.mousbah.mysis.common;

import org.controlsfx.dialog.Dialogs;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class ErrorEventHandler implements EventHandler<WorkerStateEvent> {

	@Override
	public void handle(WorkerStateEvent arg0) {
		Dialogs.create().title("Error").message("An Error has occured!")
        .showException(arg0.getSource().getException());		
	}

}
