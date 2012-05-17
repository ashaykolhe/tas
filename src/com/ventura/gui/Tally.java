package com.ventura.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ventura.converter.Converter;

/**
 * @author Ashay Kolhe 
 * @param date 6th February 2012
 */
public class Tally {

	
	private JFrame frame;
	private JPanel jp,firstpanel,secondpanel,datePanel,monthPanel,p3,errorsPanel,northPanel,southPanel;
	private JLabel ddjl,mmjl,yyyyjl,destinationFileName,title,errors,dateTitle,monthTitle;
	private JTextField dd,mm,yyyy,fileName;
	private JButton button,today,month;
	private JList months,year;
	private ButtonGroup buttonGroup;
	private JRadioButton Male,Female;
	private JScrollPane scrollerYear, scroller;
	private String selectedMonth,selectedYear,returnTextDaily,returnTextMonthly;

	private boolean monthly;
	
	
	/**
	 * @param This is the main class which starts the application
	 */
	public static void main(String args[]){
		Tally tally=new Tally();
		tally.buildGui();

	}

	
	/**
	 * @param This is the method which constructs the GUI
	 */
	public void buildGui(){

		//create
		frame=new JFrame();
		
		firstpanel = new JPanel();
		secondpanel = new JPanel();
		datePanel=new JPanel();
		monthPanel=new JPanel();
		p3 = new JPanel();
		jp=new JPanel();
		northPanel=new JPanel();
		
		southPanel=new JPanel();
		errorsPanel=new JPanel();
		
		buttonGroup = new ButtonGroup();
		
		Male = new JRadioButton("");
		buttonGroup.add(Male);
		Female = new JRadioButton("");
		buttonGroup.add(Female);
		
		title=new JLabel("Tas to XML Converter");
		dateTitle=new JLabel("Enter Date");
		monthTitle=new JLabel("Select Month & Year");
		ddjl=new JLabel("dd");
		mmjl=new JLabel("mm");
		yyyyjl=new JLabel("yyyy");
		errors=new JLabel(" ");
		destinationFileName=new JLabel("Destination file name");
		
		dd=new JTextField(2);
		mm=new JTextField(2);
		yyyy=new JTextField(4);
		fileName=new JTextField(30);

		String monthsArray[]={"January","February","March","April","May","June","July","August","September","October","November","December"};
		String yearArray[]={"2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"};

		months=new JList(monthsArray);
		year=new JList(yearArray);

		scroller=new JScrollPane(months);
		scrollerYear=new JScrollPane(year);

		button=new JButton("create XML");
		today=new JButton("Create XML for today");
		month=new JButton("Create XML for current month");

		
		
		
		//properties
		months.setEnabled(false);

		year.setEnabled(false);
		dd.setEnabled(false);
		mm.setEnabled(false);
		yyyy.setEnabled(false);

		

		Male.setSelected(false);
		button.setEnabled(false);
		
		
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


		scrollerYear.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollerYear.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		months.setVisibleRowCount(12);
		months.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		year.setVisibleRowCount(12);
		year.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		
		fileName.setFocusable(true);
		secondpanel.setLayout(new BoxLayout(secondpanel, BoxLayout.PAGE_AXIS));

		datePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		monthPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

		dateTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		monthTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		errors.setFont(new Font("lucidasansconsole", Font.BOLD, 14));
		errors.setForeground(Color.RED);
		
		//border
		northPanel.setBorder(create("Hotel Suncity",new Font("tahoma", Font.BOLD, 14) , Color.RED));
		errorsPanel.setBorder(create("Result",new Font("lucidasansconsole", Font.PLAIN, 14) , Color.RED));
		datePanel.setBorder(create("DateWise",new Font("lucidasansconsole", Font.PLAIN, 14) , Color.RED));
		monthPanel.setBorder(create("MonthWise",new Font("lucidasansconsole", Font.PLAIN, 14) , Color.RED));
		p3.setBorder(create("Create",new Font("lucidasansconsole", Font.PLAIN, 14) , Color.RED));
		

		
		//listeners
		Male.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				errors.setText(" ");
				dd.setText("");
				mm.setText("");
				yyyy.setText("");
				today.setEnabled(false);
				month.setEnabled(false);
				dd.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				mm.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				yyyy.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				button.setEnabled(true);
				button.setText("Create XML for Selected Date");

			}
		});
		
		Female.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				errors.setText(" ");
				months.setSelectedIndex(-1);
				year.setSelectedIndex(-1);
				today.setEnabled(false);
				month.setEnabled(false);
				months.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				year.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				button.setEnabled(true); 
				button.setText("Create XML for Selected Month");
			}
		});
		
		
		button.addActionListener(new ButtonListener());
		months.addListSelectionListener(new ListListener());
		year.addListSelectionListener(new ListListener());

		today.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!fileName.getText().isEmpty()){
					String returnText=new Converter().start(false, new SimpleDateFormat("ddMMyyyy").format(new Date()), fileName.getText());
					if(!returnText.isEmpty() && returnText.contains("already exists")){
						fileName.setText("");
						fileName.grabFocus();
					}else if(!returnText.isEmpty() && returnText.contains("File saved")){
						changeGuiAfterFileSave();
					}
					errors.setText(returnText);
					
				}else{
					errors.setText("Please enter a filename");
					fileName.grabFocus();
				}
			}
		});
		
		month.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!fileName.getText().isEmpty()){
					String returnText=new Converter().start(true, new SimpleDateFormat("MMyyyy").format(new Date()), fileName.getText());
					if(!returnText.isEmpty() && returnText.contains("already exists")){
						fileName.setText("");
						fileName.grabFocus();
					}else if(!returnText.isEmpty() && returnText.contains("File saved")){
						changeGuiAfterFileSave();
					}
					errors.setText(returnText);
				}else{
					errors.setText("Please enter a filename");
					fileName.grabFocus();
				}
			}
		});
		
		
		
		//add
		firstpanel.add(title);
		northPanel.add(firstpanel);
		northPanel.add(jp);
		datePanel.add(Male);
		datePanel.add(ddjl);
		datePanel.add(dd);
		datePanel.add(mmjl);
		datePanel.add(mm);
		datePanel.add(yyyyjl);
		datePanel.add(yyyy);
		monthPanel.add(Female);
		monthPanel.add(scroller);
		monthPanel.add(scrollerYear);
		secondpanel.add(datePanel);
		secondpanel.add(monthPanel);
		p3.add(button);
		p3.add(today);
		p3.add(month);
		jp.add(destinationFileName);
		jp.add(fileName);
		errorsPanel.add(errors);
		southPanel.add(p3);
		southPanel.add(errorsPanel);


		//contentpane
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());
		content.add(secondpanel,BorderLayout.CENTER);
		content.add(northPanel,BorderLayout.NORTH);
		content.add(southPanel,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle("Hotel Suncity");
		frame.setSize(600, 550);
		frame.setVisible(true);

	}


	/**
	 * @author Ashay
	 *	
	 */
	class ButtonListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent ae) {

			try {
				if(validate()){
					if(!fileName.getText().isEmpty()){
						convert();
					}else{
						errors.setText("Enter a filename.");
						fileName.grabFocus();
					}
				}
			} catch (NumberFormatException e) {
				errors.setText("Enter only numbers.");
				dd.setText("");
				mm.setText("");
				yyyy.setText("");

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	/**
	 * @author Ashay
	 *
	 */
	class ListListener implements ListSelectionListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		@Override
		public void valueChanged(ListSelectionEvent lse) {
			if(!lse.getValueIsAdjusting()){
				selectedYear=(String)year.getSelectedValue();
				switch(months.getSelectedIndex()){
				case 0:
					selectedMonth="01";
					break;
				case 1:
					selectedMonth="02";
					break;
				case 2:
					selectedMonth="03";
					break;
				case 3:
					selectedMonth="04";
					break;
				case 4:
					selectedMonth="05";
					break;
				case 5:
					selectedMonth="06";
					break;
				case 6:
					selectedMonth="07";
					break;
				case 7:
					selectedMonth="08";
					break;
				case 8:
					selectedMonth="09";
					break;
				case 9:
					selectedMonth="10";
					break;
				case 10:
					selectedMonth="11";
					break;
				case 11:
					selectedMonth="12";
					break;
				default:
					selectedMonth="";
				}
				returnTextMonthly=selectedMonth+""+selectedYear;
				monthly=true;
			}

		}



	}

	/**
	 * @param calls the converter method of the Converter class
	 */
	public void convert(){
		Converter converter=new Converter();
		if(monthly){
				String returnText=converter.start(true,returnTextMonthly,fileName.getText());
				if(!returnText.isEmpty() && returnText.contains("already exists")){
					fileName.setText("");
					fileName.grabFocus();
				}else if(!returnText.isEmpty() && returnText.contains("File saved")){
					changeGuiAfterFileSave();
				}
				errors.setText(returnText);
		}else{
			returnTextDaily=dd.getText()+""+mm.getText()+""+yyyy.getText();
			String returnText=converter.start(false, returnTextDaily,fileName.getText());
			if(!returnText.isEmpty() && returnText.contains("already exists")){
				fileName.setText("");
			}else if(!returnText.isEmpty() && returnText.contains("File saved")){
				changeGuiAfterFileSave();
			}
			errors.setText(returnText);
		}
	}

	/**
	 * @param returns true if validations are successful
	 * @return true/false
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public Boolean validate() throws NumberFormatException,Exception{
		if(Male.isSelected()){
			if(!dd.getText().isEmpty()){
				dd.getText().trim();
				if(dd.getText().length()>2 || Integer.parseInt(dd.getText())<1 || Integer.parseInt(dd.getText())>31){
					errors.setText("Date should be between 1 and 31");
					dd.setText("");
					dd.grabFocus();
					return false;
				}
			}else{
				errors.setText("Enter date.");
				dd.setText("");
				dd.grabFocus();
				return false;
			}

			if(!mm.getText().isEmpty()){
				mm.getText().trim();
				if(Integer.parseInt(mm.getText())<1 || Integer.parseInt(mm.getText())>12){
					errors.setText("Month should be between 1 and 12");
					mm.setText("");
					mm.grabFocus();
					return false;
				}

			}else{
				errors.setText("Enter month.");
				mm.setText("");
				mm.grabFocus();
				return false;
			}

			if(!yyyy.getText().isEmpty()){
				yyyy.getText().trim();
				if(yyyy.getText().length()!=4){
					errors.setText("Enter the correct year");
					yyyy.setText("");
					yyyy.grabFocus();
					return false;
				}
			}else{
				errors.setText("Enter year.");
				yyyy.setText("");
				yyyy.grabFocus();
				return false;
			}
			return true;
		}else if(Female.isSelected()){
			if(months.isSelectionEmpty()){
				errors.setText("Select a month");
				months.grabFocus();
				return false;
			}else if(year.isSelectionEmpty()){
				errors.setText("Select an year");
				year.grabFocus();
				return false;
			}
			return true;

		}else{
			errors.setText("Select month and year.");
			months.grabFocus();
			return false;
		}

	}
	
	/**
	 * @param creates titled borders for GUI
	 * @param text
	 * @param font
	 * @param color
	 * @return
	 */
	public TitledBorder create(String text,Font font,Color color){
		TitledBorder bf=BorderFactory.createTitledBorder(text);
		bf.setTitleColor(color);
		bf.setTitleFont(font);
		return bf;
	}

	protected void changeGuiAfterFileSave() {
		buttonGroup.clearSelection();
		button.setEnabled(false);
		today.setEnabled(true);
		month.setEnabled(true);
		dd.setText("");
		mm.setText("");
		yyyy.setText("");
		dd.setEnabled(false);
		mm.setEnabled(false);
		yyyy.setEnabled(false);
		months.setSelectedIndex(0);
		year.setSelectedIndex(0);
		months.setEnabled(false);
		year.setEnabled(false);
		
	}

}