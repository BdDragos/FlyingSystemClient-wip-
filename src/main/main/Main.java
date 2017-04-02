package main.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by Dragos on 3/29/2017.
 */
public class Main extends Application
{
    private FXMLLoader loader;
    private FXMLLoader loader2;
    private Stage primaryStage;
    private AnchorPane rootLayout1;
    private TabPane rootLayout2;
    private SSLSocketFactory socketFactory;
    private SSLSocket socket;
    private static void execute(String sql)
    {

    }

    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage primaryStage) throws ClassNotFoundException, SQLException
    {
        try {

            socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket("localhost", 7070);
            String[] cipherSuites = socketFactory.getSupportedCipherSuites();
            socket.setEnabledCipherSuites(cipherSuites);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        this.primaryStage = primaryStage;
        loader = new FXMLLoader();
        loader2 = new FXMLLoader();
        LoginView();
    }

    public void authenticated()
    {
        MainView();
    }

    private void LoginView() throws ClassNotFoundException, SQLException
    {
        try
        {
            String pathToFxml = "src/main/resources/LoginWindow.fxml";
            URL fxmlUrl = new File(pathToFxml).toURI().toURL();
            loader.setLocation(fxmlUrl);
            LoginClient controlLogin = new LoginClient(socket,socketFactory);
            loader.setController(controlLogin);
            rootLayout1 = loader.load();
            Scene scene = new Scene(rootLayout1);
            primaryStage.setScene(scene);
            primaryStage.show();

            controlLogin.initManager(this);

        }

        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void MainView()
    {
        try
        {
            String pathToFxml = "src/main/resources/MainWindow.fxml";
            URL fxmlUrl = new File(pathToFxml).toURI().toURL();
            loader2.setLocation(fxmlUrl);
            MainClient controlMain = new MainClient(socket,socketFactory);
            loader2.setController(controlMain);
            rootLayout2 = loader2.load();
            Scene scene = new Scene(rootLayout2);
            primaryStage.setScene(scene);
            primaryStage.show();

        }

        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
