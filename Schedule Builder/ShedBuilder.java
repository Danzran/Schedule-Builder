// Schedule builder bulk


import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FinalProject {

	public static void main(String[] args) {
		//Change this to true in the delete lectures method
		boolean delete = false;
		ArrayList<Integer> deletedCrns = new ArrayList<Integer>();
		
						
		int choice = 0;	//user choice
		ArrayList<Person> people = new ArrayList<Person>();	//stores all of our faculty, TA's, and students
		//int peopleAmount = 0;
		ClassManager manager = new ClassManager();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the absolute path of the file: ");
		String file = scanner.nextLine();
		manager.readClasses(file);		//this stores our list of classes in lec.txt
		while (choice != 7) {	
		System.out.println("Choose one of these options:\r\n"
				+ "1- Add a new Faculty to the schedule\r\n"
				+ "2- Enroll a Student to a Lecture\r\n"
				+ "3- Print the schedule of a Faculty\r\n"
				+ "4- Print the schedule of an TA\r\n"
				+ "5- Print the schedule of a Student\r\n"
				+ "6- Delete a Lecture\r\n"
				+ "7- Exit");
		System.out.println("\t\tEnter your choice: ");
		while (true) {		//make sure we have valid choice input
		    try {
		        choice = scanner.nextInt();
		        if (choice > 0 && choice <= 7) {
		            break;
		        } else {
		            System.out.println("Invalid Input, please enter an integer between 1 and 7");
		        }
		    } catch (InputMismatchException e) {
		        System.err.println("Invalid Input, please enter an integer between 1 and 7");
		        scanner.next(); //consume the invalid input to prevent an infinite loop
		    }
		}
		//if blocks for the options
		int ucfId, numberLectures;
		String name, rank, officeLocation;
		Person ucfIdCopy;
		if (choice == 1) {	//add a new faculty to the schedule
			Faculty faculty = new Faculty();
			while (true) {
			System.out.print("Enter UCF id: ");
			try {
		        ucfId = scanner.nextInt();			//cannot think how to not check for 7 digits in id for
		        									//several choices without waiting until the constructor is called
		        									//***can fix by generating with only the id first then using setters
		        faculty.setUcfId(ucfId);
		        break;	//user entered integer(checked by try catch) and it was 7 digits(checked by setucfUd)
		        		//still need to check if id is unique
		    } catch (InputMismatchException e) {
		        System.err.println("Invalid Input, please enter a 7 digit integer");
		        scanner.next(); //consume the invalid input to prevent an infinite loop
		    }
			}
			//checking that id entered is unique, if unique(continue), if another faculty(skip name,rank,office)
			//if a different type(ta/student) say that is already a student/ta Id and return to main menu
			boolean isUnique = Person.checkIfUnique(ucfId, people);
			if (isUnique) {// the UCF ID is unique, proceed with rest of option 1 here
				scanner.nextLine();
				System.out.println("Enter name: ");
				name = scanner.nextLine();
				faculty.setName(name);
				while(true) {
					System.out.print("Enter rank(professor, associate professor, assistant professor or adjunct): ");
					rank = scanner.nextLine();
					if (rank.toLowerCase().equals("professor") ||
						rank.toLowerCase().equals("associate professor") ||
						rank.toLowerCase().equals("assistant professor") ||
						rank.toLowerCase().equals("adjunct")) {	//rank is valid
						faculty.setRank(rank);
						break;
					}
					else {	//rank is invalid
						System.out.println("Sorry, invalid rank(must be professor,"
								+ " associate professor, assistant professor or adjunct");
					}
				}
				System.out.println("Enter office location: ");
				officeLocation = scanner.nextLine();
				faculty.setOfficeLocation(officeLocation);
				while (true) {
					System.out.println("Enter how many lectures: ");
					try {
						numberLectures = scanner.nextInt();
						if (numberLectures < 1) {
							System.out.println("Invalid Input, please enter a positive integer");
						}
						else {
							break;	//numberLectures is valid positive integer
						}
					} catch (InputMismatchException e){
						System.err.println("Invalid Input, please enter a positive integer");
				        scanner.next(); //consume the invalid input to prevent an infinite loop
					}
				}
				scanner.nextLine();
				System.out.printf("Enter the %d CRNs of the lectures (space-separated):\n", numberLectures);
				String crnsStr = scanner.nextLine();
				String[] crns = crnsStr.split(" ");
				int beingTaught = 0;
				for (String crn : crns) {
				    for (Person person : people) {
				    	if (person instanceof Faculty) {
				    		Faculty faculty1 = (Faculty) person;
				    		for (String[] facultyLecture : faculty1.getLecturesTaught()) {
				    			if(facultyLecture[0].equals(crn)) {
				    				System.out.println("This lecture is already being taught");
				    				beingTaught = 1;
				    			}
				    		}
				    	}
				    }
				    if (beingTaught == 0) {
				    	//find the course in classManager
					    int index = -1;	//will crash program if crn entered is incorrect, but assignment says assume crns are correct
					    for(int i = 0; i < manager.classes.size(); i++) {
					    	String[] temp = manager.classes.get(i);
					    	if(temp[0].equals(crn)) {
					    		index = i;
					    		break;
					    	}
					    }
				        faculty.lecturesTaught.add(manager.classes.get(index));
				        //people.add(faculty);
				        //if lecture has no labs/ writes info to screen,if lecture has labs, prints labs and returns crns of labs
				        	ArrayList<String[]> labs = ClassManager.getLectureInfoByCRN(crn);
				        	int taUcfId;
				        	String taName, taSupervisor, taDegreeSeeking;
				        	if (labs != null) {
				        		//do ask to enter the UCF id of the TA for each lab (a TA may do more than
				        		//one Lab). This may require entering a new TA to the system, and in this case, you need to
				        		//ask for the remaining information of the TA. Keep in mind that a TA can be a student
				        		for (String[] lab : labs) {
				        			TA ta = new TA();
				        			//scanner.next();
				        			while (true) {
				        				System.out.printf("Enter the TA's id for %s:\n", lab[0]);
				        				try {
				        					taUcfId = scanner.nextInt();//make sure this isn't our current faculty ucfId
				        					if (taUcfId == faculty.getUcfId()) {
				        						taUcfId = 1;	//setUcfId will not allow this
				        					}
				        					ta.setUcfId(taUcfId);
				        					break;
				        				} catch (InputMismatchException e) {
				        					System.err.println("Invalid input, please enter a 7 digit integer");
				        					scanner.next();
				        				}
				        			}	
				        			boolean isUniqueTa = Person.checkIfUnique(taUcfId, people);
				        			//if deleting a course makes a TA no longer TA'ing any courses, completely remove the TA
				        			//professor said that is fine to another student, when not unique, check that ta is not taking any class
				        			//with the same course prefix(not crn)
				        			if (isUniqueTa)	{//we are creating a new student as ta, no need to check for taking this class
				        				scanner.nextLine();
				        				System.out.println("Enter the name of TA: ");
				        				taName = scanner.nextLine();
				        				ta.setName(taName);
				        				System.out.println("Enter the TA's supervisor's name: ");
				        				taSupervisor = scanner.nextLine();
				        				ta.setAdvisor(taSupervisor);
				        				//instructions are unclear if supervisor must be a faculty or not
				        				//in example run, it obly asks for a name and does nothing with it, so i am only having it as a string	
				        				while(true) {
				        					System.out.println("Enter the degree seeking(MS or PhD): ");
				        					taDegreeSeeking = scanner.nextLine();
				        					if(taDegreeSeeking.toLowerCase().equals("ms") ||
				        						taDegreeSeeking.toLowerCase().equals("phd")) {	//taDegreeSeeking is valid
				        						ta.setExpectedDegree(taDegreeSeeking);
						        				break;
				        					} else {	//taDegreeSeeking is invalid
				        						System.out.println("Sorry, invalid degree seeking(must be ms or phd");
				        					}
				        				}//add ta to people and add lab to taLabs
				        				ta.labs.add(lab);
				        				people.add(ta);
				        				continue;
				        			}//end of isUniqueTa
				        			if(!isUniqueTa) {	//2 options, already a ta or already a student
				        				//already a ta, check that ta is not taking course with same prefix
				        				int index1 = 0;
				        				boolean isStudent = false;
	        							boolean isTaken = false;
				        				for (Person person : people) {
				        					if (person.getUcfId() == taUcfId) {
				        						if (person instanceof Faculty) {
				        							System.out.println("Sorry that is the ucfId of a faculty member");
				        						} else {//check if taking lecture with that prefix
				        							TA taTemp;
				        							if(!(person instanceof TA)) {//we are making a student a TA
				        								isStudent = true;
				        								Student studentTemp = (Student) person;
				        								taTemp = new TA();
				        								taTemp.setName(studentTemp.getName());
				        								taTemp.setUcfId(studentTemp.getUcfId());
				        								taTemp.setType(studentTemp.getType());
				        								taTemp.setClassesTaken(studentTemp.getClassesTaken());
				        								//now remove the student from ArrayList since it will be added back as TA
				        								for (Person potentialStudent : people) {
				        									if (potentialStudent.getUcfId() == taUcfId) {
				        										break; //the index will be stored
				        									}
				        									else {
				        										index1++;
				        									}
				        								}
				        								
				        							}
				        							else {//person is already a TA
				        								taTemp = (TA) person;
				        							}
				        							for (String[] lecture : taTemp.getClassesTaken()) {
				        								String[] currentLecture = manager.classes.get(index);
				        								if (lecture[1].substring(0,3).equalsIgnoreCase(currentLecture[1].substring(0,3))) {
				        									System.out.println("A student can't be a TA for a lecture they are taking!");
				        									System.out.print(lecture[1].substring(0,3));
				        									System.out.print(currentLecture[1].substring(0,3));
				        									isTaken = true;
				        								}
				        							}//not taking a lecture with same prefix, so continue
				        							if (!isTaken) {
				        								System.out.printf("TA found : %s\n", taTemp.getName());
					        							taTemp.labs.add(lab);
				        							}
				        							if (isStudent && !isTaken) {
								        				people.remove(index1);
								        				people.add(taTemp);
							        				}
				        						}
				        					}
				        				}//end of person:people
				        				
				        			}//end of is UniqueTa
				        		}//end of for lab:labs
				        		String[] currentLecture = manager.classes.get(index);
				        		System.out.printf("[%s/%s/%s] has been added!\n", currentLecture[0], currentLecture[1], currentLecture[2]);
				        	}
				        //}//done with all faculty attributes, now add to people
				        people.add(faculty);
				        //System.out.printf("Id: %d, type: %s", faculty.getUcfId(), faculty.getClass());
				    }//end of not is taught
				}//end of for crn : crns
			} else {	//not unique, the UCF ID already exists in the list, find it, will be checked to see updates in option 3
				for (Person person : people) {
				    if (person.getUcfId() == ucfId) {
				        // We found the person with the matching ucfId
				        if (person instanceof Faculty) {
				            Faculty facultyTemp = (Faculty) person;
				        	//faculty code after office, since we are working with a different faculty object than if
				            //we had a unique id, we have to reuse the code from before but with the facultyTemp
				            System.out.printf("Record found/Name %s\n", facultyTemp.name);
				            while (true) {
								System.out.println("Enter how many lectures: ");
								try {
									numberLectures = scanner.nextInt();
									if (numberLectures < 1) {
										System.out.println("Invalid Input, please enter a positive integer");
									}
									else {
										break;	//numberLectures is valid positive integer
									}
								} catch (InputMismatchException e){
									System.err.println("Invalid Input, please enter a positive integer");
							        scanner.next(); //consume the invalid input to prevent an infinite loop
								}
							}
				            scanner.nextLine();
							System.out.printf("Enter the %d CRNs of the lectures (space-separated):\n", numberLectures);
							String crnsStr = scanner.nextLine();
							String[] crns = crnsStr.split(" ");
							int beingTaught = 0;
							for (String crn : crns) {
							    for (Person person2 : people) {
							    	if (person2 instanceof Faculty) {
							    		Faculty faculty1 = (Faculty) person2;
							    		for (String[] facultyLecture : faculty1.getLecturesTaught()) {
							    			if(facultyLecture[0].equals(crn)) {
							    				System.out.println("This lecture is already being taught");
							    				beingTaught = 1;
							    			}
							    		}
							    	}
							    }
							    if (beingTaught == 0) {
							    	//find the course in classManager
								    int index = -1;
								    for(int i = 0; i < manager.classes.size(); i++) {
								    	String[] temp = manager.classes.get(i);
								    	if(temp[0].equals(crn)) {
								    		index = i;
								    		break;
								    	}
								    }
							        facultyTemp.lecturesTaught.add(manager.classes.get(index));
							        //for (String crnTemp: crns) {	//if lecture has no labs/ writes info to screen,
							        								//if lecture has labs, prints labs and returns crns of labs
							        	ArrayList<String[]> labs = ClassManager.getLectureInfoByCRN(crn);
							        	int taUcfId;
							        	String taName, taSupervisor, taDegreeSeeking;
							        	if (labs != null) {
							        		//do ask to enter the UCF id of the TA for each lab (a TA may do more than
							        		//one Lab). This may require entering a new TA to the system, and in this case, you need to
							        		//ask for the remaining information of the TA. Keep in mind that a TA can be a student
							        		for (String[] lab : labs) {
							        			TA ta = new TA();
							        			//scanner.next();
							        			while (true) {
							        				System.out.printf("Enter the TA's id for %s:\n", lab[0]);
							        				try {
							        					taUcfId = scanner.nextInt();
							        					if (taUcfId == facultyTemp.getUcfId()) {
							        						taUcfId = 1;	//setUcfId will not allow this
							        					}
							        					ta.setUcfId(taUcfId);
							        					break;
							        				} catch (InputMismatchException e) {
							        					System.err.println("Invalid input, please enter a 7 digit integer");
							        					scanner.next();
							        				}
							        			}	
							        			boolean isUniqueTa = Person.checkIfUnique(taUcfId, people);
							        			//if deleting a course makes a TA no longer TA'ing any courses, completely remove the TA
							        			//professor said that is fine to another student, when not unique, check that ta is not taking any class
							        			//with the same course prefix(not crn)
							        			if (isUniqueTa)	{//we are creating a new student as ta, no need to check for taking this class
							        				scanner.nextLine();
							        				System.out.println("Enter the name of TA: ");
							        				taName = scanner.nextLine();
							        				ta.setName(taName);
							        				System.out.println("Enter the TA's supervisor's name: ");
							        				taSupervisor = scanner.nextLine();
							        				ta.setAdvisor(taSupervisor);
							        				//instructions are unclear if supervisor must be a faculty or not
							        				//in example run, it obly asks for a name and does nothing with it, so i am only having it as a string	
							        				while(true) {
							        					System.out.println("Enter the degree seeking(MS or PhD): ");
							        					taDegreeSeeking = scanner.nextLine();
							        					if(taDegreeSeeking.toLowerCase().equals("ms") ||
							        						taDegreeSeeking.toLowerCase().equals("phd")) {	//taDegreeSeeking is valid
							        						ta.setExpectedDegree(taDegreeSeeking);
									        				break;
							        					} else {	//taDegreeSeeking is invalid
							        						System.out.println("Sorry, invalid degree seeking(must be ms or phd");
							        					}
							        				}//add ta to people and add lab to taLabs
							        				ta.labs.add(lab);
							        				people.add(ta);
							        				continue;
							        			}//end of isUniqueTa
							        			if(!isUniqueTa) {	//2 options, already a ta or already a student
							        				//already a ta, check that ta is not taking course with same prefix
							        				int index1 = 0;
							        				boolean isStudent = false;
				        							boolean isTaken = false;
							        				for (Person person1 : people) {
							        					if (person1.getUcfId() == taUcfId) {
							        						if (person1 instanceof Faculty) {
							        							System.out.println("Sorry that is the ucfId of a faculty member");
							        						} else {//check if taking lecture with that prefix
							        							TA taTemp;
							        							if(!(person1 instanceof TA)) {
							        								isStudent = true;
							        								Student studentTemp = (Student) person1;
							        								taTemp = new TA();
							        								taTemp.setName(studentTemp.getName());
							        								taTemp.setUcfId(studentTemp.getUcfId());
							        								taTemp.setType(studentTemp.getType());
							        								taTemp.setClassesTaken(studentTemp.getClassesTaken());
							        								//now remove the student from ArrayList since it will be added back as TA
							        								for (Person potentialStudent : people) {
							        									if (potentialStudent.getUcfId() == taUcfId) {
							        										break;
							        									}
							        									else {
							        										index1++;
							        									}
							        								}
							        							}
							        							else {
							        								taTemp = (TA) person1;
							        							}
							        							for (String[] lecture : taTemp.getClassesTaken()) {
							        								String[] currentLecture = manager.classes.get(index);
							        								if (lecture[1].substring(0,3).equalsIgnoreCase(currentLecture[1].substring(0,3))) {
							        									System.out.println("A student can't be a TA for a lecture they are taking!");
							        									isTaken = true;
							        								}
							        							}//not taking a lecture with same prefix, so continue
							        							if (!isTaken) {
							        								System.out.printf("TA found: %s\n", taTemp.getName());
								        							taTemp.labs.add(lab);
							        							}
							        							if (isStudent && !isTaken) {
											        				people.remove(index1);
											        				people.add(taTemp);
										        				}
							        						}
							        					}
							        				}//end of person:people
							        			}//end of is UniqueTa
							        		}//end of for lab:labs
							        		String[] currentLecture = manager.classes.get(index);
							        		System.out.printf("[%s/%s/%s] has been added!\n", currentLecture[0], currentLecture[1], currentLecture[2]);
							        	}
							        }//faculty is updated
							    }
							}//end of if instanceof faculty
				        //below is end of if person.getUcfId = ucfId
				         else {	//either a student or TA has the entered ID, do not allow
				            System.out.println("Sorry, but that id belongs to a student or TA");
				        }
				        break;
				    }
				}
			}}
		
		else if (choice == 2) {	//enroll a student to a lecture
			Student student = new Student();
			String type = "";
			String crn;
			while (true) {
			System.out.print("Enter UCF id: ");
			try {
		        ucfId = scanner.nextInt();			
		        student.setUcfId(ucfId);
		        break;	//user entered integer(checked by try catch) and it was 7 digits(checked by setucfUd)
		        		//still need to check if id is unique
		    } catch (InputMismatchException e) {
		        System.err.println("Invalid Input, please enter a 7 digit integer");
		        scanner.next(); //consume the invalid input to prevent an infinite loop
		    }
			}
			boolean isUnique = Person.checkIfUnique(ucfId, people);
			if (isUnique) {	//new student, no need to check if they are TA for class with this prefix
				scanner.nextLine();
				System.out.println("Enter name: ");
				name = scanner.nextLine();
				student.setName(name);
				while (true) {
					System.out.println("Is this a graduate student? (y/n): ");
					type = scanner.nextLine().toLowerCase();
					if (type.equalsIgnoreCase("y")) {
						type = "graduate";
						break;
					}
					else if (type.equalsIgnoreCase("n")) {
						type = "undergraduate";
						break;
					}
					else {
						System.out.println("Invalid input. Please enter y or n");
					}
				}
				student.setType(type);
				//assign lecture, crns are assumed to be correct as stated in assignment
				//scanner.nextLine();
				System.out.printf("Which lecture to enroll [%s] in or enter 0 for no lectures: \n", student.getName());
				crn = scanner.nextLine();
				if (crn.equals("0")) {
					System.out.println("Student enrolled to no lectures");
					people.add(student);
				}
				else {
					int index = -1;	//will crash program if crn entered is incorrect, but assignment says assume crns are correct
				    for(int i = 0; i < manager.classes.size(); i++) {
				    	String[] temp = manager.classes.get(i);
				    	if(temp[0].equals(crn)) {
				    		index = i;
				    		break;
				    	}
				    }
				    student.classesTaken.add(manager.classes.get(index));
				    ArrayList<String[]> labs = ClassManager.getLectureInfoByCRN(crn);
				    if (labs != null) {
				    	Random rand = new Random();
				    	int randomIndex= rand.nextInt(labs.size());
				    	String[] selectedLab = labs.get(randomIndex);
				    	student.classesTaken.add(selectedLab);
				    	System.out.printf("[%s] is added to lab : %s\n", student.getName(), selectedLab[0]);
				    }//end of setting lab 
				    people.add(student);
				}	
			}//end of is unique
			if (!isUnique) {
				for (Person person : people) {
					if (person.getUcfId() == ucfId) {
						if (person instanceof Faculty) {//do not allow this
							System.out.println("Sorry, that is the Id of a Faculty");
						}
						else {//we have a student or ta
							if (person instanceof TA) {	//we have a TA
								TA studentTemp = (TA) person;
								System.out.printf("Record found/Name: %s\n", studentTemp.getName());
								scanner.nextLine();
								System.out.printf("Which lecture to enroll [%s] in or enter 0 for no lectures: \n", studentTemp.getName());
								crn = scanner.nextLine();
								if (crn.equals("0")) {
									System.out.println("Student enrolled to no lectures");
								}
								else {//need to check if ta instructs lab for course prefix or if ta is taking course prefix
									String labPrefix = "";
									boolean takingClass = false;
									for (String[] lab : studentTemp.getLabs()) {
										labPrefix = lab[2];
										for (String[] lecture : manager.classes) {
											if (lecture[0].equalsIgnoreCase(crn)) {
												if(labPrefix.equalsIgnoreCase(lecture[1].substring(0,3))) {
													takingClass = true;
												}
											}
										}
									}//end of lab:getLabs
									//now check their current course
									for (String[] studentLecture : studentTemp.getClassesTaken()) {
										String studentLecturePrefix = studentLecture[1].substring(0,3);
										for (String[] lecture : manager.getClasses()) {
											//int lectureIndex = 0;
											if (lecture[0].equalsIgnoreCase(crn)) {
												if(lecture[1].substring(0,3).equalsIgnoreCase(studentLecturePrefix)) {
													//we have a matching prefix, do not allow this
													takingClass = true;
												}
											}
										}//end of lecture:manager.getClasses
									}//end of studentLecture:getClassesTaken
									if (takingClass) {
										System.out.println("Sorry, but this TA is either currently enrolled in a course with this prefix "
												+ "or is teaching a lab with this prefix and therefor cannot enroll");
									}
									else if (!takingClass) {//class is being added
										int index = -1;	//will crash program if crn entered is incorrect, but assignment says assume crns are correct
									    for(int i = 0; i < manager.classes.size(); i++) {
									    	String[] temp = manager.classes.get(i);
									    	if(temp[0].equals(crn)) {
									    		index = i;
									    		break;
									    	}
									    }
									    studentTemp.classesTaken.add(manager.classes.get(index));
									    ArrayList<String[]> labs = ClassManager.getLectureInfoByCRN(crn);
									    if (labs != null) {
									    	Random rand = new Random();
									    	int randomIndex= rand.nextInt(labs.size());
									    	String[] selectedLab = labs.get(randomIndex);
									    	studentTemp.classesTaken.add(selectedLab);
									    	System.out.printf("[%s] is added to lab : %s\n", studentTemp.getName(), selectedLab[0]);
									    }//end of setting lab 
									}//end of !takingClass
								}//end of checking ta
							}//end of ta
							else if (person instanceof Student) {//we have a student
								boolean takingClass = false;
								Student studentTemp = (Student) person;
								System.out.printf("Record found/Name: %s\n", studentTemp.getName());
								scanner.nextLine();
								System.out.printf("Which lecture to enroll [%s] in or enter 0 for no lectures: \n", student.getName());
								crn = scanner.nextLine();
								if (crn.equals("0")) {	//no lectures, no need to check
									System.out.println("Student enrolled to no lectures");
								}
								else {//make sure student is not taking course of same prefix
									for (String[] studentLecture : studentTemp.getClassesTaken()) {
										String studentLecturePrefix = studentLecture[1].substring(0,3);
										for (String[] lecture : manager.getClasses()) {
											if (lecture[0].equalsIgnoreCase(crn)) {
												if(lecture[1].substring(0,3).equalsIgnoreCase(studentLecturePrefix)) {
													//we have a matching prefix, do not allow this
													takingClass = true;
												}
											}
										}//end of lecture:manager.getClasses
									}//end of studentLecture:getClassesTaken
									if (takingClass) {
										System.out.println("Sorry, but this student is currently enrolled in a course with this prefix "
												+ "and therefor cannot enroll");
									}
									else if (!takingClass) {//class is being added
										int index = -1;	//will crash program if crn entered is incorrect, but assignment says assume crns are correct
									    for(int i = 0; i < manager.classes.size(); i++) {
									    	String[] temp = manager.classes.get(i);
									    	if(temp[0].equals(crn)) {
									    		index = i;
									    		break;
									    	}
									    }
									    studentTemp.classesTaken.add(manager.classes.get(index));
									    ArrayList<String[]> labs = ClassManager.getLectureInfoByCRN(crn);
									    if (labs != null) {
									    	Random rand = new Random();
									    	int randomIndex= rand.nextInt(labs.size());
									    	String[] selectedLab = labs.get(randomIndex);
									    	studentTemp.classesTaken.add(selectedLab);
									    	System.out.printf("[%s] is added to lab : %s\n", studentTemp.getName(), selectedLab[0]);
									    }//end of setting lab 
									}//end of !takingClass
								}//end of checking if student is taking course with same prefix
							}//end of student
						}//end of person is student/ta
					}
				}//end of person:people
			}//end of !isUnique
		}
		else if (choice == 3) {	//print the schedule of a faculty
			int ucfId1;
			Faculty faculty = new Faculty();
			while (true) {
				System.out.print("Enter the UCF id: ");
				try {
			        ucfId1 = scanner.nextInt();			
			        faculty.setUcfId(ucfId1);
			        break;	//user entered integer(checked by try catch) and it was 7 digits(checked by setucfUd)
			        		//still need to check if id is unique
			    } catch (InputMismatchException e) {
			        System.err.println("Invalid Input, please enter a 7 digit integer");
			        scanner.next(); //consume the invalid input to prevent an infinite loop
			    }
				}
			boolean isUnique = Person.checkIfUnique(ucfId1, people);
			if (isUnique) {	//does not exist
				System.out.println("Sorry, id not found");
			}
			else if (!isUnique) {	//does exist
				for (Person person : people) {
					if(person.getUcfId() == ucfId1) {
						if(person instanceof Faculty) {
							System.out.print(person);
						}
						else {//not a faculty
							System.out.println("Sorry, this id does not belong to a faculty.");
						}
					}
				}
			}
		}
		else if (choice == 4) {	//print the schedule of an TA
			int ucfId1;
			TA ta = new TA();
			while (true) {
				System.out.print("Enter the UCF id: ");
				try {
			        ucfId1 = scanner.nextInt();			
			        ta.setUcfId(ucfId1);
			        break;	//user entered integer(checked by try catch) and it was 7 digits(checked by setucfUd)
			        		//still need to check if id is unique
			    } catch (InputMismatchException e) {
			        System.err.println("Invalid Input, please enter a 7 digit integer");
			        scanner.next(); //consume the invalid input to prevent an infinite loop
			    }
				}
			boolean isUnique = Person.checkIfUnique(ucfId1, people);
			if (isUnique) {	//does not exist
				System.out.println("Sorry, id not found");
			}
			else if (!isUnique) {	//does exist
				for (Person person : people) {
					if(person.getUcfId() == ucfId1) {
						if(person instanceof Faculty) {
							System.out.println("Sorry, this id belongs to a faculty.");
						}
						else if (person instanceof TA) {
							System.out.print(person);
						}
						else {	//we have a student
							System.out.println("Sorry, this id belongs to a student.");
						}
					}
				}
			}
		}
		else if (choice == 5) {	//print the schedule of a student
			int ucfId1;
			Student student = new Student();
			while (true) {
				System.out.print("Enter the UCF id: ");
				try {
			        ucfId1 = scanner.nextInt();			
			        student.setUcfId(ucfId1);
			        break;	//user entered integer(checked by try catch) and it was 7 digits(checked by setucfUd)
			        		//still need to check if id is unique
			    } catch (InputMismatchException e) {
			        System.err.println("Invalid Input, please enter a 7 digit integer");
			        scanner.next(); //consume the invalid input to prevent an infinite loop
			    }
				}
			boolean isUnique = Person.checkIfUnique(ucfId1, people);
			if (isUnique) {	//does not exist
				System.out.println("Sorry, id not found");
			}
			else if (!isUnique) {	//does exist
				for (Person person : people) {
					if(person.getUcfId() == ucfId1) {
						if(person instanceof Faculty) {
							System.out.println("Sorry, this id belongs to a faculty.");
						}
						else if (person instanceof TA) {
							System.out.println("Sorry, this id belongs to a TA");
						}
						else {//we have a student
							System.out.print(person);
						}
					}
				}
			}
		}
		else if (choice == 6) {	//delete a lecture
			System.out.println("Enter the CRN of the lecture you want to delete: ");
			Scanner cScan = new Scanner(System.in);
			String delCRN;
			while(true) {
				try {
					delCRN = cScan.nextLine();
					break;
				} catch(Exception e) {
					System.out.println("Not a valid CRN");				
			}			
			}
			deletedCrns.add(Integer.parseInt(delCRN));
			for(String[] delClass : ClassManager.classes) {
				if(delClass[0].equals(delCRN)) {
					ClassManager.classes.remove(delClass);
				}
			}
			
			
			for(Person person : people) {
				if(person instanceof Student) {
					Student tmp = (Student)person;
					for(String[] lecture : tmp.getClassesTaken()) {
						if(lecture[0].equals(delCRN)) {
							tmp.classesTaken.remove(lecture);
						}
					}
				}
				else if(person instanceof Faculty) {
					Faculty ftmp = (Faculty)person;
					for(String[] lecture : ftmp.lecturesTaught) {
						if(lecture[0].equals(delCRN)) {
							ftmp.lecturesTaught.remove(lecture);
						}
					}
					
					
				}
				
			}
			delete = true;
			
		}
		else if (choice == 7) {	//exit
			if(delete){
				System.out.println("You have made a deletion of at least one lecture. Would you like to print the copy of lec.txt?\nEnter y/Y for Yes or n/N for No: ");
				String answer = scanner.nextLine();
				answer = answer.toLowerCase();

				while(!answer.equals("y") && !answer.equals("n")){
					System.out.println("Is that a yes or no? Enter y/Y for Yes of n/N for No:");
					answer = scanner.nextLine();
					answer = answer.toLowerCase();
				}

				if(answer.equals("n")){
					System.out.println("Bye");
					System.exit(0);
				}

				else if(answer.equals("y")){
					for(int crn: deletedCrns){
						DeleteLectureByCRN.deleteLectureByCRN("lec.txt", crn);
					}
					System.out.println("lec.txt is updated\nBye");
				}
				
			}
			else{
				System.out.println("You have not deleted any lectures\nBye");
			}
		}
		}
	}
}

