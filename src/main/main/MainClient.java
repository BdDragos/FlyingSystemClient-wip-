package main.main;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import main.model.Flight;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
/*
 * Created by Dragos on 3/30/2017.
 */
public class MainClient implements Initializable
{
    @FXML private TableView<Flight> mainTable;
    @FXML private Button logoutButton;
    @FXML private Pagination mainPagination;
    @FXML private Button buyButton;
    @FXML private TableView<Flight> buyTable;
    @FXML private TextField addClient;
    @FXML private TextArea addAdress;
    @FXML private TextField addTickets;
    @FXML private TextField searchDestination;
    @FXML private TextField searchDeparture;
    @FXML private Button searchButton;
    @FXML private TableView<Flight> searchTable;
    @FXML private TableColumn<Flight, String> destination;
    @FXML private TableColumn<Flight, Date> datehour;
    @FXML private TableColumn<Flight, String> airport;
    @FXML private TableColumn<Flight, Integer> freeseats;

    @FXML private TableColumn<Flight, String> destination2;
    @FXML private TableColumn<Flight, Date> datehour2;
    @FXML private TableColumn<Flight, String> airport2;
    @FXML private TableColumn<Flight, Integer> freeseats2;

    @FXML private TableColumn<Flight, String> destination3;
    @FXML private TableColumn<Flight, Date> datehour3;
    @FXML private TableColumn<Flight, Integer> freeseats3;
    @FXML private TableColumn<Flight, String> airport3;

    private ObservableList<Flight> zboruri;
    private final static int rowsPerPage = 7;
    private List<Flight> lista = new ArrayList<>();
    private BufferedReader in;
    private PrintWriter out;
    private SSLSocketFactory socketFactory;
    private SSLSocket socket;

    public MainClient(SSLSocket socket,SSLSocketFactory socketFactory)
    {
        this.socket=socket;
        this.socketFactory=socketFactory;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            out.println("Show");
            String retur = in.readLine();
            int lgt;
            String[] tokens = retur.split("/");
            lgt = tokens.length;
            for (int i = 1; i < lgt; i++)
            {
                String[] zboruri = tokens[i].split(",");
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");

                java.util.Date date = formatter.parse(zboruri[4]);
                java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());

                int freeseat = Integer.parseInt(zboruri[3]);
                Flight zbor = new Flight(Integer.parseInt(zboruri[0]), zboruri[1], zboruri[2], freeseat, sqlStartDate);
                lista.add(zbor);
            }

            this.zboruri = FXCollections.observableArrayList(lista);

            destination.setCellValueFactory(new PropertyValueFactory<Flight, String>("destination"));
            datehour.setCellValueFactory(new PropertyValueFactory<Flight, Date>("datehour"));
            airport.setCellValueFactory(new PropertyValueFactory<Flight, String>("airport"));
            freeseats.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("freeseats"));
            mainTable.getItems().setAll(zboruri);

            destination3.setCellValueFactory(new PropertyValueFactory<Flight, String>("destination"));
            airport3.setCellValueFactory(new PropertyValueFactory<Flight, String>("airport"));
            datehour3.setCellValueFactory(new PropertyValueFactory<Flight, Date>("datehour"));
            freeseats3.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("freeseats"));

            destination2.setCellValueFactory(new PropertyValueFactory<Flight, String>("destination"));
            airport2.setCellValueFactory(new PropertyValueFactory<Flight, String>("airport"));
            datehour2.setCellValueFactory(new PropertyValueFactory<Flight, Date>("datehour"));
            freeseats2.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("freeseats"));

            mainPagination.setPageFactory(this::createPage);


        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
    }

    private Node createPage(int pageIndex)
    {

        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, zboruri.size());
        mainTable.setItems(FXCollections.observableArrayList(zboruri.subList(fromIndex, toIndex)));

        int numOfPages = 1;
        if (zboruri.size() % rowsPerPage == 0) {
            numOfPages = zboruri.size() / rowsPerPage;
        } else if (zboruri.size() > rowsPerPage) {
            numOfPages = zboruri.size() / rowsPerPage + 1;
        }

        if (zboruri.size() == 0)
            numOfPages =1;

        mainPagination.setPageCount(numOfPages);

        return new BorderPane(mainTable);
    }

    @FXML
    public void setLogoutAction()
    {
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                out.println("END");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    @FXML
    public void setSearchAction()
    {
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                String depart = searchDeparture.getText();
                String destin = searchDestination.getText();
                if (depart.isEmpty() && destin.isEmpty())
                {
                    showErrorMessage("Complete at least one of the search fields");
                }
                else if (!depart.isEmpty() && !destin.isEmpty())
                {
                    try
                    {
                        out.println("SearchAll");
                        out.println(depart);
                        out.println(destin);
                        String result = in.readLine();
                        List<Flight> fin = new ArrayList<>();
                        int lgt;
                        String[] tokens = result.split("/");
                        lgt = tokens.length;
                        for (int i = 1; i < lgt; i++)
                        {
                            String[] zboruri = tokens[i].split(",");
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date date = formatter.parse(zboruri[4]);
                            java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
                            int freeseat = Integer.parseInt(zboruri[3]);
                            Flight zbor = new Flight(Integer.parseInt(zboruri[0]), zboruri[1], zboruri[2], freeseat, sqlStartDate);
                            fin.add(zbor);


                            searchTable.getItems().setAll(fin);


                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (!depart.isEmpty() && destin.isEmpty())
                {
                    try
                    {
                        out.println("SearchDep");
                        out.println(depart);
                        String result = in.readLine();
                        List<Flight> fin = new ArrayList<>();
                        int lgt;
                        String[] tokens = result.split("/");
                        lgt = tokens.length;
                        for (int i = 1; i < lgt; i++)
                        {
                            String[] zboruri = tokens[i].split(",");
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date date = formatter.parse(zboruri[4]);
                            java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
                            int freeseat = Integer.parseInt(zboruri[3]);
                            Flight zbor = new Flight(Integer.parseInt(zboruri[0]), zboruri[1], zboruri[2], freeseat, sqlStartDate);
                            fin.add(zbor);

                            searchTable.getItems().setAll(fin);
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (depart.isEmpty() && !destin.isEmpty())
                {
                    try
                    {
                        out.println("SearchDest");
                        out.println(destin);
                        String result = in.readLine();
                        List<Flight> fin = new ArrayList<>();
                        int lgt;
                        String[] tokens = result.split("/");
                        lgt = tokens.length;
                        for (int i = 1; i < lgt; i++)
                        {
                            String[] zboruri = tokens[i].split(",");
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date date = formatter.parse(zboruri[4]);
                            java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
                            int freeseat = Integer.parseInt(zboruri[3]);
                            Flight zbor = new Flight(Integer.parseInt(zboruri[0]), zboruri[1], zboruri[2], freeseat, sqlStartDate);
                            fin.add(zbor);


                            searchTable.getItems().setAll(fin);


                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    @FXML
    public void setBuyAction()
    {

    }
    private static void showMessage(Alert.AlertType type, String header, String text)
    {
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
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
