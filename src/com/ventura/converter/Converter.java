package com.ventura.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ventura.exceptions.FileNotFoundException;
import com.ventura.exceptions.FolderNotFoundException;
import com.ventura.exceptions.NoMatchingMonthYearFilesExistException;
import com.ventura.exceptions.NoTasFileFoundForSelectedDateException;

/**
 * @author Ashay
 *	
 */
public class Converter {
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	private Element nodeElement;
	private BufferedReader in;
	private SortedSet<String> employeeCodeForMonth;
	private Map<String,SortedSet<String>> listOfEmployeesPresentPerDay;
	private Map<String,Integer> attendance;
	
	
	/**
	 * @param This method starts conversion of Tas files to XML
	 * @param monthly
	 * @param text
	 * @param fileName
	 * @return
	 */
	public String start(final Boolean monthly,final String text,final String fileName) {
		// TODO Auto-generated method stub

		try {
			parseTasFiles(monthly,text);
			docFactory= DocumentBuilderFactory.newInstance();
			docBuilder= docFactory.newDocumentBuilder();

			// root elements
			doc= docBuilder.newDocument();
			Element rootElement = doc.createElement("ENVELOPE");
			doc.appendChild(rootElement);

			Element header = doc.createElement("HEADER");
			rootElement.appendChild(header);

			Element tallyrequest=doc.createElement("TALLYREQUEST");
			header.appendChild(tallyrequest);


			Element body = doc.createElement("BODY");
			rootElement.appendChild(body);

			Element requestdesc = doc.createElement("REQUESTDESC");
			body.appendChild(requestdesc);

			Element reportname=doc.createElement("REPORTNAME");
			reportname.appendChild(doc.createTextNode("All Masters"));
			requestdesc.appendChild(reportname);

			Element staticvariables=doc.createElement("STATICVARIABLES");
			requestdesc.appendChild(staticvariables);

			Element svcurrentcompany=doc.createElement("SVCURRENTCOMPANY");
			svcurrentcompany.appendChild(doc.createTextNode("Hotel Suncity"));
			staticvariables.appendChild(svcurrentcompany);

			Element requestdata = doc.createElement("REQUESTDATA");
			body.appendChild(requestdata);

			Element tallymessage1=doc.createElement("TALLYMESSAGE");
			tallymessage1.setAttribute("xmlns:UDF", "TallyUDF");
			requestdata.appendChild(tallymessage1);

			Element voucher1=doc.createElement("VOUCHER");
			voucher1.setAttribute("REMOTEID", "f24c0636-6534-449a-929a-e2b4913b8711-0000002b");
			voucher1.setAttribute("VCHKEY", "f24c0636-6534-449a-929a-e2b4913b8711-00009c77:00000008");
			voucher1.setAttribute("VCHTYPE", "Attendance");
			voucher1.setAttribute("ACTION", "Create");
			voucher1.setAttribute("OBJVIEW", "Accounting Voucher View");
			tallymessage1.appendChild(voucher1);

			String narration=null;
			if(monthly){
				narration="Attendance for the month of ";
			}else{
				narration="Attendance for ";
			}
			String[] voucherChildrenNodeName={"DATE","GUID","NARRATION","VOUCHERTYPENAME","VOUCHERTYPENUMBER","CSTFORMISSUETYPE","CSVFORMRECVTYPE","FBTPAYMENTTYPE","PERSISTEDVIEW","VCHGSTCLASS","DIFFACTUALQTY","AUDITED","FORJOBCOSTING","ISOPTIONAL","EFFECTIVEDATE","ISFORJOBWORKIN","ALLOWCONSUMPTION","USEFORINTEREST","USEFORGAINLOSS","USEFORGODOWNTRANSFER","USEFORCOMPOUND","ALTERID","EXCISEOPENING","USEFORFINALPRODUCTION","ISCANCELLED","HASCASHFLOW","ISPOSTDATED","HASTRACKINGNUMBER","ISINVOICE","MFGJOURNAL","HASDISCOUNTS","ASPAYSLIPS","ISCOSTCENTRE","ISSTXNONREALIZEDVCH","ISDELETED","ASORIGINAL","VCHISFROMSYNC","MASTERID","VOUCHERKEY","AUDITENTRIES.LIST","INVOICEDELNOTES.LIST","INVOICEORDERLIST.LIST","INVOICEINDENTLIST.LIST"};
			String[] voucherChildrenNodeValuesPresent={getCurrentFormattedDate(),"f24c0636-6534-449a-929a-e2b4913b8711-0000002b",narration+""+getMonthAndYearForNarration(monthly,text),"Attendance","8","","","DEFAULT","Accounting Voucher View","","No","No","No","No",getCurrentFormattedDate(),"No","No","No","No","No","No","48","No","No","No","No","No","No","No","No","No","No","No","No","No","No","No","43","172034915041288","","","",""};
			createElements(voucher1, voucherChildrenNodeName, voucherChildrenNodeValuesPresent);

			String[] nodeName={"EMPLOYEECODE","ATTENDANCETYPE","ATTDTYPETIMEVALUE"};

			if(monthly){ //if conversion is as per month
				getAttendance(text); //parse files and fill a Map with the attendance of employees for the selected month
				Iterator<String> i=attendance.keySet().iterator();
				while (i.hasNext()){
					String empcode=i.next();
					Element attendanceentries=doc.createElement("ATTENDANCEENTRIES.LIST");
					voucher1.appendChild(attendanceentries);

					String[] nodeValue={empcode,"Present",attendance.get(empcode).toString()};
					createElements(attendanceentries, nodeName, nodeValue);
				}

			}else{

				if(listOfEmployeesPresentPerDay.containsKey(text)){ //if the selected date's tas file is present, then listOfEmployees would contain the date
					Iterator<String> i = listOfEmployeesPresentPerDay.get(text).iterator();

					while (i.hasNext()){
						String empcode=i.next();
						Element attendanceentries=doc.createElement("ATTENDANCEENTRIES.LIST");
						voucher1.appendChild(attendanceentries);


						String[] nodeValue={empcode,"Present",""};
						createElements(attendanceentries, nodeName, nodeValue);
					}
				}

			}

			String[] voucherEndName={"ALLLEDGERENTRIES.LIST","ATTDRECORDS.LIST","LEDGERENTRIESRECORDS.LIST","LEDGERCSTENTRIESRECORDS.LIST"};
			String[] vouchedEndValues={"","","",""};
			createElements(voucher1, voucherEndName, vouchedEndValues);



			if(monthly){
				Element tallymessage2=doc.createElement("TALLYMESSAGE");
				tallymessage2.setAttribute("xmlns:UDF", "TallyUDF");
				requestdata.appendChild(tallymessage2);

				Element voucher2=doc.createElement("VOUCHER");
				voucher2.setAttribute("REMOTEID", "f24c0636-6534-449a-929a-e2b4913b8711-0000002b");
				voucher2.setAttribute("VCHKEY", "f24c0636-6534-449a-929a-e2b4913b8711-00009c77:00000008");
				voucher2.setAttribute("VCHTYPE", "Attendance");
				voucher2.setAttribute("ACTION", "Create");
				voucher2.setAttribute("OBJVIEW", "Accounting Voucher View");
				tallymessage2.appendChild(voucher2);
				String[] voucherChildrenNodeValuesAbsent={getCurrentFormattedDate(),"f24c0636-6534-449a-929a-e2b4913b8711-00000034","Attendance details for contract employees for "+getMonthAndYearForNarration(monthly,text)+".","Attendance","9","","","DEFAULT","Accounting Voucher View","","No","No","No","No",getCurrentFormattedDate(),"No","No","No","No","No","No","57","No","No","No","No","No","No","No","No","No","No","No","No","No","No","No","52","172034915041360","","","",""};			
				createElements(voucher2, voucherChildrenNodeName, voucherChildrenNodeValuesAbsent);

				Integer daysInMonth=getMonth(text); //get the no. of days in selected month
				Iterator<String> i=attendance.keySet().iterator();

				while (i.hasNext()){
					String empcode=i.next();

					Element attendanceentries=doc.createElement("ATTENDANCEENTRIES.LIST");
					voucher2.appendChild(attendanceentries);

					Integer absent=daysInMonth-attendance.get(empcode);

					String[] nodeValue={empcode,"Absent",absent.toString()};
					createElements(attendanceentries, nodeName, nodeValue);

				}
				createElements(voucher2, voucherEndName, vouchedEndValues);
			}						

			Element tallymessage3=doc.createElement("TALLYMESSAGE");
			tallymessage3.setAttribute("xmlns:UDF", "TallyUDF");
			requestdata.appendChild(tallymessage3);

			Element company=doc.createElement("COMPANY");
			tallymessage3.appendChild(company);

			Element remotecmpinfolist=doc.createElement("REMOTECMPINFO.LIST");
			remotecmpinfolist.setAttribute("MERGE", "Yes");
			company.appendChild(remotecmpinfolist);

			Element name=doc.createElement("NAME");
			remotecmpinfolist.appendChild(doc.createTextNode("f24c0636-6534-449a-929a-e2b4913b8711"));
			company.appendChild(name);

			Element remotecmpname=doc.createElement("REMOTECMPNAME");
			remotecmpname.appendChild(doc.createTextNode("Hotel Suncity"));
			company.appendChild(remotecmpname);

			Element remotecmpstate=doc.createElement("REMOTECMPSTATE");
			remotecmpstate.appendChild(doc.createTextNode("Karnataka"));
			company.appendChild(remotecmpstate);



			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File desDir=new File("C:\\converted");
			desDir.mkdir();
			File exists=new File("C:\\converted\\"+fileName+".xml");
			StreamResult result = null;
			if(!exists.exists()){
				result = new StreamResult(new File(desDir+"\\"+fileName+".xml"));
			}else{
				return "File \""+fileName+".xml\" already exists. XML not generated.";
			}
			
			

			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);



		} catch (FolderNotFoundException fonfe) {
			return fonfe.getMessage();
		}catch (FileNotFoundException finfe) {
			return finfe.getMessage();
		}catch(NoMatchingMonthYearFilesExistException nmmyfee){
			return nmmyfee.getMessage();
		}catch(NoTasFileFoundForSelectedDateException ntfffsde){
			return ntfffsde.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			return "XML not generated due to errors.";
		}

