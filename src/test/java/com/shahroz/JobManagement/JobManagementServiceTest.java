package com.shahroz.JobManagement;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.shahroz.enums.JobState;
import com.shahroz.services.JobManagementService;
import com.shahroz.utils.ApiJob;
import com.shahroz.utils.EmailJob;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JobManagementServiceTest extends TestCase {

	private JobManagementService service;

	public JobManagementServiceTest() {
		super("JobManagementService Test");
		this.service = JobManagementService.getInstance(1);
	}

	public static Test suite() {
		return new TestSuite(JobManagementServiceTest.class);
	}

	public void test() {

		System.out.println("============ JobManagementService Test ============ ");

		assertTrue(this.callableJobTest());
		assertTrue(this.callableJobTest2());
		assertTrue(this.runnableJobTest());
		assertTrue(this.scheduleJobTest());
		assertTrue(this.getJobStateTest());
		assertTrue(this.isSettledTest());
		
		this.service.shutdown();
	}

	public boolean callableJobTest() {
		boolean result = true;
		try {
			ApiJob callableJob = new ApiJob();
			Future<String> futureJob = this.service.executeJob("callableJobTest", callableJob);
			result = !futureJob.isDone(); // ApiJob is delayed Job so it will be false ATM

			if (result)
				System.out.println("callableJobTest ===========================> Passed ");
			else
				System.out.println("callableJobTest ===========================> Failed ");

		} catch (Exception e) {
			System.err.println("callableJobTest Failed");
			result = false;
		}
		return result;
	}

	public boolean callableJobTest2() {
		boolean result = true;
		try {
			ApiJob callableJob = new ApiJob();
			this.service.executeJob("callableJobTest2", callableJob);
			result = callableJob.getJobState().equals(JobState.QUEUED);

			if (result)
				System.out.println("callableJobTest2 ==========================> Passed ");
			else
				System.out.println("callableJobTest2 ==========================> Failed ");

		} catch (Exception e) {
			System.out.println("callableJobTest2 ==========================> Failed ");
			result = false;
		}
		return result;
	}

	public boolean runnableJobTest() {
		boolean result = true;
		try {
			EmailJob runnableJob = new EmailJob();
			this.service.executeJob("runnableJobTest", runnableJob);
			System.out.println("runnableJobTest ===========================> Passed ");
		} catch (Exception e) {
			System.out.println("runnableJobTest ===========================> Failed ");
			result = false;
		}
		return result;
	}

	public boolean scheduleJobTest() {
		boolean result = true;
		try {
			ApiJob callableJob = new ApiJob();
			this.service.schedule("scheduleJobTest", callableJob, 1000, TimeUnit.MILLISECONDS);
			result = callableJob.getJobState().equals(JobState.QUEUED);

			if (result)
				System.out.println("scheduleJobTest ===========================> Passed ");
			else
				System.out.println("scheduleJobTest ===========================> Failed ");

		} catch (Exception e) {
			System.out.println("scheduleJobTest ===========================> Failed ");
			result = false;
		}
		return result;
	}
	
	public boolean getJobStateTest() {
		ApiJob job = new ApiJob();
		boolean result = job.getJobState().equals(JobState.QUEUED);
		if(result)
			System.out.println("getJobStateTest ===========================> Passed ");
		else
			System.out.println("getJobStateTest ===========================> Failed ");
		return result;
	}
	
	public boolean isSettledTest() {
		boolean result = service.isSettled("Job that does not exists");
		if(result)
			System.out.println("isSettledTest =============================> Passed ");
		else
			System.out.println("isSettledTest =============================> Failed ");
		return result;
	}
}
