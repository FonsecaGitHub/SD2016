package pt.upa.utils;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

import java.math.BigInteger;


public class CertificateReader
{
        private Certificate _certificate;

        
        public CertificateReader(String path) throws Exception
        {
            _certificate = readCertificate(path);
        }
    
        public CertificateReader(byte[] certificate_bytes) throws Exception
        {
            _certificate = readCertificate(certificate_bytes);
        }
        
        
        public BigInteger getPublicKeyEncoded() throws Exception
	{   
            PublicKey pkey = _certificate.getPublicKey();
            
            return new BigInteger(printHexBinary(pkey.getEncoded()), 16);
	}
	
	public PublicKey getPublicKey() throws Exception
	{   
            return _certificate.getPublicKey();
        }

        //============= static private ========================================================
        /**
	 * Reads a certificate from a file.
	 * 
	 * @param certificate_filepath filepath to the certificate
	 * @return Certificate object or null.
	 * @throws Exception
	 */
	 private static Certificate readCertificate(String certificate_filepath) throws Exception
	 {
            BufferedInputStream buffered_instream = null;
            
            Certificate result = null;
            CertificateFactory cert_factory = CertificateFactory.getInstance("X.509");
	 
            try
            {
                buffered_instream = new BufferedInputStream(new FileInputStream(certificate_filepath));
            }
            catch(FileNotFoundException excep)
            {
                excep.printStackTrace();
                System.exit(-1);
            }
            
            int num_bytes_available_to_be_read = buffered_instream.available();
            
            if(num_bytes_available_to_be_read > 0)
            {
                result = cert_factory.generateCertificate(buffered_instream);
            }
            
            buffered_instream.close();
            
            return result;
	 }    
	 
	 
	 /**
	 * Reads a certificate from a byte array.
	 * 
	 * @param certificate_bytes bytes of the certificate
	 * @return Certificate object or null.
	 * @throws Exception
	 */
	 private static Certificate readCertificate(byte[] certificate_bytes) throws Exception
	 {
            BufferedInputStream buffered_instream = null;
            
            Certificate result = null;
            CertificateFactory cert_factory = CertificateFactory.getInstance("X.509");
	 

            buffered_instream = new BufferedInputStream(new ByteArrayInputStream(certificate_bytes));

            
            int num_bytes_available_to_be_read = buffered_instream.available();
            
            if(num_bytes_available_to_be_read > 0)
            {
                result = cert_factory.generateCertificate(buffered_instream);
            }
            
            buffered_instream.close();
            
            return result;
	 }

}