		return "File saved.";




	}

	/**
	 * @param creates node elements
	 * @param root
	 * @param nodeName
	 * @param nodeValue
	 */
	public void createElements(Element root,String[] nodeName,String[] nodeValue){

		for(int i=0;i<nodeName.length;i++){
			nodeElement=doc.createElement(nodeName[i]);
			nodeElement.appendChild(doc.createTextNode(nodeValue[i]));
			root.appendChild(nodeElement);

		}

	}

	public String getCurrentFormattedDate(){

		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	public String compareDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
		return sdf.format(new Date());
	}

	public String getMonthAndYearForNarration(Boolean monthly,String text) throws Exception{
		Integer date=null;
		Integer month=null;
		Integer year=null;
		SimpleDateFormat sdf=null;
		if(monthly){
			date=10;
			month=Integer.parseInt(text.substring(0, 2))-1;
			year=Integer.parseInt(text.substring(2, 6));
			sdf=new SimpleDateFormat("MMMM yyyy");

		}else{
			date=Integer.parseInt(text.substring(0, 2));;
			month=Integer.parseInt(text.substring(2, 4))-1;
			year=Integer.parseInt(text.substring(4, 8));
			sdf=new SimpleDateFormat("dd MMMM yyyy");
		}

		Calendar gc=new GregorianCalendar();
		gc.set(year, month, date);
		return sdf.format(gc.getTime());
	}

	public Integer getMonth(String text){
		Calendar calendar = new GregorianCalendar();
		int year = Integer.parseInt(text.substring(2, 6));
		int month = Integer.parseInt(text.substring(0, 2))-1;
		int date = 1;
		calendar.set(year, month, date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * @param parses the Tas files
	 * @param monthly
	 * @param text
	 * @throws NoMatchingMonthYearFilesExistException
	 * @throws Exception
	 */
	public void parseTasFiles(boolean monthly,String text) throws NoMatchingMonthYearFilesExistException,Exception{
		ArrayList<File> fileList=getFileList();
		if(!fileList.isEmpty()){
			employeeCodeForMonth=new TreeSet<String>();
			SortedSet<String> setOfEmployeesForEachDay=null;
			listOfEmployeesPresentPerDay=new HashMap<String, SortedSet<String>>();
			for(int i=0;i<fileList.size();i++){
				File file=fileList.get(i);
				if(file.isDirectory()){ //if the file is a directory, then parse the files in the directory as well
					File folder=new File(file.getAbsolutePath());
					Iterator<File> subFolderFiles=Arrays.asList(folder.listFiles()).iterator();
					while(subFolderFiles.hasNext()){
						fileList.add(subFolderFiles.next()); //add the subdirectory files to the main list
					}
					continue;
				}
				
				
				if(file.toString().endsWith(".tas") || file.toString().endsWith(".TAS")){ //only parse the Tas files
					in=new BufferedReader(new FileReader(file));
					setOfEmployeesForEachDay=new TreeSet<String>();
					String temp=null;
					String date=null;
					while ((temp = in.readLine()) != null  && !temp.equals("")) {
						if(monthly && temp.substring(12, 18).equals(text)){
							employeeCodeForMonth.add(temp.substring(18, 26)); //saves the employees for the selected month
						}
						setOfEmployeesForEachDay.add(temp.substring(18, 26)); //saves the employee code for each file parsed
						date=temp;
					}
					listOfEmployeesPresentPerDay.put(date.substring(10, 18), setOfEmployeesForEachDay); //saves the distinct employees per file(day) 
					in.close();
				}
				
			}

			if(monthly){
				if(employeeCodeForMonth.isEmpty()){
					throw new NoMatchingMonthYearFilesExistException("No Files for this Month found.");
				}
			}else{
				if(!listOfEmployeesPresentPerDay.containsKey(text)){
					throw new NoTasFileFoundForSelectedDateException("No Tas file found for selected date.");
				}
			}


		}

	}



	/**
	 * @param returns all the files and folders in the selected folder
	 * @return
	 * @throws FolderNotFoundException
	 * @throws FileNotFoundException
	 */
	public ArrayList<File> getFileList() throws FolderNotFoundException, FileNotFoundException{
		File folder = new File("c:\\Tas");
		List<File> fileList=null;
		
		if(folder.exists()){
			fileList= Arrays.asList(folder.listFiles());
			if(fileList.isEmpty()){
				throw new FileNotFoundException("No tas files found to convert.");
			}
		}else{
			throw new FolderNotFoundException("\"C:\\Tas\" Folder does not exist.");
		}

		ArrayList<File> subFolderFileList=new ArrayList<File>();
		Iterator<File> ite=fileList.iterator();
		while(ite.hasNext()){
			subFolderFileList.add(ite.next());
		}

		return subFolderFileList;

	}

	/**
	 * @param returns a map of the attendance of each employee for the specified month
	 * @param text
	 * @throws Exception
	 */
	public void getAttendance(String text) throws Exception{
		attendance=new HashMap<String, Integer>();
		Iterator<String> j=employeeCodeForMonth.iterator();

		int count;
		while(j.hasNext()){
			String empcode=j.next();
			count=0;
			Iterator<String> i=listOfEmployeesPresentPerDay.keySet().iterator();
			while(i.hasNext()){
				String date=i.next();
				if(date.substring(2).equals(text) && listOfEmployeesPresentPerDay.get(date).contains(empcode)){ //check date and whether the employeecode is present in the valueset
					count++;
				}
			}
			attendance.put(empcode, count);

		}
	}
	
	


}

