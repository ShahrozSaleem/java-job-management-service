package com.shahroz.utils;

import com.shahroz.services.TaskService;

public class HydrationJob extends CallableJob<Boolean> {

	private String data;

	public HydrationJob(String data) {
		this.data = data;
	}

	@Override
	public Boolean execute() throws Exception {
		boolean populatedDataStatus = TaskService.populateData(this.data);
		System.out.println("Completed HydrationJob => " + populatedDataStatus);
		return populatedDataStatus;
	}

	@Override
	public void revert() {
		System.out.println("HydrationJob Reverted");
	}

}
