package com.leetwise.model;

import java.util.List;

public class CodeExecutionRequest {
    private String code;
    public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTitleSlug() {
		return titleSlug;
	}
	public void setTitleSlug(String titleSlug) {
		this.titleSlug = titleSlug;
	}
	public List<TestCase> getTestCases() {
		return testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
	private String language;
    private String titleSlug;
    private List<TestCase> testCases;
}
