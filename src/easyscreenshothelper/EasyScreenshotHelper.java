/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyscreenshothelper;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyCode;
import org.jnativehook.keyboard.NativeKeyEvent;

/**
 *
 * @author jakob
 */
public class EasyScreenshotHelper extends Application {

	private HelperStateManager stateManager;
	private Stage stage;
	private ScheduledExecutorService executerService;
	private double defaultY;

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
		Button btn = new Button();
		btn.setText("Run");
		btn.setOnAction((ActionEvent event) -> {
			try {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Where do you want the screenshots to be saved in?");
				File selectedDirectory = dc.showDialog(stage);
				if (selectedDirectory.exists() && selectedDirectory.canWrite()) {
					run(selectedDirectory.getAbsolutePath());
				}
			} catch (Exception ex) {
				Logger.getLogger(EasyScreenshotHelper.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		Button btnClose = new Button();
		btnClose.setText("Stop");
		btnClose.setOnAction((ActionEvent) -> {
			Platform.exit();
		});
		btnClose.setTranslateY(50);

		StackPane root = new StackPane();
		root.getChildren().addAll(btn, btnClose);
		Scene scene = new Scene(root, 300, 250);
		stage.setTitle("EasyScreenshotHelper");
		stage.setScene(scene);
		stage.setOnCloseRequest((WindowEvent event) -> {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException ex) {
				Logger.getLogger(EasyScreenshotHelper.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		stage.show();
		defaultY = stage.getY();
	}

	private void run(String saveDirectory) throws Exception {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			Platform.exit();
		}

		Configuration config = new Configuration();
		config.setKeyCode(NativeKeyEvent.VC_SPACE);
		stateManager = new HelperStateManager(saveDirectory, this);

		GlobalMouseListener mouseListener = new GlobalMouseListener();
		GlobalKeyListener keyListener = new GlobalKeyListener(config);
		mouseListener.addObserver(stateManager);
		keyListener.addObserver(stateManager);
		GlobalScreen.addNativeMouseListener(mouseListener);
		GlobalScreen.addNativeKeyListener(keyListener);

		executerService = Executors.newScheduledThreadPool(1);
		stage.setIconified(true);
	}

	@Override
	public void stop() throws Exception {
		try {
			GlobalScreen.unregisterNativeHook();
			Logger.getLogger("Main").log(Level.INFO, "unregistered global hook");
		} catch (NativeHookException ex) {
			Logger.getLogger(EasyScreenshotHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		executerService.shutdown();
		super.stop();
	}

	public void onScreenshotSaved() {
		Platform.runLater(() -> {
			stage.toFront();  // let task icon blink
			Logger.getLogger("Main").log(Level.INFO, "screenshot saved");
		});

		executerService.schedule(() -> {
			Platform.runLater(() -> {
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				stage.setY(screenSize.getHeight()-60);
				stage.setIconified(false);
			});
		}, 500, TimeUnit.MILLISECONDS);

		executerService.schedule(() -> {
			Platform.runLater(() -> {
				stage.setY(defaultY);
				stage.setIconified(true);
			});
		}, 1000, TimeUnit.MILLISECONDS);

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
