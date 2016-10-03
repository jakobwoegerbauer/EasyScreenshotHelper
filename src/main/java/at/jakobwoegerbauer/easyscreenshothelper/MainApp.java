/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.control.CheckBox;
import org.jnativehook.keyboard.NativeKeyEvent;

/**
 *
 * @author jakob
 */
public class MainApp extends Application {

	private HelperStateManager stateManager;
	private Stage stage;
	private ScheduledExecutorService executerService;
	private CheckBox chboxSaveAll;
	
	private final Image ICON_NOTIFY1 = new Image("notify1.jpg");
	private final Image ICON_NOTIFY2 = new Image("notify2.jpg");

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		// Get the logger for "org.jnativehook" and set the level to warning.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);
		createUi(stage);
	}

	private void createUi(Stage stage) {
		stage.getIcons().add(new Image("photo-camera.png"));

		chboxSaveAll = new CheckBox("save every screenshot");
		chboxSaveAll.setVisible(false);
		chboxSaveAll.setTranslateY(50);
		chboxSaveAll.setOnAction((ActionEvent e) -> {
			stateManager.setSaveAll(chboxSaveAll.isSelected());
		});

		Button btn = new Button();
		btn.setText("Run");
		btn.setOnAction((ActionEvent event) -> {
			try {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Where do you want the screenshots to be saved in?");
				File selectedDirectory = dc.showDialog(stage);
				if (selectedDirectory.exists() && selectedDirectory.canWrite()) {
					run(selectedDirectory.getAbsolutePath());
					chboxSaveAll.setVisible(true);
				}
			} catch (Exception ex) {
				Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		Button btnClose = new Button();
		btnClose.setText("Stop");
		btnClose.setOnAction((ActionEvent) -> {
			stopRunner();
		});
		btnClose.setTranslateY(25);

		StackPane root = new StackPane();
		root.getChildren().addAll(btn, btnClose, chboxSaveAll);
		Scene scene = new Scene(root, 300, 250);
		stage.setTitle("EasyScreenshotHelper");
		stage.setScene(scene);
		stage.setOnCloseRequest((WindowEvent event) -> {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException ex) {
				Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		stage.show();
	}

	private void run(String saveDirectory) throws Exception {
		executerService = Executors.newScheduledThreadPool(1);
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			Platform.exit();
		}
		stateManager = new HelperStateManager(saveDirectory, this);

		Configuration config = new Configuration();
		config.setKeyCode(NativeKeyEvent.VC_D);

		GlobalMouseListener mouseListener = new GlobalMouseListener();
		GlobalKeyListener keyListener = new GlobalKeyListener(config);
		mouseListener.addObserver(stateManager);
		keyListener.addObserver(stateManager);
		GlobalScreen.addNativeMouseListener(mouseListener);
		GlobalScreen.addNativeKeyListener(keyListener);

		stage.setIconified(true);
	}

	private void stopRunner() {
		try {
			GlobalScreen.unregisterNativeHook();
			Logger.getLogger("Main").log(Level.INFO, "unregistered global hook");
			chboxSaveAll.setVisible(false);
			executerService.shutdown();
		} catch (NativeHookException ex) {
			Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void stop() throws Exception {
		stopRunner();
		super.stop();
	}

	public void onScreenshotSaved() {
		Logger.getLogger("Main").log(Level.INFO, "onScreenshotSaved()");
		final ObjectWrapper<Image> defaultImageWrapper = new ObjectWrapper<>();

		Platform.runLater(() -> {
			defaultImageWrapper.setValue(stage.getIcons().remove(0));
			stage.getIcons().add(ICON_NOTIFY1);
		});
		int timing = 175;
		int limit = 4;
		for (int i = 1; i <= limit; i++) {
			if (i % 2 == 1) {
				executerService.schedule(() -> {
					Platform.runLater(() -> {
						stage.getIcons().remove(0);
						stage.getIcons().add(ICON_NOTIFY2);
					});
				}, timing * i, TimeUnit.MILLISECONDS);
			} else {
				executerService.schedule(() -> {
					Platform.runLater(() -> {
						stage.getIcons().remove(0);
						stage.getIcons().add(ICON_NOTIFY1);
					});
				}, timing * i, TimeUnit.MILLISECONDS);
			}
		}
		executerService.schedule(() -> {
			Platform.runLater(() -> {
				stage.getIcons().remove(0);
				stage.getIcons().add(defaultImageWrapper.getValue());
			});
		}, timing * (limit + 1), TimeUnit.MILLISECONDS);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
