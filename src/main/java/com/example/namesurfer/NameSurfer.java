package com.example.namesurfer;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NameSurfer extends Application implements NameSurferConstants{
    // We gebruiken een Set omdat er geen dubbele items mogen
    // voorkomen, de volgorde speelt geen rol.
    private Set<NameSurferEntry> keuzeLijst = new HashSet<>();
    //int final static MAX_KEUZES = 5;
    private double[] intersectionsXas = new double [15];
    private double[] intersectionsYas = new double[15];
    private final Color[] colors ={Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.DEEPPINK};
    private NameSurferDataBase db;
    private Scene scene;
    private VBox vbox;
    private BorderPane root;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField txt;
    private TextArea txtAreaInfo;
    private Button graph, clear;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage)  {
        db = new NameSurferDataBase();

        root = new BorderPane();

        scene = new Scene(root, APPLICATION_WIDTH, APPLICATION_HEIGHT);
        centeredStage(stage, "Name Surfer");
        addChangeListenersToScene();

        makeHeader();
        makeRightSide();
        makeCenterPlace();

        stage.setScene(scene);
        stage.show();
    }

    private void makeCenterPlace() {
        canvas = new Canvas(APPLICATION_WIDTH -120, APPLICATION_HEIGHT);

        gc = canvas.getGraphicsContext2D();

        createDefaultGrid();
        root.setCenter(canvas);


    }

    private void createDefaultGrid() {

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight() -40;// ondergrens op -40
        //double beginXas = 0.0;
        double beginYas = 10.0;

        double deltaHAs = getDeltaXAs();

        double deltaVas = canvasHeight - 2 * GRAPH_MARGIN_SIZE;

        //int verbeteringIndeling = (int) (canvasWidth - (int) (deltaHAs * NDECADES));

        //beginXas = GRAPH_MARGIN_SIZE + (double) verbeteringIndeling/2;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Ctl canvas beschikbare grootte

        // aanmaak horizontale lijnen
        gc.strokeLine(GRAPH_MARGIN_SIZE,20,canvasWidth - GRAPH_MARGIN_SIZE,20);
        gc.strokeLine(GRAPH_MARGIN_SIZE, canvasHeight-40, canvasWidth-GRAPH_MARGIN_SIZE, canvasHeight -40);

        // aanmaak van 11 vertikale lijnen en notatie CoordX ervan
        for (int i=0; i< NDECADES-1  ;i++){
            //berekening snijpunt x-as van de y line
            // start X coord is marge
            // de volgende x + sprong groote naar volgende Y as
            double x = GRAPH_MARGIN_SIZE + i*deltaHAs;
            // het tekenen van de vertikale as
            gc.strokeLine(x, beginYas,x,beginYas + deltaVas  );
            // noteren van deze snijpunten met de X-as in een array
            // deze snijpunten worden later gebruikt om de grafiek
            // te tekenen
            intersectionsXas[i] = x;

        }

        // plaatsing decades text 1900, 1910, ...
        double beginX = intersectionsXas[0] +1;
        setJaartallenLegendaXas(beginX, deltaHAs,canvasHeight);

    }

    private double getDeltaXAs() {
        double canvasWidth = canvas.getWidth();
        return (canvasWidth - (double) GRAPH_MARGIN_SIZE)/(NDECADES-1);
    }

    private void designNamesGraph(){
        int teller = 0;
        if(keuzeLijst==null || keuzeLijst.isEmpty()) {return;}
        for (NameSurferEntry entr:keuzeLijst) {
            Color color = colors[teller];
            designNameGraph(entr.getName(),color, entr.getIntegerArray());
            teller ++;
        }
    }
    private void designNameGraph(String name, Color color, ArrayList<Integer> list){

        int teller= 0;
        double rangeYWindow = gc.getCanvas().getHeight() - GRAPH_MARGIN_SIZE - 80; // tekstruimte legende = 40
        double scale = rangeYWindow/MAX_RANK;

        gc.setStroke(color);
        //System.out.println("snijpunten y-as voor " + name);
        for(Integer i:list){
            double y = (double) i;

            if(y> MAX_RANK){ y= MAX_RANK;}// om binnen de gestelde range te houden
            y = y * scale;
            y = y + GRAPH_MARGIN_SIZE; //marge boven er terug bij
            intersectionsYas[teller]= y ;

            // plaatsen tekst naam en aantal bij graph
            int ii = (int) i;
            String textStr = name + " " ;
            //indien rank = 1000 dan * weergeven else rangwaarde weergeven
            if(ii == 1000) {
                textStr += "*";
            }else{
                textStr += "" + ii;
            }
            gc.strokeText(textStr, intersectionsXas[teller], intersectionsYas[teller]);
            teller++;
        }

        drawPolyLine();

    }

    private void drawPolyLine() {
        for(int i=0 ; i < NDECADES -2 ; i++){
            gc.strokeLine(intersectionsXas[i],intersectionsYas[i],intersectionsXas[i+1],intersectionsYas[i+1]);
        }
    }

    private void setJaartallenLegendaXas(double beginX, double deltaHAs,double canvasHeight) {
        gc.setLineWidth(1);

        for (int i=0; i< NDECADES -1; i++ ){
            String str = "" + ((DECADE* i) + START_DECADE);
            double x = beginX +  i * deltaHAs;
            double y = canvasHeight-29;//-15
            gc.strokeText(str,x,y);
        }
    }

    private void makeRightSide() {
        vbox = new VBox();

        vbox.setMinWidth(120);
        vbox.setMaxWidth(120);
        //vbox.setPrefWidth(120);
        vbox.setBackground(new Background( new BackgroundFill(Color.LIGHTGRAY, null, null)));
        //setStyle("-fx-background-color: lightgray");
        vbox.setPadding(new Insets(6,6,6,6));
        Label vLbl = new Label("Info");
        vLbl.setPadding(new Insets(6,6,6,6));

        txtAreaInfo = new TextArea();
        txtAreaInfo.setPrefRowCount(8);
        txtAreaInfo.setText("Database telt " + db.size() + " namen ." );

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(vLbl);

        vbox.getChildren().addAll(hb,txtAreaInfo);
        root.setRight(vbox);

    }

    private void makeHeader() {
        HBox hbox = new HBox();

        Label lbl = new Label("Name:");
        lbl.setAlignment(Pos.BASELINE_RIGHT);

        txt = new TextField();
        txt.setPrefWidth(200);//100
        graph = new Button("graph");
        clear = new Button("clear graph");

        hbox.getChildren().addAll(lbl,txt,graph, clear);
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.setBackground(new Background( new BackgroundFill(Color.LIGHTGRAY, null, null)));
        hbox.setPadding(new Insets(6,6,6,6));
        root.setTop(hbox);
        setListenersForInteractorsHeader();

    }

    private void setListenersForInteractorsHeader() {
        txt.setOnAction(e->{
            String keuze = txt.getText().toLowerCase().trim();
            beheerKeuzeSet(keuze);


        });
        graph.setOnAction(e ->{
            //NameSurferEntry entr = keuzelijst.
            designNamesGraph();
        });
        clear.setOnAction(e->{
            clearGraphPanel();
        });
    }

    /**
     * @ addChangeListenersToScene
     * Add changeListeners at the width and Height properties
     * of the scene object
     */
    private void addChangeListenersToScene(){
        scene.widthProperty().addListener(this::processScreenResize);
        scene.heightProperty().addListener(this::processScreenResize);
    }

    /**
     * @ processScreenResize()
     * @param observable
     * Excecute the resize of the canvas and right panel
     */
    private void processScreenResize(Observable observable){
        /*(ObservableValue<? extends Number> observable, Object oldValue, Object newValue) {
        * omdat de oldValue en newValue niet nodig zijn, hebben we enkel het observable object nodig
        */
        double scWidth = scene.getWidth();
        double newCanvasWidth = 0.0;
        double newCanvasHeigth = 0.0;
        Set<NameSurferEntry> oldKeuzelijst = keuzeLijst;

        newCanvasHeigth = scene.getHeight();
        if(scene.getWidth()< APPLICATION_WIDTH -120){
            vbox.setVisible(false);
            newCanvasWidth = scWidth;
        }else{
            vbox.setVisible(true);
            newCanvasWidth = scWidth -120;
        }

        canvas.setWidth(newCanvasWidth);

        canvas.setHeight(newCanvasHeigth);

        clearGraphPanel();
        keuzeLijst = oldKeuzelijst;
        showKeuzeLijst();

        designNamesGraph();
    }

    private void clearGraphPanel() {

        keuzeLijst = new HashSet<>();
        showKeuzeLijst();// is een visuele controle
        txt.setVisible(true);
        // nu nog de grafiek
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        createDefaultGrid();
        gc.setStroke(Color.BLACK);
    }

    /**
     * De namen worden opgezocht in de database db
     * en indien gevonden opgeslagen in een Set die enkel
     * unieke waarden kan bevatten. Door de opgeslagen naam
     * in de entry te nemen, kan de set enkel deze unieke
     * namen opnemen. De set keuzeLijst is niet geordend!
     * @param naam die opgezocht wordt in de database
     */
    private void beheerKeuzeSet(String naam) {
        int old;//,current;
        NameSurferEntry entr = null;

        if(db.dbContains(naam)) entr = db.getEntry(naam);
        old = keuzeLijst.size();
        // mag maar maximaal 5 namen bevatten
        if(entr != null && old < 5) {
            keuzeLijst.add(entr);
            txt.setText("");
            showKeuzeLijst();
        }
        // Indien er vijf naam keuzen werden gemaakt dan
        // maakt de invoer van een zesde naam onmogelijk.
        if(keuzeLijst.size()>4){
            txt.setVisible(false);
        }
    }

    /**
     * Dit is een visuele controle om de gemaakte keuzen
     * zichtbaar te maken door weergave ervan in de
     * rechtse info zuil
     */
    private void showKeuzeLijst() {
        StringBuilder str = new StringBuilder();
        str.append("keuzelijst\n");

        // ctl of keuzelijst leeg is of null
        if(keuzeLijst == null || keuzeLijst.isEmpty()){
            txtAreaInfo.setText(str.toString());//
            return;
        }

        // voeg gemaakte naam keuzes toe
        for(NameSurferEntry ent: keuzeLijst){
            str.append(ent.getName()).append("\n");
        }

        // toon keuzelijst
        String tkst = str.toString();
        txtAreaInfo.setText(tkst);
    }

    /**
     * Het centreren van de stage op het scherm zie handboek
     * Learn JavaFX 8 blz135 (aangepast)
     * @param stage als het window waarin de scene wordt geplaatst
     * @param title de aftiteling van het window
     */
    private void centeredStage(Stage stage, String title) {
        //stage.centerOnScreen();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - APPLICATION_WIDTH -120)/2;
        double y = bounds.getMinY() +(bounds.getHeight() - APPLICATION_HEIGHT)/2;
        stage.setX(x);
        stage.setY(y);

        stage.setTitle(title);

    }

}
