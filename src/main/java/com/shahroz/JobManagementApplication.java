package com.shahroz;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.shahroz.services.JobManagementService;
import com.shahroz.utils.ApiJob;
import com.shahroz.utils.EmailJob;
import com.shahroz.utils.FailedHydrationJob;
import com.shahroz.utils.HydrationJob;

public class JobManagementApplication {

	public static void main(String[] args) {

		JobManagementService service = JobManagementService.getInstance(1);

		try {
			// Callable Jobs
			Future<String> apiRes = service.executeJob("Isolated Api Job 1", new ApiJob());
			Future<Boolean> hydrationRes = service.executeJob("Isolated Hydration Job 2",
					new HydrationJob("some data"));
			Future<Boolean> failedHydrationRes = service.executeJob("Failed Hydration Job 3", new FailedHydrationJob());

			// Runnable Jobs
			service.executeJob("Isolated Email Job 4", new EmailJob());
			service.schedule("Scheduled Job 5", new EmailJob(), 100, TimeUnit.MILLISECONDS);

			System.out.println("=================================================== ");
			System.out.println("============ Current Status of all Jobs =========== ");
			System.out.println("=================================================== ");
			System.out.println("Isolated Api Job 1 ========> " + service.getJobState("Isolated Api Job 1"));
			System.out.println("Isolated Hydration Job 2 ==> " + service.getJobState("Isolated Hydration Job 2"));
			System.out.println("Failed Hydration Job 3 ====> " + service.getJobState("Failed Hydration Job 3"));
			System.out.println("Isolated Email Job 4 ======> " + service.getJobState("Isolated Email Job 4"));
			System.out.println("Scheduled Job 5 ===========> " + service.getJobState("Scheduled Job 5"));
			System.out.println("=================================================== \n");

			service.shutdown();

			service.awaitTermination(17, TimeUnit.SECONDS);

			System.out.println("\n=================================================== ");
			System.out.println("============ Final Status of all Jobs ============= ");
			System.out.println("=================================================== ");
			System.out.println("Isolated Api Job 1 ========> " + service.getJobState("Isolated Api Job 1") + " ("
					+ apiRes.get() + ")");
			System.out.println("Isolated Hydration Job 2 ==> " + service.getJobState("Isolated Hydration Job 2") + " ("
					+ hydrationRes.get() + ")");
			System.out.println("Failed Hydration Job 3 ====> " + service.getJobState("Failed Hydration Job 3") + "  ("
					+ failedHydrationRes.get() + ")");
			System.out.println("Isolated Email Job 4 ======> " + service.getJobState("Isolated Email Job 4"));
			System.out.println("Scheduled Job 5 ===========> " + service.getJobState("Scheduled Job 5"));
			System.out.println("=================================================== \n");

		} catch (Exception e) {
			e.printStackTrace();
			service.shutdown();
		}
	}
}
