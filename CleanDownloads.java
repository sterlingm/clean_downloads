
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import javax.swing.*;
import java.lang.System;
import java.io.BufferedWriter;
import java.util.Scanner;


class CleanDownloads extends JFrame
{

    private static final long serialVersionUID = 1L;

    // ArrayLists to hold files
	private static ArrayList<File> oldFiles;
    private static ArrayList<File> toDelete;
    private static ArrayList<String> notToDelete;
    private static ArrayList<JCheckBox> checkBoxes;
    private static int numDaysThreshold = 30;
    private static boolean oneTime = false;
    private static boolean checkNotToDelete = true;

    
    // debugging
    private static ArrayList<File> notOldFiles;
    
    // GUI members
    private JPanel panel;
    private JScrollPane scrollPane;
    private JButton deleteButton;
    private JLabel instructionsLabel;
    
    public CleanDownloads() throws IOException
    {
        setTitle("Clean Downloads");
        
        // Create the panel
        panel = new JPanel();
        panel.setMinimumSize(new Dimension(600,400));
        panel.setBackground(Color.DARK_GRAY);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setLayout(new GridBagLayout());


        // Add the panel to the frame
        add(panel);

        // Set close operation
        setCloseOperation();

        // Create all the components
        createComponents(true);
    }

    public void createComponents(boolean checkNotDeleting)
    {
        createInstructionsLabel();
        createCheckBoxes();
        createScrollPane();
        createButton();
    }

    public void createInstructionsLabel()
    {
        // Label telling the user what to do.
        instructionsLabel = new JLabel("Select which files you would like to delete.");

        // Align the label
        // Don't know why the text isn't left-aligned. None of the line below have worked.
        // Maybe something with the panel layout?
        instructionsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        instructionsLabel.setAlignmentX(LEFT_ALIGNMENT);
        instructionsLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        instructionsLabel.setForeground(Color.CYAN);

        // Create a grid bag constraints
        GridBagConstraints positionConst = new GridBagConstraints();
        positionConst.gridx = 0;
        positionConst.gridy = 0;

        // Add the instructions label
        panel.add(instructionsLabel, positionConst);
    }

