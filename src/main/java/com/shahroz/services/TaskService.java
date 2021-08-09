package com.shahroz.services;

public abstract class TaskService {

	public static String getApiData() {
		try {
			Thread.sleep(2000);
			return "fetched api data";
		} catch (InterruptedException e) {
			System.err.println("TaskService Error for getApiData => " + e.getMessage());
			return null;
		}
	}

	public static boolean populateData(String data) {
		try {
			Thread.sleep(3000);
			return true;
		} catch (InterruptedException e) {
			System.err.println("TaskService Error for populateData => " + e.getMessage());
			return false;
		}
	}

	public static boolean failedHydrationJob() throws Exception {
		try {
			Thread.sleep(1000);
			throw new Exception("Data Hydration failed");
		} catch (InterruptedException e) {
			System.err.println("TaskService Error for failedHydrationJob => " + e.getMessage());
			return false;
		}
	}

	public static void sendEmail() {
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			System.err.println("TaskService Error for sendEmail => " + e.getMessage());
		}
	}
}
