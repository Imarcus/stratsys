import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    Label recommendLabel, lengthLabel, styleLabel, warningLabel;

    @FXML
    Button confirmButton;

    @FXML
    public void initialize(){
        warningLabel.setVisible(false);
        recommendLabel.setVisible(false);
        lengthLabel.setVisible(false);
        styleListView.setDisable(true);
        styleLabel.setDisable(true);
        confirmButton.setDisable(true);
        ageListView.setItems(FXCollections.observableArrayList (
                SkiRequestPacket.Age.ZEROFOUR.getDescription(),
                SkiRequestPacket.Age.FIVEEIGHT.getDescription(),
                SkiRequestPacket.Age.NINEPLUS.getDescription()));

        styleListView.setItems(FXCollections.observableArrayList (
                SkiRequestPacket.Style.KLASSISK.name().toLowerCase(),
                SkiRequestPacket.Style.FRISTIL.name().toLowerCase()));
    }

    /**
    * Disables styleListView if NINEPLUS is not selected
     */
    public void onAgeListInteraction(){
        if(ageListView.getSelectionModel().getSelectedItem().equals(SkiRequestPacket.Age.NINEPLUS.getDescription())){
            styleListView.setDisable(false);
            styleLabel.setDisable(false);
        } else {
            styleListView.setDisable(true);
            styleLabel.setDisable(true);
        }
        validateForm();
    }

    public void onStyleListInteraction(){
        validateForm();
    }

    public void onLengthFieldKeyReleased(){
        validateForm();
    }

    public void onConfirmButtonPressed(){
        SkiRequestPacket packet = createSkiRequestPacket();
        sendRequest(packet);
    }



    /**
     * Checks whether the user input is correctly filled out.
     */
    public void validateForm(){
        try {
            int length = Integer.parseInt(lengthTextField.getText()); //Throws exception if not parseable
            if(ageListView.getSelectionModel().getSelectedItem() != null && length < 500){ //If an age is selected and length is less than 500
                if(!ageListView.getSelectionModel().getSelectedItem().equals(SkiRequestPacket.Age.NINEPLUS.getDescription()) || //If NINEPLUS is not selected, or it is selected and a style is also selected
                        (ageListView.getSelectionModel().getSelectedItem().equals(SkiRequestPacket.Age.NINEPLUS.getDescription()) &&
                                styleListView.getSelectionModel().getSelectedItem() != null)){
                    confirmButton.setDisable(false);
                } else {
                    confirmButton.setDisable(true);
                }
            } else {
                confirmButton.setDisable(true);
            }
        } catch (NumberFormatException e){
            confirmButton.setDisable(true);
        }
    }

    /**
     * Creates a SkiRequestPacket from selected info in the GUI
     * @return SkiRequestPacket to be sent to the server
     */
    private SkiRequestPacket createSkiRequestPacket(){
        SkiRequestPacket.Age age;
        SkiRequestPacket.Style style;

        String selectedAge = ageListView.getSelectionModel().getSelectedItem();
        if(selectedAge.equals(SkiRequestPacket.Age.ZEROFOUR.getDescription())){
            age = SkiRequestPacket.Age.ZEROFOUR;
            style = null;

        } else if(selectedAge.equals(SkiRequestPacket.Age.FIVEEIGHT.getDescription())){
            age = SkiRequestPacket.Age.FIVEEIGHT;
            style = null;
        } else {//if(selectedAge.equals(SkiGuidePacket.Age.NINEPLUS.getDescription())){
            age = SkiRequestPacket.Age.NINEPLUS;
            if(styleListView.getSelectionModel().getSelectedItem() != null && !styleListView.isDisable()) { //If a style is selected
                String selectedStyle = styleListView.getSelectionModel().getSelectedItem().toUpperCase();
                if(selectedStyle.equals(SkiRequestPacket.Style.KLASSISK.name())){
                    style = SkiRequestPacket.Style.KLASSISK;
                } else { //if (selectedStyle.equals(SkiGuidePacket.Style.FRISTIL.name())){
                    style = SkiRequestPacket.Style.FRISTIL;
                }
            } else {
                style = null;
            }
        }




        return new SkiRequestPacket(age, style, Integer.parseInt(lengthTextField.getText()));

    }

    /**
     * Sends request packet to server
     * @param packet packet to send.
     */
    private void sendRequest(SkiRequestPacket packet){
        try {
            clientSocket = new Socket("127.0.0.1", 54555);
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(packet);
            receivePacket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Catches expected packet from server
     */
    private void receivePacket(){
        try {
            ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            SkiRecommendationPacket result = (SkiRecommendationPacket) inFromServer.readObject();
            if(result.getSkiLengthTo() == 0) { //If recommended length is NOT an interval
                lengthLabel.setText(result.getSkiLengthFrom() + " cm");
            } else {
                lengthLabel.setText(result.getSkiLengthFrom() + " - " + result.getSkiLengthTo() + " cm");
            }
            recommendLabel.setVisible(true);
            lengthLabel.setVisible(true);

            if(result.getWarning() != null){ //If there is a warning
                warningLabel.setText(result.getWarning());
                warningLabel.setVisible(true);
            } else {
                warningLabel.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