class ClassManager {
    
    public static ArrayList<String[]> classes;
    
    public ClassManager() {
        classes = new ArrayList<String[]>();
    }
    
    public void readClasses(String filename) {
    	while(true) {
	        try {
	            BufferedReader reader = new BufferedReader(new FileReader(filename));
	            String line = reader.readLine();
	            while (line != null) {
	                String[] parts = line.split(",");
	                classes.add(parts);
	                line = reader.readLine();
	            }
	            reader.close();
	            break; //break out of loop since our file was correct
	        } catch (IOException e) {
	            System.err.println("Sorry, no such file. Please Enter again: ");
	            Scanner scanner = new Scanner(System.in);
                filename = scanner.nextLine();
	        }
    	}
    }
    public static ArrayList<String[]> getLectureInfoByCRN(String crn) {
    	int i = 0;  //stores index of lecture
        for (String[] lecture : classes) {
            if (lecture[0].equals(crn)) {
            	String[] currentLecture = classes.get(i);
	            if (lecture.length > 6 && lecture[6].equalsIgnoreCase("yes")) {
	                // check if there is a lab associated with the lecture
	            	 ArrayList<String[]> labs = new ArrayList<>();
	            	 int j = i + 1;	//j is the index of potential labs(labs have length of 2)
	            	 String[] currentLab = classes.get(j);
	            	 while (currentLab.length < 3) {
	            		 currentLab = classes.get(j);
	            		 String[] labWithCrn = Arrays.copyOf(currentLab, currentLab.length + 1);
	            		 labWithCrn[currentLab.length] = lecture[1].substring(0,3);	//adds a crn to the lab for later use
	            		 labs.add(labWithCrn);
	            		 j++;
	            		 currentLab = classes.get(j);
	            	 }
	            	 if (labs.isEmpty()) {
	                     System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] Added!");
	            	 } else {
	                     System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] has these labs:");
	                     for (String[] lab : labs) {
	                         System.out.println(lab[0] + "," + lab[1]);
	                     }
	                     return labs;
	                 }
	             } else {
	                 System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] Added!");
	             }
	             return null;
	         }
            i++;
	     }
	     System.out.println("Lecture not found.");
	     return null;	
    }
    public static ArrayList<String[]> getLectureInfoByCRNWithoutPrint(String crn) {
    	int i = 0;  //stores index of lecture
        for (String[] lecture : classes) {
            if (lecture[0].equals(crn)) {
            	String[] currentLecture = classes.get(i);
	            if (lecture.length > 6 && lecture[6].equalsIgnoreCase("yes")) {
	                // check if there is a lab associated with the lecture
	            	 ArrayList<String[]> labs = new ArrayList<>();
	            	 int j = i + 1;	//j is the index of potential labs(labs have length of 2)
	            	 String[] currentLab = classes.get(j);
	            	 while (currentLab.length < 3) {
	            		 currentLab = classes.get(j);
	            		 String[] labWithCrn = Arrays.copyOf(currentLab, currentLab.length + 1);
	            		 labWithCrn[currentLab.length] = lecture[1].substring(0,3);	//adds a crn to the lab for later use
	            		 labs.add(labWithCrn);
	            		 j++;
	            		 currentLab = classes.get(j);
	            	 }
	            	 if (labs.isEmpty()) {
	                     //System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] Added!");
	            	 } else {
	                     //System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] has these labs:");
	                     for (String[] lab : labs) {
	                         //System.out.println(lab[0] + "," + lab[1]);
	                     }
	                     return labs;
	                 }
	             } else {
	                 //System.out.println("[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "] Added!");
	             }
	             return null;
	         }
            i++;
	     }
	     //System.out.println("Lecture not found.");
	     return null;	
    }
    
    
	public static ArrayList<String[]> getClasses() {
		return classes;
	}
	public static void setClasses(ArrayList<String[]> classes) {
		ClassManager.classes = classes;
	}
}
abstract class Person {
    public String name;
	private int ucfId;

