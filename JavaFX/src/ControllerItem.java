import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

public class ControllerItem {
    
    @FXML
    private Label title, id;
    @FXML
    private ImageView image;

    @FXML
    private void handleMenuAction() {
        ControllerModificar cm = (ControllerModificar) UtilsViews.getController("ViewModificar");
        cm.usuariModificar(Integer.parseInt(id.getText()));
        UtilsViews.setViewAnimating("ViewModificar");
    }
    @FXML
    private void setImageVisible(){
        this.image.setVisible(true);
    }
    @FXML
    private void setImageInvisible(){
        this.image.setVisible(false);
    }
    public void setTitle(String title) {
        this.title.setText(title);
    }
    public String getTitle(){
        return this.title.getText();
    }
    public String getId(){
        return this.id.getText();
    }
    public void setId(int int1) {
        this.id.setText(String.valueOf(int1));
    }
    public ImageView getImageView(){
        return this.image;
    }
}
