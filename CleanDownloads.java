
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import javax.swing.*;


class CleanDownloads extends JFrame
{

    private static ArrayList<File> oldFiles;
    private static ArrayList<File> toDelete;
    private static ArrayList<JCheckBox> checkBoxes;
    
    // debugging
    private static ArrayList<File> notOldFiles;
    
    private JPanel panel;
    private JScrollPane scrollPane;
    
    public CleanDownloads() throws IOException
    {
        populateFileLists();
        setTitle("Clean Downloads");
        
        // Create the panel
        panel = new JPanel();
        panel.setMinimumSize(new Dimension(450,400));
        panel.setBackground(Color.DARK_GRAY);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        
        add(panel);
        
        // Create gui components
        createCheckBoxes();
        createScrollPane();
    }
    
    public void createCheckBoxes()
    {
        checkBoxes = new ArrayList<>();
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints positionConst = new GridBagConstraints();
        
        positionConst.anchor = GridBagConstraints.WEST;
        
        for(int i=0;i<oldFiles.size();i++)
        {
            JCheckBox temp = new JCheckBox(oldFiles.get(i).getName());
            temp.setBackground(Color.DARK_GRAY);
            temp.setForeground(Color.CYAN);
            temp.setAlignmentX(LEFT_ALIGNMENT);
            temp.setHorizontalTextPosition(SwingConstants.LEFT);
            
            positionConst.gridx = 0;
            positionConst.gridy = i;
            positionConst.insets = new Insets(10,10,10,10);
        
            panel.add(temp, positionConst);
        }
        
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
    
    public void populateFileLists() throws IOException
    {
        // Get number of days in milliseconds and current time in ms
        int numDays = 30;
        long timeThresholdInMs = numDays * 86400000L;
        long currentTime = System.currentTimeMillis();

        // Get file path
        String home = System.getProperty("user.home");
        String downloadsPath = home+"\\Downloads";
        System.out.println(downloadsPath);

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

            if(diff > timeThresholdInMs)
            {
                    oldFiles.add(file);
            }
            else
            {
                    notOldFiles.add(file);
            }
            //System.out.println("File: "+file.getName()+" Last access time: "+fatr.lastAccessTime().toMillis()+" Diff: "+diff);        
        }

        /*System.out.println("oldFiles.size(): "+oldFiles.size());
        for(File file : oldFiles)
        {
                System.out.println("File: "+file.getName());
        }*/
        
        /*System.out.println("notOldFiles.size(): "+notOldFiles.size());
        for(File file : notOldFiles)
        {
                System.out.println("File: "+file.getName());
        }*/
    }
    
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("Hello world");

        
        // Create GUI and start code
        CleanDownloads myGUI = new CleanDownloads();
        myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myGUI.pack();
        myGUI.setVisible(true);
        
    }   // end main
}