package se.alipsa.gmdtest

import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage

import se.alipsa.groovy.gmd.*

class GmdTestGui extends Application {

  static void main(String[] args) {
    Application.launch(GmdTestGui.class, args)
  }

  /**
   * The main entry point for all JavaFX applications.
   * The start method is called after the init method has returned,
   * and after the system is ready for the application to begin running.
   *
   * <p>
   * NOTE: This method is called on the JavaFX Application Thread.
   * </p>
   *
   * @param primaryStage the primary stage for this application, onto which
   * the application scene can be set.
   * Applications may create other stages, if needed, but they will not be
   * primary stages.
   * @throws java.lang.Exception if something goes wrong
   */
  @Override
  void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane()
    TextArea ta = new TextArea('''
    # Test
    
    ```{groovy}
    def a = 3
    for (i in 1..a) {
      out.println("Hello ${i}")  
    }
    ```
    
    - first 
    - second
    '''.stripIndent())
    root.setCenter(ta)
    HBox infoBox = new HBox()
    root.setTop(infoBox)
    TextField tf = new TextField()
    HBox.setHgrow(tf, Priority.ALWAYS)
    infoBox.getChildren().add(tf)

    HBox actionBox = new HBox()
    root.setBottom(actionBox)
    File file = new File("build/test.pdf")
    TextField toFile = new TextField(file.getAbsolutePath())
    HBox.setHgrow(toFile, Priority.ALWAYS)
    Button exportButton = new Button("Export to pdf")

    exportButton.setOnAction {
      final String filePath = toFile.getText()
      tf.setText("Exporting to pdf file...")
      Task<Void> task = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
          String txt = ta.getText()
          File f = new File(filePath)
          Gmd gmd = new Gmd()
          gmd.gmdToPdf(txt, f)
          return null
        }
      }
      task.setOnFailed {
        def e = task.getException()
        tf.setText(e.getMessage() + "; " + e.getCause())
      }
      task.setOnSucceeded {
        tf.setText("Exported file to " + filePath + ", size = " + new File(filePath).length() + " bytes")
      }
      Thread thread = new Thread(task)
      thread.setDaemon(false)
      thread.start()
    }
    actionBox.getChildren().addAll(toFile, exportButton)
    primaryStage.setTitle("GmdTestGui")
    primaryStage.setScene(new Scene(root, 600, 400))
    primaryStage.show()
  }
}
