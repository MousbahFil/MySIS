import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.mousbah.mysis.user.CourseRegistration;


public class ScreenTest extends Application{
	
	public static void main(String[] args) {
		Application.launch(args); 
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(new CourseRegistration(null));
		stage.setScene(scene);
		stage.setTitle("Screen");
		stage.show();
	}

}
