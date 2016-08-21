import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

/**
 * Created by Marcus on 2016-08-16.
 */
public class ClientController {

    Socket clientSocket;

    @FXML
    ListView<String> ageListView, styleListView;

    @FXML
    TextField lengthTextField;

    @FXML
    Label recommendLabel;

    @FXML
    public void initialize(){
        styleListView.setDisable(true);
        ageListView.setItems(FXCollections.observableArrayList (
                SkiGuidePacket.Age.ZEROFOUR.getDescription(),
                SkiGuidePacket.Age.FIVEEIGHT.getDescription(),
                SkiGuidePacket.Age.NINEPLUS.getDescription()));

        styleListView.setItems(FXCollections.observableArrayList (
                SkiGuidePacket.Style.KLASSISK.name().toLowerCase(),
                SkiGuidePacket.Style.FRISTIL.name().toLowerCase()));
    }

    /*
    * Disables styleListView if NINEPLUS is not selected
     */
    public void onAgeListInteraction(){
        if(ageListView.getSelectionModel().getSelectedItem().equals(SkiGuidePacket.Age.NINEPLUS.getDescription())){
            styleListView.setDisable(false);
        } else {
            styleListView.setDisable(true);
        }

    }

    public void onConfirmButtonPressed(){
        SkiGuidePacket packet = createSkiGuidePacket();
        sendPacket(packet);

    }

    private SkiGuidePacket createSkiGuidePacket(){
        SkiGuidePacket.Age age;
        String selectedAge = ageListView.getSelectionModel().getSelectedItem();
        if(selectedAge.equals(SkiGuidePacket.Age.ZEROFOUR.getDescription())){
            age = SkiGuidePacket.Age.ZEROFOUR;
        } else if(selectedAge.equals(SkiGuidePacket.Age.FIVEEIGHT.getDescription())){
            age = SkiGuidePacket.Age.FIVEEIGHT;
        } else {//if(selectedAge.equals(SkiGuidePacket.Age.NINEPLUS.getDescription())){
            age = SkiGuidePacket.Age.NINEPLUS;
        }

        SkiGuidePacket.Style style;
        String selectedStyle = styleListView.getSelectionModel().getSelectedItem().toUpperCase();
        if(selectedStyle.equals(SkiGuidePacket.Style.KLASSISK.name())){
            style = SkiGuidePacket.Style.KLASSISK;
        } else { //if (selectedStyle.equals(SkiGuidePacket.Style.FRISTIL.name())){
            style = SkiGuidePacket.Style.FRISTIL;
        }

        return new SkiGuidePacket(age, style, Integer.parseInt(lengthTextField.getText()));

    }

    private void sendPacket(SkiGuidePacket packet){
        try {
            clientSocket = new Socket("127.0.0.1", 54555);
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

            outToServer.writeObject(packet);
            receivePacket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivePacket(){
        try {
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String result = dIn.readUTF();
            recommendLabel.setText(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