    // This sets the members numDaysThreshold and notToDelete
    public static void readNotDeletingFile()
    {
        notToDelete = new ArrayList<String>();
        try
        {
            File file = new File("not_deleting.txt");
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextInt())
            {
                numDaysThreshold = scanner.nextInt();
            }

            while(scanner.hasNextLine())
            {
                notToDelete.add(scanner.nextLine());
            }

            scanner.close();
        }
        catch(Exception e) {System.out.println("Something went wrong with the file not_deleting.txt.");}
    }

    public void setCloseOperation()
    {             
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                /*// Confirm closing
                if (JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to close this window?", "Close Window?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    System.exit(0);
                }*/

                try {
                    // Don't write to file if the 'one-time' option was specified
                    if(oneTime == false)
                    {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("not_deleting.txt"));
                    
                        writer.write(Integer.toString(numDaysThreshold)+"\n");
                        // Save unchecked files
                        for(int i=0;i<oldFiles.size();i++)
                        {
                            // Write the filename
                            String fileName = oldFiles.get(i).getName();
    
                            if(i == oldFiles.size()-1)
                            {
                                writer.write(fileName);
                            }
                            else
                            {
                                writer.write(fileName+"\n");
                            }                    
                        }   // end for
                        writer.close();
                    }                    
                }
            catch(Exception e) {System.out.println("Something went wrong with the file not_deleting.txt.");}
            }
        });
    }



    public void createButton()
    {
        // Instantiate object
        deleteButton = new JButton("Delete");

        // Create a position in the gui for the button
        GridBagConstraints positionConst = new GridBagConstraints();
        positionConst.gridx = 0;
        positionConst.gridy = checkBoxes.size()+1;

        // Add an action listener to the button
        deleteButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 

                // Collect all the checked filenames
                for(int i=0;i<checkBoxes.size();i++)
                {
                    if(checkBoxes.get(i).isSelected())
                    {
                        toDelete.add(oldFiles.get(i));
                        panel.remove(checkBoxes.get(i));
                    }
                }

                // Delete all the files that were checked
                deleteFiles();
            }   // end actionPerformed
          } );

        // Add the button to the jpanel
        panel.add(deleteButton, positionConst);
    }


    public void createCheckBoxes()
    {
        checkBoxes = new ArrayList<>();
        
        GridBagConstraints positionConst = new GridBagConstraints();
        
        positionConst.anchor = GridBagConstraints.WEST;
        
        for(int i=0;i<oldFiles.size();i++)
        {
            // Create a checkbox and add it to the panel
            JCheckBox temp = new JCheckBox(oldFiles.get(i).getName());
            temp.setBackground(Color.DARK_GRAY);
            temp.setForeground(Color.CYAN);
            temp.setAlignmentX(LEFT_ALIGNMENT);
            temp.setHorizontalTextPosition(SwingConstants.LEFT);
            
            positionConst.gridx = 0;
            positionConst.gridy = i+1;
            positionConst.insets = new Insets(10,10,10,10);
        
            panel.add(temp, positionConst);

            checkBoxes.add(temp);
        }   // end for
    }
    
    public void createScrollPane()
    {
        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(450,400));
        scrollPane.setAlignmentX(RIGHT_ALIGNMENT);
        add(scrollPane);
    }

    
    public static boolean isNotToDelete(File f)
    {
        for(String s : notToDelete)
        {
            if(s.equals(f.getName()))
            {
                return true;
            }
        }

        return false;
    }

    
    public static void populateFileLists(boolean checkNotDeleting) throws IOException
    {
        // Get number of days in milliseconds and current time in ms
        long timeThresholdInMs = numDaysThreshold * 86400000L;
        long currentTime = System.currentTimeMillis();

        // Get file path
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name");
        
        String downloadsPath = home;
        if(os.startsWith("Windows"))
        {
            downloadsPath += "\\Downloads";
        }
        else
        {
            downloadsPath += "/Downloads";
        }

        // Get list of files
        File folder = new File(downloadsPath);
        File[] allFiles = folder.listFiles();

        // Store the files
        oldFiles = new ArrayList<>();
        notOldFiles = new ArrayList<>();
        toDelete = new ArrayList<>();

        // For each file, compare last accessed time and current time
        for(File file : allFiles)
        {
            Path path = file.toPath();

            BasicFileAttributes fatr = Files.readAttributes(path,
            BasicFileAttributes.class);

            long diff = currentTime - fatr.lastAccessTime().toMillis();

            // Check the time it was last accessed
            // and check that it hasn't been ignored previously
            if(diff > timeThresholdInMs && (checkNotDeleting == false || isNotToDelete(file) == false))
            {
                oldFiles.add(file);
            }
            else
            {
                notOldFiles.add(file);
            }
        }   // end for each
    }

    public static void deleteFiles()
    {
        for(File f : toDelete)
        {
            f.delete();
        }

        toDelete.clear();
    }

    public static void displayHelpMessage()
    {
        System.out.println("\tUsage: java CleanDownloads [-aho] [-t numberOfDays]");
        System.out.println("\tCommand line args:");
        System.out.println("\t\t-a | --all: Display all old files instead of checking not_deleting.txt");
        System.out.println("\t\t-h | --help: Display this message.");
        System.out.println("\t\t-o | --one-time: Do not save unmarked files. This option will trigger the -a option.");
        System.out.println("\t\t-t <value> | --threshold <value>: Sets a new threshold value for marking a file for deletion. This option will trigger the -a option.");
    }
    
    
    public static void parseCmdLineAgs(String[] args)
    {
        // Show all files with cmd line arg --all or -a
        if(args.length > 0)
        {
            for(int i=0;i<args.length;i++)
            {
                // help
                if(args[0].equals("--h") || args[0].equals("-h"))
                {
                    displayHelpMessage();
                    break;
                }

                // all
                if(args[i].equals("--all") || args[i].equals("-a"))
                {
                    checkNotToDelete = false;
                }

                // threshold
                else if(args[i].equals("--threshold") || args[i].equals("-t"))
                {
                    if(args.length < i+2)
                    {
                        System.out.println("Missing threshold value. Enter an integer value.");
                    }
    
                    // Set the value
                    numDaysThreshold = Integer.parseInt(args[i+1]);
    
                    // If a threshold is specified, check all files again
                    checkNotToDelete = false;

                    i++;
                }

                // one-time
                else if(args[i].equals("-o") || args[i].equals("--one-time"))
                {
                    oneTime = true;
                }
            }   // end for      
            
            // Do this outside for loop in case -o was set before -t
            if(oneTime)
            {
                checkNotToDelete = false;
            }
        }   // end if
    }


    public static void main(String[] args) throws Exception
    {
        // Read the contents of not_deleting.txt
        // and set the ArrayList member notToDelete
        readNotDeletingFile();

        // Do this after reading not_deleting file because
        // the user may have specified a new threshold
        parseCmdLineAgs(args);        

        // Populate the ArrayList members that hold files
        populateFileLists(checkNotToDelete);

        // If there are files to delete, then create GUI and start main loop
        if(oldFiles.size() > 0)
        {
            CleanDownloads myGUI = new CleanDownloads();
            myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            myGUI.pack();
            myGUI.setVisible(true);
        }
    }   // end main
}