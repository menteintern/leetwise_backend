package com.leetwise.model;

public class TestCase {
    private String input;
    
    public TestCase() {}
    
    public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getExpectedOutput() {
		return expectedOutput;
	}
	public void setExpectedOutput(String expectedOutput) {
		this.expectedOutput = expectedOutput;
	}
	private String expectedOutput;
}
