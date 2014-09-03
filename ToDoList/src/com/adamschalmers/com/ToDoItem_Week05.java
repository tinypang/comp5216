package com.adamschalmers.com;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ToDoItems")
public class ToDoItem_Week05 extends Model {
	
    @Column(name = "Name")
    public String name;
    
    // Default constructor (all AndroidActive models need one!)
	public ToDoItem_Week05() { 
		super();
	}
	
	public ToDoItem_Week05(String n) {
		super();
		this.name = n;
	}

}