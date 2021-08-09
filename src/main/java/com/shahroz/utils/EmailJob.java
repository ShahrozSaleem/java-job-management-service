package com.shahroz.utils;

import com.shahroz.services.TaskService;

public class EmailJob extends RunnableJob {

	@Override
	public void execute() {
		TaskService.sendEmail();
		System.out.println("Completed EmailJob");
	}

	@Override
	public void revert() {
	}

}
