package com.shahroz.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.shahroz.enums.JobState;
import com.shahroz.utils.CallableJob;
import com.shahroz.utils.RunnableJob;

public class JobManagementService {

	private static JobManagementService instance = null;
	private Map<String, RunnableJob> runableJobHolder = null;
	@SuppressWarnings("rawtypes")
	private Map<String, CallableJob> callableJobHolder = null;
	private ExecutorService jobExecutor = null;
	private ScheduledExecutorService scheduledJobExecutor = null;

	private JobManagementService(int poolSize) {
		this.runableJobHolder = new HashMap<>();
		this.callableJobHolder = new HashMap<>();
		this.jobExecutor = Executors.newFixedThreadPool(poolSize);
		this.scheduledJobExecutor = Executors.newScheduledThreadPool((poolSize / 2) + 1);
	}

	public static JobManagementService getInstance() {
		int defaultPoolSize = Runtime.getRuntime().availableProcessors() * 2;

		if (JobManagementService.instance == null)
			JobManagementService.instance = new JobManagementService(defaultPoolSize);
		return JobManagementService.instance;
	}

	public static JobManagementService getInstance(int poolSize) {
		if (JobManagementService.instance == null)
			JobManagementService.instance = new JobManagementService(poolSize);
		return JobManagementService.instance;
	}

	public void executeJob(String jobName, RunnableJob job) throws Exception {
		if (this.runableJobHolder.containsKey(jobName))
			throw new Exception("Job Already exists");

		this.runableJobHolder.put(jobName, job);
		this.jobExecutor.submit(job);
	}

	public <T> Future<T> executeJob(String jobName, CallableJob<T> job) throws Exception {
		if (this.callableJobHolder.containsKey(jobName))
			throw new Exception("Job Already exists");

		this.callableJobHolder.put(jobName, job);

		return this.jobExecutor.submit(job);
	}

	public <T> CompletableFuture<T> executeJob(String jobName, CallableJob<T> job, long timeout, TimeUnit unit)
			throws Exception {

		if (this.callableJobHolder.containsKey(jobName))
			throw new Exception("Job Already exists");

		Supplier<T> supplier = () -> {
			try {
				Future<T> executeJob = JobManagementService.getInstance().executeJob(jobName, job);
				return executeJob.get(timeout, unit);
			} catch (Exception e) {
				System.err.println("JobManagementService Error for " + jobName + " => " + e.getMessage());
				return null;
			}
		};

		return new CompletableFuture<T>().completeAsync(supplier, jobExecutor);
	}

	public <T> Future<T> schedule(String jobName, CallableJob<T> job, long delay, TimeUnit unit) {
		this.callableJobHolder.put(jobName, job);
		return this.scheduledJobExecutor.schedule(job, delay, unit);
	}

	public void schedule(String jobName, RunnableJob job, long delay, TimeUnit unit) {
		this.runableJobHolder.put(jobName, job);
		this.scheduledJobExecutor.schedule(job, delay, unit);
	}

	public void scheduleAtFixedRate(String jobName, RunnableJob job, long initialDelay, long period, TimeUnit unit) {
		this.runableJobHolder.put(jobName, job);
		this.scheduledJobExecutor.scheduleAtFixedRate(job, initialDelay, period, unit);
	}

	public void scheduleWithFixedDelay(String jobName, RunnableJob job, long initialDelay, long delay, TimeUnit unit) {
		this.runableJobHolder.put(jobName, job);
		this.scheduledJobExecutor.scheduleWithFixedDelay(job, initialDelay, delay, unit);
	}

	public JobState getJobState(String jobName) {
		JobState state = null;
		if (this.runableJobHolder.containsKey(jobName)) {
			state = this.runableJobHolder.get(jobName).getJobState();
		} else if (this.callableJobHolder.containsKey(jobName)) {
			state = this.callableJobHolder.get(jobName).getJobState();
		}
		return state;
	}

	public boolean isSettled(String jobName) {
		boolean settled = true;
		JobState jobState = null;

		if (this.runableJobHolder.containsKey(jobName))
			jobState = this.runableJobHolder.get(jobName).getJobState();
		else if (this.callableJobHolder.containsKey(jobName))
			jobState = this.callableJobHolder.get(jobName).getJobState();

		settled = jobState == null ? true : (jobState.equals(JobState.SUCCESS) || jobState.equals(JobState.FAILED));

		return settled;
	}

	public void shutdown() {
		this.jobExecutor.shutdown();
		this.scheduledJobExecutor.shutdown();
	}

	public List<Runnable> shutdownNormalJob() {
		return this.jobExecutor.shutdownNow();
	}

	public List<Runnable> shutdownSchedualJobs() {
		return this.scheduledJobExecutor.shutdownNow();
	}

	public Map<String, List<Runnable>> shutdownAllJobs() {
		Map<String, List<Runnable>> pendingJobs = new HashMap<>();
		List<Runnable> normal = this.jobExecutor.shutdownNow();
		List<Runnable> scheduled = this.scheduledJobExecutor.shutdownNow();
		pendingJobs.put("normal", normal);
		pendingJobs.put("scheduled", scheduled);
		return pendingJobs;
	}

	public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
		boolean jobExecutorAwait = this.jobExecutor.awaitTermination(timeout, unit);
		boolean scheduledJobExecutorAwait = this.scheduledJobExecutor.awaitTermination(timeout, unit);
		return (jobExecutorAwait && scheduledJobExecutorAwait);
	}
}
