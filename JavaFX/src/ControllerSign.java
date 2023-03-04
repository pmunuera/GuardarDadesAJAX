import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class ControllerSign implements Initializable{
    @FXML
    private TextField localhost;
    @FXML
    private TextField port;
    @FXML
    private TextField protocol;

    @FXML
    private Label txtError;

    @FXML
    private ProgressIndicator loading;
    private int loadingCounter = 0;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
    }

    @FXML
    public void enter(){
        Main.host=localhost.getText();
        Main.port=Integer.parseInt(port.getText());
        Main.protocol=protocol.getText();
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "test");

        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            enterCallback(response);
            hideLoading();
        });
    }

    public void enterCallback(String response){

        JSONObject objResponse = new JSONObject(response);
        if (objResponse.getString("status").equals("OK")) {
            UtilsViews.setViewAnimating("ViewFormulari");
        }
        else{
            System.out.println(objResponse);
            showError();
        }
    }
    public int getPort(){
        return Integer.parseInt(this.port.getText());
    }
    private void showLoading () {
        loadingCounter++;
        loading.setVisible(true);
    }

    private void hideLoading () {
        loadingCounter--;
        if (loadingCounter <= 0) {
            loadingCounter = 0;
            loading.setVisible(false);
        }
    }

    private void showError () {
        // Show the error
        txtError.setVisible(true);
        txtError.setText("Error amb un dels parametres introduits");
        // Hide the error after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> txtError.setVisible(false)));
        timeline.play();
    }
    
}
