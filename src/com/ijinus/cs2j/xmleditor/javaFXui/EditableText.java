/*
 Copyright 2015 Mathieu Blond - Ijinus ( mathieu.blond@ijinus.fr )

 This file is part of CS2JXmlEditor.

    CS2JXmlEditor is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CS2JXmlEditor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with CS2JXmlEditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ijinus.cs2j.xmleditor.javaFXui;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * 
 * @author Mathieu Blond - Ijinus (http://www.ijinus.com/?lang=en)
 *
 *	Represents a Label which can be modified on double-click.
 *	The strategy here is to change the Label to a TextField to allow to modify the text,
 *	and then change again to a Label.
 *
 */
public class EditableText extends HBox{
	
	private Object _file;
	private String _fieldName;
	
	private Label text;
	private TextField textField;
	
	public EditableText(){
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditableText.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
	}
	
	public EditableText(String fieldName, Object file){
		
		this();
		populate(fieldName, file);
		
	}
	
	@FXML
	public void initialize(){

	}
	
	/**
	 * Creates the content of the Label, defines the events, and restrict the Label/TextField change to avoid empty areas on empty text (see below).
	 * 
	 * @param fieldName
	 * @param file
	 */
	public void populate(String fieldName, Object file){
		
		_file = file;
		_fieldName = fieldName;
		
		String initialValue = "Unable to read value";
		
		try {
			initialValue = (String) _file.getClass().getMethod("get"+_fieldName).invoke(_file);
		} catch (Exception e) { e.printStackTrace(); }
		
		text = new Label(initialValue);
		textField = new TextField();
		
		textField.setMinHeight(20);
		
		/*
		 * Avoids an empty area unclickable
		 */
		
		if(initialValue != null && initialValue.length()>0)
			this.getChildren().add(text);
		else
			this.getChildren().add(textField);
		
		/*
		 * Events
		 */
		
		/*
		 * On double-click
		 */
		text.setOnMouseClicked(new EventHandler<MouseEvent>() {  
			@Override  
			public void handle(MouseEvent event) {  
				if (event.getClickCount()==2) {  
					textField.setText(text.getText());

					EditableText.this.getChildren().add(textField);
					EditableText.this.getChildren().remove(text);

					textField.requestFocus(); 
					textField.selectAll();  

				}  
			}});

		/*
		 * On action (tab, enter, ...)
		 */
		textField.setOnAction(new EventHandler<ActionEvent>() {  
			@Override  
			public void handle(ActionEvent event) {  
				
				text.setText(textField.getText());  

				if(textField.getText().length() > 0 && EditableText.this.getChildren().contains(textField)){
					EditableText.this.getChildren().add(text);
					EditableText.this.getChildren().remove(textField);
				}

				try {
					_file.getClass().getMethod("set"+_fieldName, java.lang.String.class).invoke(_file, text.getText());
				} catch (Exception e) { e.printStackTrace(); }

			}

		});

		/*
		 * On focus change (to handle clicks in a void area to validate the field)
		 */
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {  
			@Override  
			public void changed(ObservableValue<? extends Boolean> observable,  
					Boolean oldValue, Boolean newValue) {  
				if (! newValue) {  
					text.setText(textField.getText());

					if(textField.getText().length() > 0 && EditableText.this.getChildren().contains(textField)){
						EditableText.this.getChildren().add(text);
						EditableText.this.getChildren().remove(textField);
					}
				}
				
				try {
					_file.getClass().getMethod("set"+_fieldName, java.lang.String.class).invoke(_file, text.getText());
				} catch (Exception e) { e.printStackTrace(); }
			}  
		}); 
		
	}
	
	/*
	 * Getters
	 */
	
	public Label getText(){ return text; }
	public TextField getTextField(){ return textField; }
}
