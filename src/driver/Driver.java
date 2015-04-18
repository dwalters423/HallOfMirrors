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
        
        ArrayList<Point2D> MirrorBounces = new ArrayList ();
        double x, y, m;            //X and Y coordinates are used to calculate b (slope)
        double b = 0;              //The first Y intercept is at 0.
        boolean backwards = false; //The first movement is forwards (see lines 54-62)
        double initialLength;      //This is the initial length of the ray.
        double rayLength = 0;      //The ray magnitude is finite.
        
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
            
            /*Get the ray length*/
            initialLength = getDistance(lightRay);
            
            while (rayLength < initialLength){
                
                /*Calculate the light slope using (y2 - y1) / (x2 - x1) equation.*/
                m = ((lightRay[1].getY() - lightRay[0].getY()) / (lightRay[1].getX() - lightRay[0].getX()));
                
                 /*The test for boolean backwards determines if the ray of light will
                  *move forward (+1) or backwards (-1) on the X axis.*/
                if (!backwards) {
                    x = (lightRay[0].getX() + 1); 
                }
                else {
                    x = (lightRay[0].getX() - 1);
                }
                /*Solve for y: y = mx + b*/
                y = (m*x) + b;
                /*Solve for new y intercept*/
                b = y - (m*x);
                
                //System.out.println(lightRay[0].getX() + ", " + lightRay[0].getY() + "     /// b = "+ b);
                
                /*Set the new location of the ray point to check*/
                lightRay[0].setLocation(x,y);
                
                /*Checks if light is colliding with any mirror in the list*/
                for (Line2D mirrorList1 : mirrorList) {
                    
                    /*Collision with a mirror, will collide with the mirror closest*/
                    if (isBetween (lightRay[0], mirrorList1)) {
                        
                        MirrorBounces.add(lightRay[0]);     //Add it to the collection of mirror collisions.
                        rayLength += getDistance(lightRay); //Keep track of the magnitude of the light ray by adding the current segment.
                        
                        break; //Stop the current for loop iteration, as no more checks need to be performed.
                    } //end if.
                } //end for loop
            } //end while
            
            
        }
        catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException e){
            e.printStackTrace();
        }
    }
    
    /*Reads input from the mirrorgroup element in MirrorInput.xml and returns an ArrayList of line segments (mirrors)*/
    private ArrayList<Line2D> populateMirrors(Document XML){
        
        int index;
        double startPointX, startPointY;
        double endPointX, endPointY;
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
                
                MirrorList.add(new Line2D.Double(startPointX, startPointY, endPointX, endPointY)); //Add mirror to mirrorlist.
                                
                System.out.print ("Start: (" + startPointX + "," + startPointY + ")"); //TESTING PURPOSES
                System.out.println ("End: (" + endPointX + "," + endPointY + ")");     //TESTING PURPOSES.
            } //end for loop
            
            return MirrorList;
        
    } //end populateMirrors
    
    /*Reads input from the lightgroup element in MirrorInput.xml and returns an array of Point2Ds representing
     *the light's current position and theoretical finite end point. 
    */
    private Point2D[] getInitialLights (Document XML) {
        
        int index;
        double startPointX, startPointY;
        double endPointX, endPointY;
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
        
        Points[0] = new Point2D.Double(startPointX, startPointY);

        index = endString.indexOf(","); //For parsing of string.
        endPointX = Float.parseFloat(endString.substring(0,index));  //Create integer for x value.
        endPointY = Float.parseFloat(endString.substring(index+1, endString.length())); //Create integer for y value.
        
        Points[1] = new Point2D.Double(endPointX, endPointY);

        System.out.print ("Start: (" + startPointX + "," + startPointY + ")"); //TESTING PURPOSES
        System.out.println ("End: (" + endPointX + "," + endPointY + ")");     //TESTING PURPOSES.
        
        return Points;
    } //end getLight
    
    private double getDistance(Point2D[] Ray) {
        
        /*Initialize variables for readability*/
        /*Based on distance formula sqrt((x2 - x1)^2 + (y2 - y1)^2)) */
        
        double x1 = Ray[0].getX();
        double y1 = Ray[0].getY();
        double x2 = Ray[1].getX();
        double y2 = Ray[1].getY();
        
        double xEquation;
        double yEquation;
        
        xEquation = Math.pow((x2 - x1),2);
        yEquation = Math.pow((y2 - y1),2);
        
        return Math.sqrt(xEquation + yEquation);
    }
    
    /*Function checks to see if a point (light in this context) falls on a line segment (mirror)*/
    private boolean isBetween (Point2D point, Line2D line){
        
        /*Assign variables for readability where Z variables are the point and
         * x1,y1,x2,and y2 are the two points that make up the line segment.*/
        double x1 = line.getX1();
        double y1 = line.getY1();
        double x2 = line.getX2();
        double y2 = line.getY2();
        double xZ = point.getX();
        double yZ = point.getY();
        
        /*Get cross product of x and y values.*/
        double xEquation = (xZ - x1) / (x2 - x1);
        double yEquation = (yZ - y1) / (y2 - y1);
        
        /*Check to see if the cross product of both equations is within a 0.05 tolerance.*/
        /*Tolerance is necessary because the light travels exactly 1 or -1 space on the 
         *x axis. Tolerance could be adjusted if the x increment was less than 1.*/
        if(Math.abs(xEquation - yEquation) <= 0.05) {
            
            /*Checks to see if values fall within the line segment (mirror).
             *Since the x and y values are colinear, you can test only 1 set of
             *variables (x or y). 
            */
            if (y2 > y1){
                return (y1 < yZ) && (yZ < y2);
            }
            else {
                return (y2 < yZ) && (yZ < y1);
            }
        }
        return false;
    }
    
    private double angleOfReflection(double slope, Line2D mirror){
        double angleOfIncidence;
        double angleOfReflection;
        
        
        
        return angleOfReflection;
    }
    
} //end of Driver class.
