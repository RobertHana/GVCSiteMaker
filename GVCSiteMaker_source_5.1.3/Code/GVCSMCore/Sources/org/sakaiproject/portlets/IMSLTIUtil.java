package org.sakaiproject.portlets;

import java.util.Locale;
import java.util.UUID;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

/**
 * Some Utility code for IMS LTI
 * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
 */
public class IMSLTIUtil {

   	public static void main(String[] av)
   	{
		String testDates[] = {
			"2008-06-17T22:29:17Z",
			"2008-06-17T18:29:17-0400",
			"2008-06-17T22:29:17" }; // Will assume GMT
	
		Date first = parseISO8601(testDates[0]);
		System.out.println(testDates[0]+" -> "+first);
        	for(int i=1; i<testDates.length; i++) 
		{
			Date next = parseISO8601(testDates[i]);
			if ( next == null )
			{
				System.out.println(testDates[i]+" ***** Parse failed");
				continue;
			}
			System.out.println(testDates[i]+" -> "+next);
			if ( first.getTime() != next.getTime() )
			{
				System.out.println("Mismatch first="+first+" test="+testDates[i]+" new="+next);
			}
		}
   	}
	
	public static String BASE64SHA1(String text) 
	{
		System.out.println("BASE64SHA1 text="+text);
		String key = null;
		try
		{
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("utf-8"), 0, text.length());
			sha1hash = md.digest();
			key = new sun.misc.BASE64Encoder().encode(sha1hash);
		}
		catch(NoSuchAlgorithmException e)
		{
		}
		catch(UnsupportedEncodingException e)
		{
		}
		System.out.println("BASE64SHA1 returning key "+key);
		return key;
    	}

	/** Encode a plaintext string into Base64 using UTF-8
	 */
	public static String encodeBase64(String unencoded)
 	{
		if ( unencoded == null ) return null;
		try 
		{
			byte[] bytes = unencoded.getBytes("UTF8");
			String encoded = new sun.misc.BASE64Encoder().encode(bytes);
			System.out.println("enencoded="+unencoded+" encoded="+encoded);
			return encoded;
		}
		catch (UnsupportedEncodingException ex)
		{
			return null;
		}
	}

	// http://www.dynamicobjects.com/d2r/archives/003057.html
	public static SimpleDateFormat RFC822DATEFORMAT 
		= new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

	// Parse a subset of the ISO8601 dates - in preference order
	private static String outBoundISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";   // Assume GMT
 	private static String inBoundISO8601B = "yyyy-MM-dd'T'HH:mm:ssZ";  // Accept TimeZone offset
 	private static String inBoundISO8601C = "yyyy-MM-dd'T'HH:mm:ss";  // Assume GMT

	public static Date parseISO8601(String str)
	{
                SimpleDateFormat formatter = new SimpleDateFormat(outBoundISO8601);
                try {
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                	Date dt = formatter.parse(str);
                	// System.out.println("Outbound="+dt);
			return dt;
                } catch (Exception e) {
               		// Keep on trying 
                }
                formatter = new SimpleDateFormat(inBoundISO8601B);
                try {
			// This should have timezone for us to read
			formatter.setLenient(true);
                	Date dt = formatter.parse(str);
                	// System.out.println("Inbound B="+dt);
			return dt;
                } catch (Exception e) {
               		// Keep on trying 
                }
                formatter = new SimpleDateFormat(inBoundISO8601C);
                try {
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatter.setLenient(true);
                	Date dt = formatter.parse(str);
                	// System.out.println("Inbound C="+dt);
			return dt;
                } catch (Exception e) {
               		// Keep on trying 
                }
		return null;
	}

	public static String getDateAsRFC822String()
	{
		return getDateAsRFC822String(new Date());
        }
	public static String getDateAsRFC822String(Date date)
	{
		return RFC822DATEFORMAT.format(date);
	}

	/* 
	 * We are going to force our dates to GMT and stick a Z on the end
         *       (eg 1997-07-16T19:20:30Z)
         */
	public static String getDateAsISO8601String()
	{
		return getDateAsISO8601String(new Date());
        }

	public static String getDateAsISO8601String(Date date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(outBoundISO8601);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String result = formatter.format(date);
		return result;
	}

	// PasswordDigest = Base64 \ (SHA1 (Nonce + CreationTimestamp + Password))
	// X-WSSE: UsernameToken Username="ltitc", PasswordDigest="5BSxco0uWjGtYrTDaxgcUnEfviA=",  
	//      Nonce="13294281-1645-42c6-93a8-2f486cff2f7c", Created="2008-05-08T00:14:06-04:00"
        public static String getNonce()
	{
		return UUID.randomUUID().toString();
        }

        public static String getDigest(String nonce, String timestamp, String password)
        {
		String presha1 = nonce + timestamp + password;
		System.out.println("presha1="+presha1);
		String digest = IMSLTIUtil.BASE64SHA1(presha1);
		System.out.println("digest="+digest);
		return digest;
	}

        public static String getWSSEHeader(String password)
        {
		String nonce = IMSLTIUtil.getNonce();
		String timestamp = IMSLTIUtil.getDateAsISO8601String();
		String digest = IMSLTIUtil.getDigest(nonce, timestamp, password);
		String wsse = "UsernameToken Username=\"ltitc\", PasswordDigest=\""+digest+
			"\",  Nonce=\""+nonce+"\", Created=\""+timestamp+"\"";
		System.out.println("wsse="+wsse);
		return wsse;
	}

}
