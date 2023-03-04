import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
public class ControllerUserList implements Initializable{
    
    @FXML
    private VBox vBoxList = new VBox();
    @FXML
    private Label txtError;

    @FXML
    private ProgressIndicator loading;

    private int loadingCounter = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }
    public void mostrarVista(){
        
        vBoxList.getChildren().clear();
        // Load list of consoles for this brand
        JSONObject obj = new JSONObject("{}");
        obj.put("type","carrega");
        // Ask for data
        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            loadList(response);
            hideLoading();
        });
    }

    private void loadList (String response) {
        
        JSONObject objResponse = new JSONObject(response);
        System.out.println(objResponse);
         if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            URL resource = this.getClass().getResource("./assets/listItem.fxml");
            
            // Clear the list of consoles
            vBoxList.getChildren().clear();
            // Add received consoles from the JSON to the yPane (VBox) list
            for (int i = 0; i < JSONlist.length(); i++) {

                // Get console information
                JSONObject user = JSONlist.getJSONObject(i);

                    try {
                    // Load template and set controller
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent itemTemplate = loader.load();
                    ControllerItem itemController = loader.getController();
                        
                    
                    // Fill template with console information
                    itemController.setTitle(user.getString("firstName"));                     
                    itemController.setId(user.getInt("id"));
                    // Add template to the list
                    vBoxList.getChildren().add(itemTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else{
          //  showError();
        }
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

    @FXML
    public void goBack() {
        UtilsViews.setViewAnimating("ViewSign");
    }
    @FXML
    public void goToForm() {
        UtilsViews.setViewAnimating("ViewFormulari");
    }
}