    public Person(String name, int ucfId) {
        this.name = name;
        setUcfId(ucfId);
    }
    
    public void setUcfId(int ucfId) {
    	while (true) {
        if (String.valueOf(ucfId).length() != 7) {
            try {
                throw new IdException("Sorry incorrect format or Id already in use. (Ids are 7 digits and unique)");
            } catch (IdException e) {
                System.out.println(e.getMessage());
                while (true) {
	                System.out.println("Enter UCF id: ");
	                Scanner scanner = new Scanner(System.in);
	                try {
	                	ucfId = scanner.nextInt();
	                	break;
	                } catch (InputMismatchException d) {
	                	System.err.println("Invalid Input, please enter a 7 digit integer");
	    		        scanner.next(); //consume the invalid input to prevent an infinite loop
	                }
                }
                
            }
        } else {
            this.ucfId = ucfId;
            break;
          }
        }
    }
    public static boolean checkIfUnique(int ucfId, ArrayList<Person> people) {
    	 boolean isUnique = true;
    		for (Person person : people) {
    		    if (person.getUcfId() == ucfId) {
    		        isUnique = false;
    		        return isUnique;
    		    }
    		}	//out of for loop, id is unique
    		return isUnique;
    }
   
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUcfId() {
		return ucfId;
	}
	@Override
	public String toString() {
		return name;
	}
}

