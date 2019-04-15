//Dawson Murphy
//This is a Catch Rate Calculator for Pokemon, specifically for Pokemon Ultra Sun and Pokemon Ultra Moon.
//This calculator utilizes a text file as a database, which contains the information about a pokemon's 
//name, type('s), HP, speed, weight, and catch rate. The program opens a window that prompts the user 
//to input information about the pokemon they want to calculate the catch rate of. The formula used
//to calculate the catch rate is based upon the formula used in the games, and was retrieved from Bulbapedia.
//The formula the games use creates a modified catch rate based upon the pokemon's HP at full health, the pokemon's
//HP at the time of attempted capture, the pokemon's base catch rate, a bonus provided by the type of pokeball used,
//a bonus provided by the status condition affecting the pokemon, and a Capture Power Bonus (If activated).
//It then uses the modified catch rate to calculate a number for the shake check. The game then performs
//four shake checks, each time randomly generating a number between 0 and 65535. If at any point 
//the random number generated is greater than or equal to the shake check number, the capture fails
//and no more checks are performed. If all four random numbers are less than the shake check number,
//the capture succeeds. For this program, the modified catch rate and shack check number are calculated
//in the same way as the game. The program then finds the probability that four randomly generated numbers
//between 0 and 65535 are less than the shake check number. 
//Due to the pokemon's HP IV being unknown to the player until after the pokemon's capture, this program
//creates two probabilities for the catch rate, using an IV of 31 for the minimum calculations and an IV of 0
//for the maximum calculations.

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//Creates the class pokemon. Initially set as empty, the data is filled when
//a pokemon is selected. 
class pokemon{
	String type1,type2,name;
	double weight,catchRate,hp;
	int speed;
}

public class CatchRateCalculator {
	
	public static void main(String[] args) throws java.lang.InterruptedException, FileNotFoundException{
		new frame();//Initiates the catch rate window
	}
}

