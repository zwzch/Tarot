package com.zwzch.fool.engine.router.model;

public class JoinOn {
	private String leftTableName;
	private String leftColumnName;
	private String rightTableName;
	private String rightColumnName;	
	
	public JoinOn(String leftTableName, String leftColumnName, String rightTableName, String rightColumnName) {
		super();
		this.leftTableName = leftTableName;
		this.leftColumnName = leftColumnName;
		this.rightTableName = rightTableName;
		this.rightColumnName = rightColumnName;
	}
	
	public String getLeftTableName() {
		return leftTableName;
	}
	public void setLeftTableName(String leftTableName) {
		this.leftTableName = leftTableName;
	}
	public String getLeftColumnName() {
		return leftColumnName;
	}
	public void setLeftColumnName(String leftColumnName) {
		this.leftColumnName = leftColumnName;
	}
	public String getRightTableName() {
		return rightTableName;
	}
	public void setRightTableName(String rightTableName) {
		this.rightTableName = rightTableName;
	}
	public String getRightColumnName() {
		return rightColumnName;
	}
	public void setRightColumnName(String rightColumnName) {
		this.rightColumnName = rightColumnName;
	}
	
	

}
