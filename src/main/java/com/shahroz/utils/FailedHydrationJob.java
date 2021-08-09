package com.shahroz.utils;

import com.shahroz.services.TaskService;

public class FailedHydrationJob extends CallableJob<Boolean> {

	@Override
	public Boolean execute() throws Exception {
		boolean populatedDataStatus = TaskService.failedHydrationJob();
		System.out.println("Completed FailedHydrationJob => " + populatedDataStatus);
		return populatedDataStatus;
	}

	@Override
	public void revert() {
		System.out.println("FailedHydrationJob Reverted");
	}

}
