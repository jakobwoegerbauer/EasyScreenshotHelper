/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Simon
 */
public class NotificationHelper {

	private static final long NOTIFICATION_DELAY_MS = 50;
	private static final Image ICON_NOTIFY1 = new Image("notify1.jpg");
	private static final Image ICON_NOTIFY2 = new Image("notify2.jpg");
	final ObjectWrapper<Image> defaultImageWrapper = new ObjectWrapper<>();

	private Stage stage;
	private ScheduledExecutorService executerService;
	private long lastNotificationEnd_ms;
	private boolean notifying;

	public NotificationHelper(Stage stage) {
		this.stage = stage;
		this.lastNotificationEnd_ms = 0;
		this.notifying = false;
		executerService = Executors.newScheduledThreadPool(1);
	}

	public void notifyUser() {
		notifyUser(
				/* blinkspeed_ms: */ 175,
				/* blinks:        */   5);
	}

	private void notifyUser(int blinkspeed_ms, int blinks) {
		if (lastNotificationEnd_ms < System.currentTimeMillis()) { // no current notification
			notifyUser(blinkspeed_ms, blinks, /* timout_ms: */ 0);
		} else { // delay notification
			long timeout_ms = lastNotificationEnd_ms + NOTIFICATION_DELAY_MS;
			long duration_ms = blinkspeed_ms * blinks - timeout_ms;
			blinks = (int)(duration_ms / blinkspeed_ms) + 1;
			notifyUser(blinkspeed_ms, blinks, timeout_ms);
		}
	}

	private void notifyUser(int blinkspeed_ms, int blinks, long timeout_ms) {
		executerService.schedule(() -> {
			Platform.runLater(() -> {
				defaultImageWrapper.setValue(stage.getIcons().remove(0));
				stage.getIcons().add(ICON_NOTIFY1);
			});
		}, timeout_ms, TimeUnit.MILLISECONDS);
		for (int i = 1; i < blinks; i++) {
			if (i % 2 == 1) {
				executerService.schedule(() -> {
					Platform.runLater(() -> {
						stage.getIcons().remove(0);
						stage.getIcons().add(ICON_NOTIFY2);
					});
				}, timeout_ms + blinkspeed_ms * i, TimeUnit.MILLISECONDS);
			} else {
				executerService.schedule(() -> {
					Platform.runLater(() -> {
						stage.getIcons().remove(0);
						stage.getIcons().add(ICON_NOTIFY1);
					});
				}, timeout_ms + blinkspeed_ms * i, TimeUnit.MILLISECONDS);
			}
		}
		executerService.schedule(() -> {
			Platform.runLater(() -> {
				stage.getIcons().remove(0);
				stage.getIcons().add(defaultImageWrapper.getValue());
			});
		}, blinkspeed_ms * blinks + timeout_ms, TimeUnit.MILLISECONDS);
		lastNotificationEnd_ms = blinkspeed_ms * blinks + timeout_ms + System.currentTimeMillis();
	}
	
	public void dispose() {
		executerService.shutdown();
	}
}