class Faculty extends Person {
	private String rank;
    private String officeLocation;
    public ArrayList<String[]> lecturesTaught;

    public Faculty(String name, int ucfId, String rank, String officeLocation, ArrayList<String[]> lecturesTaught) {
        super(name, ucfId);
        this.rank = rank;
        this.officeLocation = officeLocation;
        this.lecturesTaught = lecturesTaught;
    }
    public Faculty() {
    	super("", 1111111);
        this.rank = "";
        this.officeLocation = "";
        this.lecturesTaught = new ArrayList<String[]>();
    }

	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getOfficeLocation() {
		return officeLocation;
	}
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}
	public ArrayList<String[]> getLecturesTaught() {
		return lecturesTaught;
	}
	public void setLecturesTaught(ArrayList<String[]> lecturesTaught) {
		this.lecturesTaught = lecturesTaught;
	}
	@Override
	public String toString() {
		String result = super.toString() + " is teaching the following lectures: \n";
		for (String[] lecture : lecturesTaught) {
			result += "[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "][" + lecture[4] + "]\n";
			ArrayList<String[]> labs = ClassManager.getLectureInfoByCRNWithoutPrint(lecture[0]);
			if (labs != null) {
				result += "with Labs: \n";
				for (String[] lab : labs) {
					result += "[" + lab[0] + "/" + lab[1] + "]\n";
				}
			}
		}
	return result;
	}
}
class Student extends Person {
    private String type;	//either graduate or undergraduate for students
    public ArrayList<String[]> classesTaken;					

