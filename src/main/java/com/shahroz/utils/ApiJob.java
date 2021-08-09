package com.shahroz.utils;

import com.shahroz.services.TaskService;

public class ApiJob extends CallableJob<String> {

	@Override
	public String execute() throws Exception {
		String apiData = TaskService.getApiData();
		System.out.println("Completed ApiJob => "+ apiData);
		return apiData;
	}

	@Override
	public void revert() {
	}

}
