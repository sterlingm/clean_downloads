import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;


class CleanDownloads 
{

	public static void main(String[] args) throws Exception
	{
		System.out.println("Hello world");

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
		ArrayList<File> oldFiles = new ArrayList<File>();
		ArrayList<File> notOldFiles = new ArrayList<File>();


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

        	System.out.println("File: "+file.getName()+" Last access time: "+fatr.lastAccessTime().toMillis()+" Diff: "+diff);        
		}

		System.out.println("oldFiles.size(): "+oldFiles.size());
		for(File file : oldFiles)
		{
			System.out.println("File: "+file.getName());
		}

		System.out.println("notOldFiles.size(): "+notOldFiles.size());
		for(File file : notOldFiles)
		{
			System.out.println("File: "+file.getName());
		}

	}

}