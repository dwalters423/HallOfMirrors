/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author Dave
 */
public class Driver {
    private final String filePath = "src/input/MirrorInput.xml";
    
    public Driver ()  {
        double lightDirection;
        double x, y;
        
        try {
            
            /*Create the XML document for parsing*/
            File XMLInput = new File (filePath);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document XML = docBuilder.parse(XMLInput);
            
            /*Create an ArrayList of mirrors by parsing the document.*/
            ArrayList<Line2D> mirrorList = populateMirrors(XML);
            
            /*Create the light ray points. Position 0 is the current position,
             * position 1 is the theoretical end point to be used for slope
             * and angle calculation.*/           
            Point2D [] lightRay = getInitialLights(XML);
            
            while (true){

                /*Calculate the light slope using (y2 - y1) / (x2 - x1) equation.*/
                lightDirection = (lightRay[1].getY() - lightRay[0].getY()) / (lightRay[1].getX() - lightRay[0].getX());
                
                x = lightRay[0].getX() + 1; //OR MINUS 1!!! TO DO
                y = y - lightDirection(x);
                

                lightRay[0].setLocation(lightRay[0].getX(), lightDirection);

                for (int i = 0; i < mirrorList.size(); i++){
                    if (mirrorList.get(i).contains(lightRay[0])){
                        System.out.println ("Light ray collided with mirror at point "
                         + lightRay[0].getX() + ", " + lightRay[0].getY()); //Output the point of intersection.
                        //Calculate angle incidence
                        //Set Lights[1].X and Lights[1].Y to arbitray points in distance along slope.
                        break; //Stop the current for loop iteration, as no more checks need to be performed.
                    } //end if.
                } //end for loop



            } //end while
            
            
        }
        catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException e){
            e.printStackTrace();
        }
    }
    
    private ArrayList<Line2D> populateMirrors(Document XML){
        
        int index;
        float startPointX, startPointY;
        float endPointX, endPointY;
        String startString;
        String endString;
        
        ArrayList<Line2D> MirrorList = new ArrayList();
        
            /*Create a list of mirror elements.*/
            NodeList mirrorNodeList = XML.getElementsByTagName("mirror");
            
            /*Parse through mirror elements and populate array list with Mirrors.*/
            for (int i = 0; i < mirrorNodeList.getLength(); i++){
                Element mirrorE = (Element) mirrorNodeList.item(i); //Grab the mirror element at position i.
                
                startString = mirrorE.getElementsByTagName("start").item(0).getTextContent(); //Grab a string with start points.
                endString = mirrorE.getElementsByTagName("end").item(0).getTextContent();     //Grab a string with end points.
                
                index = startString.indexOf(","); //For parsing of string.
                startPointX = Float.parseFloat(startString.substring(0,index));  //Create integer for x value.
                startPointY = Float.parseFloat(startString.substring(index+1, startString.length())); //Create integer for y value.
                
                index = endString.indexOf(","); //For parsing of string.
                endPointX = Float.parseFloat(endString.substring(0,index)); //Create integer for x value.
                endPointY = Float.parseFloat(endString.substring(index+1, endString.length())); //Create integer for y value.
                
                MirrorList.add(new Line2D.Float(startPointX, startPointY, endPointX, endPointY)); //Add mirror to mirrorlist.
                                
                System.out.print ("Start: (" + startPointX + "," + startPointY + ")"); //TESTING PURPOSES
                System.out.println ("End: (" + endPointX + "," + endPointY + ")");     //TESTING PURPOSES.
            } //end for loop
            
            return MirrorList;
        
    } //end populateMirrors
    
    private Point2D[] getInitialLights (Document XML) {
        
        int index;
        float startPointX, startPointY;
        float endPointX, endPointY;
        Point2D[] Points = new Point2D[2];
        String startString;
        String endString;

        /*Create light ray object from light element.*/
        Element lightE = (Element)XML.getElementsByTagName("light").item(0);

        startString = lightE.getElementsByTagName("start").item(0).getTextContent();
        endString = lightE.getElementsByTagName("end").item(0).getTextContent();

        index = startString.indexOf(","); //For parsing of string.
        startPointX = Float.parseFloat(startString.substring(0,index)); //Create integer for x value.
        startPointY = Float.parseFloat(startString.substring(index+1, startString.length())); //Create integer for y value.
        
        Points[0] = new Point2D.Float(startPointX, startPointY);

        index = endString.indexOf(","); //For parsing of string.
        endPointX = Float.parseFloat(endString.substring(0,index));  //Create integer for x value.
        endPointY = Float.parseFloat(endString.substring(index+1, endString.length())); //Create integer for y value.
        
        Points[1] = new Point2D.Float(endPointX, endPointY);

        System.out.print ("Start: (" + startPointX + "," + startPointY + ")"); //TESTING PURPOSES
        System.out.println ("End: (" + endPointX + "," + endPointY + ")");     //TESTING PURPOSES.
        
        return Points;
    } //end getLight
    
} //end of Driver class.
