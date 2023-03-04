import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class ControllerModificar implements Initializable {
    @FXML
    private TextField nom,cognoms,correu_electronic,telefon,direccio,ciutat;

    @FXML
    private Label title,usuari,errorNom,errorCognoms,errorCorreu,errorTelefon,errorDireccio,errorCiutat,textAfegit;
    @FXML
    private Button modificar;
    @FXML
    private ProgressIndicator loading;
    @FXML
    private HBox hBox;
    private int idUsuari;
    private int loadingCounter = 0;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
    }

    @FXML
    public void modificar(){
        errorNom.setVisible(false);
        errorCognoms.setVisible(false);
        errorCorreu.setVisible(false);
        errorTelefon.setVisible(false);
        errorDireccio.setVisible(false);
        errorCiutat.setVisible(false);
        JSONObject obj = new JSONObject("{}");
        obj.put("type", "modify");
        obj.put("userId", this.idUsuari);
        obj.put("firstName", this.nom.getText());
        obj.put("lastName", this.cognoms.getText());
        obj.put("mail", this.correu_electronic.getText());
        obj.put("phone", this.telefon.getText());
        obj.put("direction", this.direccio.getText());
        obj.put("city", this.ciutat.getText());
        //JSONObject obj = new JSONObject("{}");
        //obj.put("type", "test");
        showLoading();
        UtilsHTTP.sendPOST(Main.protocol + "://" + Main.host + ":" + Main.port + "/dades", obj.toString(), (response) -> {
            System.out.println(response);
            enterCallback(response);
            hideLoading();
        });
    }
    public void usuariModificar(int id){
        nom.setText("");
        cognoms.setText("");
        correu_electronic.setText("");
        telefon.setText("");
        direccio.setText("");
        ciutat.setText("");
        idUsuari=id;
        usuari.setText("Usuari: "+id);
    }
    public void enterCallback(String response){

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            showModificat();
        }
        else{
            if(objResponse.getString("status").contains("NO_fname")){
                errorNom.setText("Nom buit");
                errorNom.setVisible(true);
            }
            if(objResponse.getString("status").contains("NO_lname")){
                errorCognoms.setText("Cognom buit");
                errorCognoms.setVisible(true);
            }
            if(objResponse.getString("status").contains("NO_mail")){
                errorCorreu.setText("Correu electronic buit");
                errorCorreu.setVisible(true);
            }
            if(objResponse.getString("status").contains("NO_phone")){
                errorTelefon.setText("Telefon buit");
                errorTelefon.setVisible(true);
            }
            else if(objResponse.getString("status").contains("NO_type")){
                errorTelefon.setText("Telefon ha de ser numeric");
                errorTelefon.setVisible(true);
            }
            else if(objResponse.getString("status").contains("NO_len")){
                errorTelefon.setText("Longitud telefon incorrecta");
                errorTelefon.setVisible(true);
            }
            if(objResponse.getString("status").contains("NO_dir")){
                errorDireccio.setText("Direccio buida");
                errorDireccio.setVisible(true);
            }
            if(objResponse.getString("status").contains("NO_city")){
                errorCiutat.setText("Ciutat buida");
                errorCiutat.setVisible(true);
            }
        }
    }
    @FXML
    public void goToList(){
        ControllerUserList cl = (ControllerUserList ) UtilsViews.getController("ViewList");
        cl.mostrarVista();
        UtilsViews.setViewAnimating("ViewList");
    }
    @FXML
    public void goToForm(){
        UtilsViews.setViewAnimating("ViewFormulari");
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
    private void showModificat () {
        // Show the error
        textAfegit.setText("Usuari modificat exitosament!");
        textAfegit.setVisible(true);
        // Hide the error after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> textAfegit.setVisible(false)));
        timeline.play();
    }
    
    @FXML
    public void goBack() {
        UtilsViews.setViewAnimating("ViewSign");
    }
    
}
