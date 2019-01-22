package PdfFileOperations;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Calendar;

import javax.security.cert.Certificate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;


public class PdfSign {

	public static void main(String args[]){

		// Create a new document
		PDDocument pdDocument;
		try {
			File f =new File("C:\\Users\\arti jaiswal\\Downloads\\JAVA PROJECT.pdf");
			pdDocument = PDDocument.load(f);

			PDPageTree pages = (PDPageTree) pdDocument.getDocumentCatalog().getPages();
			int lastPage = pages.getCount();

			PDPage page = pages.get(lastPage - 1); 
		    PDSignature pdSignature = new PDSignature();
		    KeyStore ks= null;
		    java.io.FileInputStream fis = null;
			try {
				
					ks = KeyStore.getInstance(KeyStore.getDefaultType());
				
			 // get user password and file input stream
		    char[] password = new char[]{'a'};

		  
		   fis = new java.io.FileInputStream("E:\\JavaWorkSpace\\PDF_Sign\\bin\\PdfFileOperations\\idsrv3test.pfx");
			  ks.load(fis, password);
			  System.out.print(ks);
			  }
			 catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
		        if (fis != null) {
		            fis.close();
		        }
		    }
			pdSignature.setFilter(PDSignature.FILTER_VERISIGN_PPKVS);
			pdSignature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_SHA1);
			pdSignature.setName("KSCodes");
			pdSignature.setLocation("WFH");
			pdSignature.setReason("Sample Signature test");
			pdSignature.setSignDate(Calendar.getInstance());
			pdDocument.addSignature(pdSignature);
		    //Saving the document   
			pdDocument.save("E:\\arti_info\\Documents1.pdf");
			pdDocument.close();
			System.out.println("PDF saved to the location !!!");

		} catch (IOException ioe) {
			System.out.println("Error while saving pdf" + ioe.getMessage());
		}
	}

}