    public Student(String name, int ucfId, String type, ArrayList<String[]> classesTaken) {
        super(name, ucfId);
        this.type = type;
        this.classesTaken = classesTaken;
    }
    public Student() {
    	super("", 2222222);
        this.type = "undergraduate";
        this.classesTaken = new ArrayList<String[]>();
    }
    

    
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String[]> getClassesTaken() {
		return classesTaken;
	}

	
	public void setClassesTaken(ArrayList<String[]> classesTaken) {
		this.classesTaken = classesTaken;
	}
	@Override
	public String toString() {
		String result = super.toString() + " is enrolled in the following lectures: ";
		for (String[] lecture : classesTaken) {
			if (lecture.length > 4) { //we have a lecture
				result += "\n[" + lecture[0] + "/" + lecture[1] + "/" + lecture[2] + "]";
				if (lecture.length > 5 && lecture[6].equalsIgnoreCase("yes")) {	//lecture has a lab
					System.out.println("1");
					for (String[] potentialLab : classesTaken) {//loop again to find a lab
						System.out.print(potentialLab[2]);
						if (potentialLab[2].equalsIgnoreCase(lecture[1].substring(0,3)) && potentialLab.length < 4) {
							//we have a matching lab
							System.out.println("3");
							result += "/[Lab: " + potentialLab[0] + "]";
						}	
					}	
				}
			}
		}
		result += "\n";
		return result;
	}
}
class TA extends Student {
    private String advisor;
    private String expectedDegree;
    public ArrayList<String[]> labs;

