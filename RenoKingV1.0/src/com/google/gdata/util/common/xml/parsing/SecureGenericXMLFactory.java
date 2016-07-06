/*     */ package com.google.gdata.util.common.xml.parsing;
/*     */ 
/*     */ import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SecureGenericXMLFactory
/*     */ {
	/*  43 */   private static final SecureEntityResolver NOOP_RESOLVER = new SecureEntityResolver();
	/*     */ 
	/*     */   public static SAXParserFactory getSAXParserFactory(SAXParserFactory factory)
			/*     */     throws ParserConfigurationException, SAXException
			/*     */   {
		/*  51 */     return new SecureSAXParserFactory(factory);
	/*     */   }
	/*     */ 
	/*     */   public static DocumentBuilderFactory getDocumentBuilderFactory(DocumentBuilderFactory factory)
	/*     */   {
		/*  56 */     return new SecureDocumentBuilderFactory(factory);
	/*     */   }
	/*     */ 
	/*     */   private static final class SecureEntityResolver
	/*     */     implements EntityResolver
	/*     */   {
		/*     */     public InputSource resolveEntity(String publicId, String systemId)
		/*     */     {
			/* 399 */       return new InputSource(new StringReader(""));
		/*     */     }
	/*     */   }
	/*     */ 
	/*     */   protected static class SecureDocumentBuilderFactory extends DocumentBuilderFactory
	/*     */   {
		/*     */     private DocumentBuilderFactory factory;
		/*     */ 
		/*     */     protected SecureDocumentBuilderFactory(DocumentBuilderFactory factory)
		/*     */     {
			/* 228 */       this.factory = factory;
			/*     */ 
			/* 231 */       factory.setValidating(false);
			/*     */       try
			/*     */       {
				/* 237 */         factory.setXIncludeAware(false);
			/*     */       }
			/*     */       catch (UnsupportedOperationException e)
			/*     */       {
			/*     */       }
			/*     */       catch (NoSuchMethodError e)
			/*     */       {
			/*     */       }
			/*     */ 
			/*     */       try
			/*     */       {
				/* 255 */         factory.setAttribute("http://xml.org/sax/features/external-general-entities", Boolean.valueOf(false));
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       try {
				/* 261 */         factory.setAttribute("http://xml.org/sax/features/external-parameter-entities", Boolean.valueOf(false));
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       try {
				/* 267 */         factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */ 
			/*     */       try
			/*     */       {
				/* 276 */         factory.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
		/*     */     }
		/*     */ 
		/*     */     public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException
		/*     */     {
			/* 285 */       DocumentBuilder docBuilder = this.factory.newDocumentBuilder();
			/* 286 */       docBuilder.setEntityResolver(SecureGenericXMLFactory.NOOP_RESOLVER);
			/* 287 */       return docBuilder;
		/*     */     }
		/*     */ 
		/*     */     public void setNamespaceAware(boolean awareness)
		/*     */     {
			/* 292 */       this.factory.setNamespaceAware(awareness);
		/*     */     }
		/*     */ 
		/*     */     public void setValidating(boolean validating)
		/*     */     {
			/* 297 */       this.factory.setValidating(validating);
		/*     */     }
		/*     */ 
		/*     */     public void setIgnoringElementContentWhitespace(boolean whitespace)
		/*     */     {
			/* 302 */       this.factory.setIgnoringElementContentWhitespace(whitespace);
		/*     */     }
		/*     */ 
		/*     */     public void setExpandEntityReferences(boolean expandEntityRef)
		/*     */     {
			/* 307 */       this.factory.setExpandEntityReferences(expandEntityRef);
		/*     */     }
		/*     */ 
		/*     */     public void setIgnoringComments(boolean ignoreComments)
		/*     */     {
			/* 312 */       this.factory.setIgnoringComments(ignoreComments);
		/*     */     }
		/*     */ 
		/*     */     public void setCoalescing(boolean coalescing)
		/*     */     {
			/* 317 */       this.factory.setCoalescing(coalescing);
		/*     */     }
		/*     */ 
		/*     */     public boolean isNamespaceAware()
		/*     */     {
			/* 322 */       return this.factory.isNamespaceAware();
		/*     */     }
		/*     */ 
		/*     */     public boolean isValidating()
		/*     */     {
			/* 327 */       return this.factory.isValidating();
		/*     */     }
		/*     */ 
		/*     */     public boolean isIgnoringElementContentWhitespace()
		/*     */     {
			/* 332 */       return this.factory.isIgnoringElementContentWhitespace();
		/*     */     }
		/*     */ 
		/*     */     public boolean isExpandEntityReferences()
		/*     */     {
			/* 337 */       return this.factory.isExpandEntityReferences();
		/*     */     }
		/*     */ 
		/*     */     public boolean isIgnoringComments()
		/*     */     {
			/* 342 */       return this.factory.isIgnoringComments();
		/*     */     }
		/*     */ 
		/*     */     public boolean isCoalescing()
		/*     */     {
			/* 347 */       return this.factory.isCoalescing();
		/*     */     }
		/*     */ 
		/*     */     public void setAttribute(String name, Object value) throws IllegalArgumentException
		/*     */     {
			/* 352 */       this.factory.setAttribute(name, value);
		/*     */     }
		/*     */ 
		/*     */     public Object getAttribute(String name) throws IllegalArgumentException
		/*     */     {
			/* 357 */       return this.factory.getAttribute(name);
		/*     */     }
		/*     */ 
		/*     */     public void setFeature(String name, boolean value) throws ParserConfigurationException
		/*     */     {
			/* 362 */       this.factory.setFeature(name, value);
		/*     */     }
		/*     */ 
		/*     */     public boolean getFeature(String name) throws ParserConfigurationException
		/*     */     {
			/* 367 */       return this.factory.getFeature(name);
		/*     */     }
		/*     */ 
		/*     */     public Schema getSchema() throws UnsupportedOperationException
		/*     */     {
			/* 372 */       return this.factory.getSchema();
		/*     */     }
		/*     */ 
		/*     */     public void setSchema(Schema schema) throws UnsupportedOperationException
		/*     */     {
			/* 377 */       this.factory.setSchema(schema);
		/*     */     }
		/*     */ 
		/*     */     public void setXIncludeAware(boolean state) throws UnsupportedOperationException
		/*     */     {
			/* 382 */       this.factory.setXIncludeAware(state);
		/*     */     }
		/*     */ 
		/*     */     public boolean isIncludeAware() throws UnsupportedOperationException {
			/* 386 */       return this.factory.isXIncludeAware();
		/*     */     }
	/*     */   }
	/*     */ 
	/*     */   protected static class SecureSAXParserFactory extends SAXParserFactory
	/*     */   {
		/*     */     private SAXParserFactory factory;
		/*     */ 
		/*     */     protected SecureSAXParserFactory(SAXParserFactory factory)
				/*     */       throws ParserConfigurationException, SAXException
				/*     */     {
			/*  79 */       this.factory = factory;							
			/*     */ 
			/*  82 */       factory.setValidating(false);
			/*     */       try
			/*     */       {
				/*  88 */         factory.setXIncludeAware(false);
			/*     */       }
			/*     */       catch (UnsupportedOperationException e)
			/*     */       {
			/*     */       }
			/*     */       catch (NoSuchMethodError e)
			/*     */       {
			/*     */       }
			
							if (true){
								return;
							}
			/*     */ 
			/*     */       try
			/*     */       {
				/* 106 */         factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       catch (SAXNotRecognizedException e)
			/*     */       {
			/*     */       }
			/*     */       try
			/*     */       {
				/* 116 */         factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       catch (SAXNotRecognizedException e) {
			/*     */       }
			/*     */       try {
				/* 124 */         factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       catch (SAXNotRecognizedException e)
			/*     */       {
			/*     */       }
			/*     */ 
			/*     */       try
			/*     */       {
				/* 135 */         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
			/*     */       }
			/*     */       catch (IllegalArgumentException e)
			/*     */       {
			/*     */       }
			/*     */       catch (SAXNotRecognizedException e)
			/*     */       {
			/*     */       }
		/*     */     }
		/*     */ 
		/*     */     public SAXParser newSAXParser() throws ParserConfigurationException, SAXException
		/*     */     {
			/* 147 */       SAXParser parser = this.factory.newSAXParser();
			/* 148 */       XMLReader xmlReader = parser.getXMLReader();
			/* 149 */       xmlReader.setEntityResolver(SecureGenericXMLFactory.NOOP_RESOLVER);
			/*     */ 
			/* 151 */       return parser;
		/*     */     }
		/*     */ 
		/*     */     public void setNamespaceAware(boolean awareness)
		/*     */     {
			/* 156 */       this.factory.setNamespaceAware(awareness);
		/*     */     }
		/*     */ 
		/*     */     public void setValidating(boolean validating)
		/*     */     {
			/* 161 */       this.factory.setValidating(validating);
		/*     */     }
		/*     */ 
		/*     */     public boolean isNamespaceAware()
		/*     */     {
			/* 166 */       return this.factory.isNamespaceAware();
		/*     */     }
		/*     */ 
		/*     */     public boolean isValidating()
		/*     */     {
			/* 171 */       return this.factory.isValidating();
		/*     */     }
		/*     */ 
		/*     */     public void setFeature(String name, boolean value)
				/*     */       throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
				/*     */     {
			/* 178 */       this.factory.setFeature(name, value);
		/*     */     }
		/*     */ 
		/*     */     public boolean getFeature(String name)
				/*     */       throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
				/*     */     {
			/* 185 */       return this.factory.getFeature(name);
		/*     */     }
		/*     */ 
		/*     */     public Schema getSchema() throws UnsupportedOperationException
		/*     */     {
			/* 190 */       return this.factory.getSchema();
		/*     */     }
		/*     */ 
		/*     */     public void setSchema(Schema schema) throws UnsupportedOperationException
		/*     */     {
			/* 195 */       this.factory.setSchema(schema);
		/*     */     }
		/*     */ 
		/*     */     public void setXIncludeAware(boolean state)
				/*     */       throws UnsupportedOperationException
				/*     */     {
			/* 201 */       this.factory.setXIncludeAware(state);
		/*     */     }
		/*     */ 
		/*     */     public boolean isXIncludeAware() throws UnsupportedOperationException
		/*     */     {
			/* 206 */       return this.factory.isXIncludeAware();
		/*     */     }
	/*     */   }
/*     */ }

/* Location:           G:\Samples\sss\gdata-core-1.0.jar
 * Qualified Name:     com.google.gdata.util.common.xml.parsing.SecureGenericXMLFactory
 * JD-Core Version:    0.6.0
 */