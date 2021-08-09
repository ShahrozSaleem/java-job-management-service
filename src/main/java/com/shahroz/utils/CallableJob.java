package com.shahroz.utils;

import java.util.concurrent.Callable;

import com.shahroz.enums.JobState;

public abstract class CallableJob<T> implements Callable<T> {

	private JobState jobState = JobState.QUEUED;

	public CallableJob() {
	}

	public JobState getJobState() {
		return jobState;
	}
	
	public void setJobState(JobState jobState) {
		this.jobState = jobState;
	}

	@Override
	public final T call() {
		this.jobState = JobState.RUNNING;
		T response;

		try {
			response = this.execute();
			this.jobState = JobState.SUCCESS;
			return response;

		} catch (Exception e) {
			System.err.println("Callable Job Error => "+ e.getMessage());
			this.jobState = JobState.FAILED;
			this.revert();

			response = null;
		}

		return response;
	}

	JobState getState() {
		return this.jobState;
	}

	public abstract T execute() throws Exception;

	public abstract void revert();

}
