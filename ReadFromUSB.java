package ReadSignature;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class ReadFromUSB {

	 public static File  getFilePath()
	  {
		 Scanner c = new Scanner(System.in);
		 System.out.println("Give the USB name");
		 String USBname = c.nextLine();
	      System.out.println("File system roots returned by   FileSystemView.getFileSystemView():");
	      FileSystemView fsv = FileSystemView.getFileSystemView();
	      File[] roots = fsv.getRoots();
	      for (int i = 0; i < roots.length; i++)
	      {
	        System.out.println("Root: " + roots[i]);
	      }

	      System.out.println("Home directory: " + fsv.getHomeDirectory());

	      System.out.println("File system roots returned by File.listRoots():");

	      File[] f = File.listRoots();
	      for (int i = 0; i < f.length; i++)
	      {
	    	  if(fsv.getSystemDisplayName(f[i]).toLowerCase().contains(USBname.toLowerCase()))
	    	  {
	    	  System.out.println("I a into USB");
	    	  System.out.println("Readable: " + f[i].canRead()+" "+f[i].getAbsolutePath());
		      System.out.println("Writable: " + f[i].canWrite());
		      String path = f[i].getAbsolutePath()+"Form16\\Docsigntest.pfx";
		      System.out.println(path);
		     File pdfFile = new File(path);
		     /* if(Desktop.isDesktopSupported())
		      {
		    	  try {
					Desktop.getDesktop().open(pdfFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }*/
		      return pdfFile;
	    	  }
	        
	      
	      }
	      return new File("");
	      
	   }
}
