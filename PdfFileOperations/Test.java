package PdfFileOperations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

import sun.security.pkcs11.SunPKCS11;

public class Test {
	public static void main(String args[]) throws IOException, GeneralSecurityException, DocumentException {
		// Create instance of SunPKCS11 provider

		String userFile = "C:\\Users\\arti jaiswal\\Desktop/Test.pdf";
		String userFile_signed = "C:\\Users\\arti jaiswal\\Desktop/Test_signed.pdf";

		String pkcs11Config = "name=eToken\nlibrary=C:\\Windows\\System32\\eps2003csp11.dll";
		//ByteArrayInputStream pkcs11ConfigStream = new java.io.ByteArrayInputStream(pkcs11Config.getBytes());
		SunPKCS11 providerPKCS11 = new SunPKCS11("E:\\JavaWorkSpace\\PDF_Sign\\src\\PdfFileOperations\\pkcs11.cfg");
		java.security.Security.addProvider(providerPKCS11);

		// Get provider KeyStore and login with PIN
		String pin = "12345678";
		java.security.KeyStore keyStore = java.security.KeyStore.getInstance("PKCS11", providerPKCS11);
		keyStore.load(null, pin.toCharArray());

		// Enumerate items (certificates and private keys) in the KeyStore
		java.util.Enumeration<String> aliases = keyStore.aliases();
		String alias = null;
		while (aliases.hasMoreElements()) {
			alias = aliases.nextElement();
			System.out.println(alias);
		}

		PrivateKey pk = (PrivateKey) keyStore.getKey(alias, "12345678".toCharArray());
		Certificate[] chain = keyStore.getCertificateChain(alias);
		@SuppressWarnings("deprecation")
		OcspClient ocspClient = new OcspClientBouncyCastle();
		TSAClient tsaClient = null;
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = (X509Certificate) chain[i];
			String tsaUrl = CertificateUtil.getTSAURL(cert);
			if (tsaUrl != null) {
				tsaClient = new TSAClientBouncyCastle(tsaUrl);
				break;
			}
		}
		List<CrlClient> crlList = new ArrayList<CrlClient>();
		crlList.add(new CrlClientOnline(chain));
		Test t = new Test();
		PDDocument doc = PDDocument.load(new File(userFile));
		int count = doc.getNumberOfPages();
		t.sign(userFile, userFile_signed, chain, pk, DigestAlgorithms.SHA256, providerPKCS11.getName(),
				CryptoStandard.CMS, "Test", "India", crlList, ocspClient, tsaClient, count);
	}

	public void sign(String src, String dest, Certificate[] chain, PrivateKey pk, String digestAlgorithm,
			String provider, CryptoStandard subfilter, String reason, String location, Collection<CrlClient> crlList,
			OcspClient ocspClient, TSAClient tsaClient, int estimatedSize)
			throws GeneralSecurityException, IOException, DocumentException {
		// Creating the reader and the stamper
		PdfReader reader = new PdfReader(src);
		FileOutputStream os = new FileOutputStream(dest);
		PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
		// Creating the appearance
		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
		appearance.setReason(reason);
		appearance.setLocation(location);
		appearance.setVisibleSignature(getRectangle(reader, estimatedSize), estimatedSize, "sig");
		// Creating the signature
		System.out.println("Creating the signature");
		ExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
		ExternalDigest digest = new BouncyCastleDigest();
		MakeSignature.signDetached(appearance, digest, pks, chain, crlList, ocspClient, tsaClient, 0, subfilter);
		System.out.println("PDF file is signed");
	}

	public Rectangle getRectangle(PdfReader reader, int index) {
		Rectangle cropBox = reader.getCropBox(index);
		float width = 200;
		float height = 100;
		Rectangle rectangle = null;
		// Top left
		rectangle = new Rectangle(cropBox.getLeft(), cropBox.getTop(height), cropBox.getLeft(width), cropBox.getTop());

		// Top right
		rectangle = new Rectangle(cropBox.getRight(width), cropBox.getTop(height), cropBox.getRight(),
				cropBox.getTop());

		// Bottom left
		rectangle = new Rectangle(cropBox.getLeft(), cropBox.getBottom(), cropBox.getLeft(width),
				cropBox.getBottom(height));

		// Bottom right
		rectangle = new Rectangle(cropBox.getRight(width), cropBox.getBottom(), cropBox.getRight(),
				cropBox.getBottom(height));

		return rectangle;
	}
}