    public TA(String name, int ucfId, String advisor, String type, ArrayList<String[]> classesTaken) {
        super(name, ucfId, type, classesTaken);
        this.advisor = advisor;
    }
    public TA() {
    	super("", 3333333, "graduate", new ArrayList<String[]>());
    	this.advisor = "";
    	this.expectedDegree = "";
    	this.labs = new ArrayList<String[]>();
    }
	public String getAdvisor() {
		return advisor;
	}
	public void setAdvisor(String taSupervisor) {
		this.advisor = taSupervisor;
	}
	public String getExpectedDegree() {
		return expectedDegree;
	}
	public void setExpectedDegree(String expectedDegree) {
		this.expectedDegree = expectedDegree;
	}
	public ArrayList<String[]> getLabs() {
		return labs;
	}
	public void setLabs(ArrayList<String[]> labs) {
		this.labs = labs;
	}
	@Override
	public String toString() {
		String result = super.toString();	//super gives us their name and their classes enrolled in
		result += name + " also instructs the following labs: \n";
		for (String[] lab : labs) {
			result += "[" + lab[0] + "/" + lab[1] + "]\n";
		}
		return result;
	}
}
class IdException extends Exception {
    public IdException(String message) {
        super(message);
    }
}
class DeleteLectureByCRN{
	public static void deleteLectureByCRN(String filename, int crn) {
    	boolean deleteFlag = false;
    	try {
        	BufferedReader reader = new BufferedReader(new FileReader(filename));
        	StringBuilder sb = new StringBuilder();
        	String line;
        	while ((line = reader.readLine()) != null) {
            	String[] fields = line.split(",");
            	if (fields.length >= 5 && Integer.parseInt(fields[0]) == crn) {
                	deleteFlag = true;
            	} else if (fields.length >= 5 && deleteFlag) {
                	deleteFlag = false;
                	sb.append(line).append(System.lineSeparator());
            	} else if (!deleteFlag) {
                	sb.append(line).append(System.lineSeparator());
            	}
        	}
        	reader.close();
        	BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
        	writer.write(sb.toString());
        	writer.close();
    	} catch (IOException e) {
        	e.printStackTrace();
    	}
	}

}