class tops{
	//Creates a new TreeMap to hold the data for all pokemon. 
	Map<String,pokemon>data2=new TreeMap<String,pokemon>();
	//Creates a Double array to hold the data needed for the calculations
	//pkminfo[0]= Pokemon's Catch Rate
	//pkminfo[1]= Pokemon's base HP
	//pkminfo[2]= Pokemon's current HP
	//pkminfo[3]= Pokeball Modifier
	//pkminfo[4]= Status Condition Modifier
	//pkminfo[5]= Rotom Catch Modifier
	static Double[] pkminfo=new Double[6];
	double rate,bB,sB,level;
	static String sC,bC,pS,rC;
	//Creates the TreeMap for Status Conditions <Status, Modifier>
	Map<String, Double>status=new TreeMap<String,Double>();
	
	
	public tops()throws FileNotFoundException{
		//Adds the status conditions and their modifiers to status
		status.put("None", 1.0);
		status.put("Sleep",2.0);
		status.put("Frozen", 2.0);
		status.put("Poisoned", 1.5);
		status.put("Paralized", 1.5);
		status.put("Burned", 1.5);
		
		//Creates the scanner to read the text file where all the pokemon data is stored
		Scanner pInput = new Scanner(new File("AlltheData"));
		//While there is still more pokemon to read
		while(pInput.hasNextLine()){
			pokemon p=new pokemon(); //Creates new pokemon class
			p.name=pInput.next(); //Assigns first string on line as the pokemons name
			p.type1=pInput.next(); //Next String is the pokemon's type
			//Checks if the next item on the line is an integer. This is to check if the pokemon is dual typed or not.
			//If the next item is not an integer, then that is the pokemon's second type and must be included in the pokemon class
			if(!pInput.hasNextInt()){ 
				p.type2=pInput.next(); //Assigns the next String as the pokemons second type if it exists.
			}
			p.hp=pInput.nextDouble(); //Assigns next double as pokemon's hp
			p.speed=pInput.nextInt(); //Assigns next integer as pokemon's speed
			p.weight=pInput.nextDouble(); //Assigns next double as the pokemon's weight
			p.catchRate=pInput.nextDouble(); //Assigns next double as the pokemons catch rate
			data2.put(p.name, p); //Adds pokemon to TreeMap data2, with its names as its address
			pInput.nextLine(); //Continues to the next line if it exists
		}
		
	}
}
//Creates all the data for the window 
class frame extends tops{
	static final int W=900,H=400;//width and height of Frame
	private JFrame f; //first window extends 
	private JPanel p; //Sets interior of the window
	private JLabel lab0,lab1,lab2,lab3,lab4,lab5,night,turns,repCatch,lure,dive,love,levelB,rotoCatch; //For the Labels 
	private JComboBox<String> pkmSelect,statusBonus,ballBonus,nightYN,repCatchYN,lureYN,diveYN,YN,rotoCatchAns; //For the Drop Down Windows
	private JButton b; //For the button to run the calculation
	private JTextField levelW,hpNow,turnsR,levelR; //For the needed input text
	private String nAns,rAns,lureAns,diveAns,Ans,rotoAns; //For the Yes No questions
	private double levelP; //For the level of the pokemon
	
	
	public frame()throws FileNotFoundException{ //constructor
		
		
		f=new JFrame("Catch Rate Calculator");
		f.setSize(W,H); //Sets size of Frame from variables
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //for exiting
		
		p=new JPanel();
		p.setBackground(Color.ORANGE); //Sets background color of window
		p.setLayout(null);
		
		lab0 = new JLabel("Catch Rate Calculator"); //Prints the title on page
		lab0.setBounds(W/4,10,W-20,H/6); //positions title on page
		lab0.setFont(new Font("Serif", Font.PLAIN, 30)); //sets the font and size

		//Label for the Pokemon selection drop box
		lab1 = new JLabel("Pokemon?"); //Label for first input
		lab1.setBounds(100,30, 400,100); //positioning
		lab1.setFont(new Font("Serif", Font.PLAIN, 20)); //Sets the font and size
		lab1.setMinimumSize(new Dimension(50,50)); //Sets dimension of label box
		
		//Combo Box from which to select pokemon
		pkmSelect=new JComboBox<String>(); //combo box for pokemon
		pkmSelect.setBounds(200,70,200,27); //size of text field
		pkmSelect.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		Collection<String>pkmList=data2.keySet(); //Makes a list to display in the Combo Box from the data2 keys
		for(String stat:pkmList){ 
			pkmSelect.addItem(stat);}
		//Waits for a pokemon to be selected and then pulls that pokemon's name and saves it as pS
		pkmSelect.addActionListener(new ActionListener(){ //Creates the action listener for the drop box
			public void actionPerformed(ActionEvent action) {
					pS=((String)pkmSelect.getSelectedItem());	
				}
		});
		
		//Label for the level of the pokemon text field
		lab2 = new JLabel("Level?"); //Label for input
		lab2.setBounds(100,60, 400,100); //positioning
		lab2.setFont(new Font("Serif", Font.PLAIN, 20)); 
		lab2.setMinimumSize(new Dimension(50,50));
		
		//Text field for the level of the pokemon
		levelW=new JTextField(5); //text field for HP percentage left
		levelW.setBounds(160,105,50,20); //size of text field
		levelW.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		
		//Label for the HP percentage left (or 0 for 1 HP left)
		lab3 = new JLabel("Current HP%? 0 for exactly 1 HP left."); //Label for third input
		lab3.setBounds(100,90, 400,100); //positioning
		lab3.setFont(new Font("Serif", Font.PLAIN, 20)); 
		lab3.setMinimumSize(new Dimension(50,50));
		
		//Text Field for the HP percentage left (or 0 for 1 HP left)
		hpNow=new JTextField(5); //text field for hp percentage left
		hpNow.setBounds(405,130,50,20); //size of text field
		hpNow.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		
		//Label for Poke Ball Type Combo Box
		lab4 = new JLabel("Poke Ball Used?"); //Label for fourth input
		lab4.setBounds(100,120, 400,100); //positioning
		lab4.setFont(new Font("Serif", Font.PLAIN, 20)); 
		lab4.setMinimumSize(new Dimension(50,50));
		
		//Combo Box for the Poke Balls
		ballBonus=new JComboBox<String>(); //text field for ballBonus
		ballBonus.setBounds(240,160,150,25); //size of text field
		ballBonus.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		ballBonus.addItem("Poke Ball");
		ballBonus.addItem("Great Ball");
		ballBonus.addItem("Ultra Ball");
		ballBonus.addItem("Master Ball");
		ballBonus.addItem("Premier Ball");
		ballBonus.addItem("Luxury Ball");
		ballBonus.addItem("Friend Ball");
		ballBonus.addItem("Heal Ball");
		ballBonus.addItem("Moon Ball");
		ballBonus.addItem("Fast Ball");
		ballBonus.addItem("Dusk Ball");
		ballBonus.addItem("Net Ball");
		ballBonus.addItem("Nest Ball");
		ballBonus.addItem("Quick Ball");
		ballBonus.addItem("Timer Ball");
		ballBonus.addItem("Repeat Ball");
		ballBonus.addItem("Lure Ball");
		ballBonus.addItem("Dive Ball");
		ballBonus.addItem("Love Ball");
		ballBonus.addItem("Heavy Ball");
		ballBonus.addItem("Level Ball");
		ballBonus.addItem("Beast Ball");
		//Creates action listener for the Poke Balls. Some Balls require more data from the user.
		//When those Balls are selected the window will be repainted with the new questions added.
		//Code has also been added to remove questions if a different Ball is then selected and the 
		//questions are no longer relevant.
		ballBonus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
					bC=((String)ballBonus.getSelectedItem());
					//If the Dusk Ball is selected, adds the night question to the window
					if(bC.equals("Dusk Ball")){
						p.add(nightYN);
						p.add(night);
						f.validate();
						f.repaint();
					}
					//Removes Night question if Dusk Ball is deselected
					if(!bC.equals("Dusk Ball")){
						p.remove(nightYN);
						p.remove(night);
						f.validate(); 
						f.repaint();
					}
					//Adds lure question if Lure Ball is selected
					if(bC.equals("Lure Ball")){
						p.add(lure);
						p.add(lureYN);
						f.validate();
						f.repaint();
					}
					//Removes lure question if Lure Ball is deselected
					if(!bC.equals("Lure Ball")){
						p.remove(lure);
						p.remove(lureYN);
						f.validate(); 
						f.repaint();
					}
					//Adds dive question if Dive Ball is selected
					if(bC.equals("Dive Ball")){
						p.add(dive);
						p.add(diveYN);
						f.validate();
						f.repaint();
					}
					//Removes dive question if Dive Ball is deselected
					if(!bC.equals("Dive Ball")){
						p.remove(dive);
						p.remove(diveYN);
						f.validate(); 
						f.repaint();
					}
					//Adds repCatch question if Repeat Ball is selected
					if(bC.equals("Repeat Ball")){
						p.add(repCatchYN);
						p.add(repCatch);
						f.validate();
						f.repaint();
					}
					//Removes repCatch if Repeat Ball is deselected
					if(!bC.equals("Repeat Ball")){
						p.remove(repCatchYN);
						p.remove(repCatch);
						f.validate(); 
						f.repaint();
					}
					//Adds level question if Level Ball is selected
					if(bC.equals("Level Ball")){
						p.add(levelB);
						p.add(levelR);
						f.validate();
						f.repaint();
					}
					//Removes level question if Level Ball is deselected
					if(!bC.equals("Level Ball")){
						p.remove(levelB);
						p.remove(levelR);
						f.validate(); 
						f.repaint();
					}
					//Adds love question if Love Ball is selected
					if(bC.equals("Love Ball")){
						p.add(YN);
						p.add(love);
						f.validate();
						f.repaint();
					}
					//Removes the love question if Love Ball is deselected
					if(!bC.equals("Love Ball")){
						p.remove(YN);
						p.remove(love);
						f.validate(); 
						f.repaint();
					}
					//Adds the turns question if either Quick Ball or Timer Ball are selected
					if(bC.equals("Quick Ball")||bC.equals("Timer Ball")){
						p.add(turns);
						p.add(turnsR);
						f.validate();
						f.repaint();
					}
					//Removes turns question if Quick Ball or Timer Ball are not selected
					if(!bC.equals("Quick Ball")&&!bC.equals("Timer Ball")){
						p.remove(turns);
						p.remove(turnsR);
						f.validate();
						f.repaint();
					}
				}
		});
		
		//Label for the Status Conditions Drop Box
		lab5 = new JLabel("Status Condition?"); //Label for fifth input
		lab5.setBounds(100,150, 400,100); //positioning
		lab5.setFont(new Font("Serif", Font.PLAIN, 20)); 
		lab5.setMinimumSize(new Dimension(50,50));
		
		//Drop Box for the Status Conditions
		statusBonus=new JComboBox<String>(); //Drop Box for status bonus
		statusBonus.setBounds(250,190,100,25); //size of Drop Box
		statusBonus.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		Collection<String>statC=status.keySet(); //Creates the list of status conditions from the TreeMap status keys
		for(String stat:statC){
			statusBonus.addItem(stat);}
		statusBonus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
					sC=((String)statusBonus.getSelectedItem());	
				}
		});
		
		//Drop Box for whether RotoCatch has been activated.
		rotoCatch = new JLabel("RotoCatch Activated?");
		rotoCatch.setBounds(100,180,400,100);
		rotoCatch.setFont(new Font("Serif", Font.PLAIN, 20));
		rotoCatch.setMinimumSize(new Dimension(50,50));
		
		//Combo Box field for Night question
		rotoCatchAns=new JComboBox<String>(); //Drop Box for rotoCatch Question
		rotoCatchAns.setBounds(280,220,100,25); //size of text field
		rotoCatchAns.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		rotoCatchAns.addItem("YES");
		rotoCatchAns.addItem("NO");
		//Creates action listener for rotoCatch question, and saves answer as rotoAns
		rotoCatchAns.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == rotoCatchAns) {
					rotoAns=((String)rotoCatchAns.getSelectedItem());
				}}
		});
		
		//Label for Night question
		night= new JLabel("Is it dark out?"); //Label for fourth input
		night.setBounds(450,30, 400,100); //positioning
		night.setFont(new Font("Serif", Font.PLAIN, 20)); 
		night.setMinimumSize(new Dimension(50,50));
		
		//Combo Box field for Night question
		nightYN=new JComboBox<String>(); //Drop Box for Night Question
		nightYN.setBounds(565,70,100,25); //size of text field
		nightYN.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		nightYN.addItem("YES");
		nightYN.addItem("NO");
		//Creates action listener for Night question, and saves answer as nAns
		nightYN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == nightYN) {
					nAns=((String)nightYN.getSelectedItem());
				}}
		});
		
		//Question label for repeat ball
		repCatch= new JLabel("Previously Caught?"); //Label for input
		repCatch.setBounds(450,30, 400,100); //positioning
		repCatch.setFont(new Font("Serif", Font.PLAIN, 20)); 
		repCatch.setMinimumSize(new Dimension(50,50));
		
		//Drop Box for repeat ball question
		repCatchYN=new JComboBox<String>(); //Drop box for repeat ball
		repCatchYN.setBounds(565,100,100,25); //size of text field
		repCatchYN.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		repCatchYN.addItem("YES");
		repCatchYN.addItem("NO");
		//Creates action listener for Repeat question, saves answer as rAns
		repCatchYN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == repCatchYN) {
					rAns=((String)repCatchYN.getSelectedItem());
				}}
		});
		
		//label for fishing question
		lure= new JLabel("Are you fishing?"); //Label for input
		lure.setBounds(450,30, 400,100); //positioning
		lure.setFont(new Font("Serif", Font.PLAIN, 20)); 
		lure.setMinimumSize(new Dimension(50,50));
		
		//Drop Box for fishing question
		lureYN=new JComboBox<String>(); //Drop Box
		lureYN.setBounds(565,100,100,25); //size of text field
		lureYN.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		lureYN.addItem("YES");
		lureYN.addItem("NO");
		lureYN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == lureYN) {
					lureAns=((String)lureYN.getSelectedItem()); //Saves the answer as lureAns
				}}
		});
		
		//label for surfing/fishing question
		dive= new JLabel("Are you surfing or fishing?"); //Label for input
		dive.setBounds(450,30, 400,100); //positioning
		dive.setFont(new Font("Serif", Font.PLAIN, 20)); 
		dive.setMinimumSize(new Dimension(50,50));
		
		//Drop Box for surfing/fishing question
		diveYN=new JComboBox<String>(); //Drop Box
		diveYN.setBounds(565,100,100,25); //size of text field
		diveYN.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		diveYN.addItem("YES");
		diveYN.addItem("NO");
		diveYN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == diveYN) {
					diveAns=((String)diveYN.getSelectedItem()); //saves answer as diveAns
				}}
		});
		
		//Label for love question
		love= new JLabel("Pokemon the same species & opposite gender?"); //Label for input
		love.setBounds(450,30, 400,100); //positioning
		love.setFont(new Font("Serif", Font.PLAIN, 20)); 
		love.setMinimumSize(new Dimension(50,50));
		
		//Drop Box for love question
		YN=new JComboBox<String>(); //Drop Box
		YN.setBounds(565,100,100,25); //size of text field
		YN.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		YN.addItem("YES");
		YN.addItem("NO");
		YN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == YN) {
					Ans=((String)YN.getSelectedItem()); //saves love question as Ans
				}}
		});
		
		//Question for how many turns have passed
		turns= new JLabel("How many turns have passed?"); //Label for turns passed
		turns.setBounds(450,30, 400,100); //positioning
		turns.setFont(new Font("Serif", Font.PLAIN, 20)); 
		turns.setMinimumSize(new Dimension(50,50));
		
		//Text field for number of turns passed
		turnsR=new JTextField(5); //
		turnsR.setBounds(565,100,100,25); //size of text field
		turnsR.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		
		//Label for your pokemon's level question
		levelB= new JLabel("What level is your pokemon?"); //Label for fourth input
		levelB.setBounds(450,30, 400,100); //positioning
		levelB.setFont(new Font("Serif", Font.PLAIN, 20)); 
		levelB.setMinimumSize(new Dimension(50,50));
		
		//Text field for your pokemon's level
		levelR=new JTextField(5); //text field for hpMax
		levelR.setBounds(565,100,100,25); //size of text field
		levelR.setFont(new Font("Serif", Font.PLAIN, 20)); //font and size of text
		
		//The button used to initiate the calculations
		b=new JButton("Calculate");
		b.setBounds(100,270,100,30);
		b.setFont(new Font("Serif", Font.PLAIN, 18));
		b.addActionListener(new ActionListener(){ //When clicked, the calculations begin
			public void actionPerformed(ActionEvent action) {
				if (action.getSource() == b) {
					//In case user didn't touch the selection, these getSelectedItem's will
					//prevent the program from crashing
					sC=((String)statusBonus.getSelectedItem());	
					bC=((String)ballBonus.getSelectedItem());
					pS=((String)pkmSelect.getSelectedItem());	
					rotoAns=((String)rotoCatchAns.getSelectedItem());
					nAns=((String)nightYN.getSelectedItem());
					rAns=((String)repCatchYN.getSelectedItem());
					lureAns=((String)repCatchYN.getSelectedItem());
					diveAns=((String)diveYN.getSelectedItem());
					Ans=((String)YN.getSelectedItem());
					pkminfo[0]=data2.get(pS).catchRate; //Pulls the pokemon's catch rate from data2
					pkminfo[1]=data2.get(pS).hp; //Pulls the pokemon's base HP from data2
					if (!levelW.getText().equals("")) { //Saves the text version of the level as a double
						level=Double.parseDouble(levelW.getText());} 
					if(!levelR.getText().equals("")){ //Saves text version of your pokemon's level as a double
						levelP=Double.parseDouble(levelR.getText());
					}
					//Takes the hpNow percentage and calculates the pokemon's remaining HP using its base HP
					if (!hpNow.getText().equals("")) { 
						double hpP=Double.parseDouble(hpNow.getText());
						double hpN=(pkminfo[1])*(hpP/100);
						pkminfo[2]=hpN;
						if(hpP==0){ //If the entry was 0, the HP left is exactly 1
							pkminfo[2]=1.0;
						}
					} 
					//This piece of the program handles the Heavy Ball, which adds to the pokemon's catch rate instead of using
					//a multiplier. 
					if(bC.equals("Heavy Ball")&&!(pS.equals("Nihilego")||pS.equals("Pheromosa")||pS.equals("Buzzwole")||pS.equals("Xurkitree")||pS.equals("Celesteela")||pS.equals("Kartana")||pS.equals("Guzzlord")||pS.equals("Poipole")||pS.equals("Naganadel")||pS.equals("Stakataka")||pS.equals("Blacephalon"))){
						pkminfo[3]=1.0;
						if(data2.get(pS).weight<=220.2){
							pkminfo[0]-=20;
							if(pkminfo[0]<=0){ //prevents the catch rate from becoming less than 0
								pkminfo[0]=1.0;
							}
						}if(data2.get(pS).weight>=440.9 && data2.get(pS).weight<=661.2){
							pkminfo[0]+=20;
						}
						if(data2.get(pS).weight>=661.4){
							pkminfo[0]+=30;
						}
					}
					if (bC.equals("Poke Ball")) { 
						pkminfo[3]=1.0;} 
					if (bC.equals("Great Ball")) { 
						pkminfo[3]=1.5;} 
					if (bC.equals("Ultra Ball")) { 
						pkminfo[3]=2.0;} 
					//The Master Balls catch multiplier is set at 255, which makes he catch guaranteed
					if (bC.equals("Master Ball")){
						pkminfo[3]=255.0;}
					if (bC.equals("Premier Ball")){
						pkminfo[3]=1.0;}
					if (bC.equals("Luxury Ball")){
						pkminfo[3]=1.0;}
					if (bC.equals("Friend Ball")){
						pkminfo[3]=1.0;}
					if (bC.equals("Heal Ball")){
						pkminfo[3]=1.0;}
					//This piece of code handles the Moon Ball, which has a catch rate of 4 for all pokemon
					//that evolve using a Moon Stone. It has a catch rate of 1 for all other pokemon.
					if(bC.equals("Moon Ball")){
						if (pS.equals("Nidoran(m)")||pS.equals("Nidoran(f)")||pS.equals("Nidorino")||pS.equals("Nidorina")||pS.equals("Nidoking")||pS.equals("Nidoqueen")||pS.equals("Clefairy")||pS.equals("Cleffa")||
								pS.equals("Clefable")||pS.equals("Igglypuff")||pS.equals("Jigglypuff")||pS.equals("Wigglytuff")||pS.equals("Skitty")||pS.equals("Delcatty")||pS.equals("Munna")||pS.equals("Musharna")){
							pkminfo[3]=4.0;
						}else{
							pkminfo[3]=1.0;}}
					//The Fast Ball relies on the pokemon's speed, having a multiplier of 4 for all
					//pokemon with a speed of 100 or higher, 1 otherwise. 
					//This program uses the base speed of the pokemon, but the games will use the 
					//actual speed of the pokemon, which is based on the base speed, nature, and IV's
					//of the individual pokemon. Since nature and IV's are unknown until after capture,
					//they cannot be input into the calculator for better accuracy.
					if(bC.equals("Fast Ball")){
						if(data2.get(pS).speed>=100){
							pkminfo[3]=4.0;
						}else{
							pkminfo[3]=1.0;}}
					//The Dusk Ball has a catch rate of 4 at night or in a cave, 1 otherwise
					if(bC.equals("Dusk Ball")){
						if (nAns.equals("YES")){
							pkminfo[3]=4.0;
						}if(nAns.equals("NO")){
							pkminfo[3]=1.0;}}
					//The Repeat Ball has a multiplier of 3.5 if the pokemon has previously been registered as 
					//caught in the players pokedex. 1 multiplier otherwise.
					if(bC.equals("Repeat Ball")){
						if (rAns.equals("YES")){
							pkminfo[3]=3.5;
						}if(rAns.equals("NO")){
							pkminfo[3]=1.0;}}
					//The Dive Ball has a catch rate of 3.5 when used while surfing, and a 1
					//multiplier otherwise.
					if(bC.equals("Dive Ball")){
						if (diveAns.equals("YES")){
							pkminfo[3]=3.5;
						}if(diveAns.equals("NO")){
							pkminfo[3]=1.0;}}
					//The Love Ball has a catch rate of 8 when used on a pokemon of the SAME species BUT
					//opposite gender of the players pokemon. It has a multiplier of 1 otherwise.
					if(bC.equals("Love Ball")){
						if (Ans.equals("YES")){
							pkminfo[3]=8.0;
						}if(Ans.equals("NO")){
							pkminfo[3]=1.0;}}
					//The Lure Ball has a catch rate of 5 when fishing or surfing, and a catch rate of 1 otherwise.
					if(bC.equals("Lure Ball")){
						if (lureAns.equals("YES")){
							pkminfo[3]=5.0;
						}if(lureAns.equals("NO")){
							pkminfo[3]=1.0;}}
					//The Net Ball relies on the pokemon's type to calculate its multiplier. It has a catch rate
					//of 3.5 when used on a bug and/or water type, 1 otherwise.
					if(bC.equals("Net Ball")){
						if(data2.get(pS).type1.equals("Bug")||data2.get(pS).type1.equals("Water")){
							pkminfo[3]=3.5;
						}else{
							pkminfo[3]=1.0;}}
					//The Level Ball relies on a comparison of the player pokemon's level with that of the 
					//wild pokemon's level, with an increased multiplier the higher the player pokemon's level is compared to the 
					//wild pokemon's level.
					if(bC.equals("Level Ball")){
						if(levelP<=level){
							pkminfo[3]=1.0;}
						if(levelP>=level){
							pkminfo[3]=2.0;}
						if(levelP>=level*2){
							pkminfo[3]=4.0;}
						if(levelP>=level*4){
							pkminfo[3]=8.0;}
					}
					//The Nest Ball's multiplier relies on a calculation involving the wild pokemons level, with 
					//the catch rate going up the lower the level of the pokemon. Minimum catch rate is 1.
					if(bC.equals("Nest Ball")){
						pkminfo[3]=(8-0.2*(level-1));
						if(pkminfo[3]<1){
							pkminfo[3]=1.0;}}
					//The Quick Ball has a multiplier of 5 only when used on the first turn of battle,
					//and a catch rate of 1 otherwise.
					if(bC.equals("Quick Ball")){
						if(turnsR.getText().equals("1")){
							pkminfo[3]=5.0;
						}else{
							pkminfo[3]=1.0;}}
					//The Timer Ball has a catch rate starting at 1, increasing with each turn that passes 
					//with a max catch rate of 4.
					if(bC.equals("Timer Ball")){
						double x=1+((1229.0/4096.0)*(Double.parseDouble(turnsR.getText())));
						pkminfo[3]=x;
						if(x>4){
							pkminfo[3]=4.0;
						}}
					//The Beast Ball has a catch rate of 0.1 for all Pokemon EXCEPT Ultra Beasts, on which it has 
					//a catch rate of 5.
					if(bC.equals("Beast Ball")){
						pkminfo[3]=0.1;
						if(pS.equals("Nihilego")||pS.equals("Pheromosa")||pS.equals("Buzzwole")||pS.equals("Xurkitree")||pS.equals("Celesteela")||pS.equals("Kartana")||pS.equals("Guzzlord")||pS.equals("Poipole")||pS.equals("Naganadel")||pS.equals("Stakataka")||pS.equals("Blacephalon")){
							pkminfo[3]=5.0;
						}
					}
					//This piece of code handles the Ultra Beasts. Ultra Beasts ignore all poke ball multipliers to make
					//every poke ball have a multiplier of 1 when used on them, with the exception of the Master Ball and 
					//Beast Ball.
					if(!bC.equals("Master Ball")&&!bC.equals("Beast Ball")&&(pS.equals("Nihilego")||pS.equals("Pheromosa")||pS.equals("Buzzwole")||pS.equals("Xurkitree")||pS.equals("Celesteela")||pS.equals("Kartana")||pS.equals("Guzzlord")||pS.equals("Poipole")||pS.equals("Naganadel")||pS.equals("Stakataka")||pS.equals("Blacephalon"))){
						pkminfo[3]=1.0;
					}
					
					pkminfo[4]=status.get(sC); //retrieves the status condition multiplier.
					if(rotoAns.equals("YES")){
						pkminfo[5]=2.0;
					}if(rotoAns.equals("NO")){
						pkminfo[5]=1.0;
					}
					f.setVisible(true); //Keeps the calculator visible so it can be used again quickly
					new results(); //Calls the results window
				}}
		});
		
		
		
		
		//Adds all the parts to f, and makes f visible
		f.add(p);
		p.add(lab0);
		p.add(lab1);
		p.add(lab2);
		p.add(lab3);
		p.add(lab4);
		p.add(lab5);
		p.add(rotoCatch);
		p.add(rotoCatchAns);
		p.add(b);
		p.add(pkmSelect);
		p.add(levelW);
		p.add(hpNow);
		p.add(ballBonus);
		p.add(statusBonus);
		f.setVisible(true);
	}
	class results {
		static final int W=600,H=200;
		private JLabel result;
		
		public results(){
			double hpMax=hitPoints(pkminfo[1],31,level); //calculates the max possible HP of the wild pokemon
			double x=percentage(hpMax,pkminfo[2],pkminfo[0],pkminfo[3],pkminfo[4],pkminfo[5]); //Calculates the minimum catch rate 
			double hpMin=hitPoints(pkminfo[1],0,level); //calculates the minimum possible HP of the wild pokemon
			double y=percentage(hpMin,pkminfo[2],pkminfo[0],pkminfo[3],pkminfo[4],pkminfo[5]); //Calculates the maximum catch rate
			
			//Creates the results window
			f=new JFrame("Results");
			f.setSize(W,H); //Sets size of Frame from variables
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //for exiting
			
			p=new JPanel();
			p.setBackground(Color.PINK); //Sets background color of window
			p.setLayout(null);
			
			result = new JLabel("The chance of catching this pokemon is "+(y)+"%-"+x+"%"); //Displays the range of catch rates
			result.setBounds(0,0, 600,100); //positioning
			result.setFont(new Font("Serif", Font.PLAIN, 20)); 
			result.setMinimumSize(new Dimension(50,50));
			
			//Adds the parts and makes the window visible
			f.add(p);
			p.add(result);
			f.setVisible(true);
		}
	}
	//This method performs the actual calculations, taking the HP at full health, the current HP, the pokemon's catch rate,
	//the multiplier of the poke ball used, and the multiplier from the status condition.
	public double percentage(double hpFull,double hpCurrent, double rate, double ballBonus, double statusBonus, double rotomPower){
		double a=(((3*hpFull-2*hpCurrent)*rate*ballBonus)/(3*hpFull))*statusBonus; //Calculates the modified catch rate
		//Next three lines calculate the shake check, with b being the final result
		double x=255/a; 
		double y=Math.pow(x,(3.0/16.0));
		double b=65536/y;
		//Calculates the probability that, when four numbers are created between 0 and 65536, that the numbers 
		//are all less than or equal to b.
		double p=Math.pow((b/65536),4.0);
		//Rounds the probability 
		p=Math.round(p*10000);
		p=p/100; //Turns the answer into a percentage
		if(p>100){ //Prevents the percentage from going above 100
			p=100.00;
		}
		p=p*rotomPower;
		return p;
	}
	//Method used to calculate the HP of a pokemon based on it's base HP, it's HP IV and level.
	public double hitPoints(double base,int IV, double level){
		double x=Math.round(((((2*base)+IV)*level)/100)+level+10); 
		return x;
	}
	
}