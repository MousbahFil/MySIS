package com.mousbah.mysis.common;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GridPaneHelper {
	
	private GridPane gridPane;
	private int index=0;

	public GridPaneHelper(GridPane gridPane) {
		this.gridPane = gridPane;
	}
	
	public void addRowToGridPane(Node... nodes){
			gridPane.addRow(index, nodes);
			index++;
			for(Node node: nodes){
				GridPane.setHalignment(node, HPos.RIGHT);
			}
	}
}
