/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorialhelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

/**
 *
 * @author jakob
 */
public class TutorialHelper extends Application {

	private HelperStateManager stateManager;

	@Override
	public void start(Stage primaryStage) {
		// Get the logger for "org.jnativehook" and set the level to warning.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);

		Button btn = new Button();
		btn.setText("Run");
		btn.setOnAction((ActionEvent event) -> {
			try {
				run();
			} catch (Exception ex) {
				Logger.getLogger(TutorialHelper.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		Button btnClose = new Button();
		btnClose.setText("Stop");
		btnClose.setOnAction((ActionEvent) -> {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException ex) {
				Logger.getLogger(TutorialHelper.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		btnClose.setTranslateY(50);

		StackPane root = new StackPane();
		root.getChildren().addAll(btn, btnClose);
		Scene scene = new Scene(root, 300, 250);
		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((WindowEvent event) -> {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException ex) {
				Logger.getLogger(TutorialHelper.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		primaryStage.show();

	}

	private void run() throws Exception {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		Configuration config = new Configuration();
		config.setKeyCode(43);
		stateManager = new HelperStateManager();

		GlobalMouseListener mouseListener = new GlobalMouseListener();
		GlobalKeyListener keyListener = new GlobalKeyListener(config);
		mouseListener.addObserver(stateManager);
		keyListener.addObserver(stateManager);
		GlobalScreen.addNativeMouseListener(mouseListener);
		GlobalScreen.addNativeKeyListener(keyListener);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
