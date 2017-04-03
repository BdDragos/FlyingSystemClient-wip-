package main.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/*
 * Created by Dragos on 3/29/2017.
 */
public class LoginClient implements Initializable {

    @FXML private TextField user;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    private SSLSocketFactory socketFactory;
    private SSLSocket socket;

    public LoginClient(SSLSocket socket,SSLSocketFactory socketFactory)
    {
        this.socket=socket;
        this.socketFactory=socketFactory;
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        passwordField.setPromptText("Your password");
        user.setPromptText("Your username");
    }

    public void initManager(final Main loginManager)
    {
        loginButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter output   = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                        String userName = user.getText();
                        String password = passwordField.getText();
                        if (userName == null) {
                            showErrorMessage("Dati un username");
                        } else if (password == null) {
                            showErrorMessage("Dati o parola");
                        } else {
                            output.println("Login");
                            output.println(userName);
                            output.println(password);
                            String response = input.readLine();
                            if (response != null && response.compareTo("Invalid") == 0) {
                                showErrorMessage("Username sau parola invalida");
                                initManager(loginManager);
                            } else {
                                loginManager.authenticated();
                            }
                        }

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
            }
        });
    }

    private static void showMessage(Alert.AlertType type)
    {
        Alert message=new Alert(type);
        message.setHeaderText("Succes");
        message.setContentText("Succes");
        message.showAndWait();
    }

    private static void showErrorMessage(String text)
    {
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }
}
