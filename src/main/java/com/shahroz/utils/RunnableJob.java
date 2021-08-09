package com.shahroz.utils;

import com.shahroz.enums.JobState;

public abstract class RunnableJob implements Runnable {
	private JobState jobState = JobState.QUEUED;

	public RunnableJob() {
	}
	
	public JobState getJobState() {
		return jobState;
	}
	
	public void setJobState(JobState jobState) {
		this.jobState = jobState;
	}

	@Override
	public final void run() {
		this.jobState = JobState.RUNNING;
		try {
			this.execute();
			this.jobState = JobState.SUCCESS;
		} catch (Exception e) {
			System.err.println("Runable Job Error => "+ e.getMessage());
			this.jobState = JobState.FAILED;
		}
	}

	JobState getState() {
		return this.jobState;
	}

	public abstract void execute();

	public abstract void revert();
}
