package com.xandrev.jdorg.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

	private Utils(){}
	
	/**
	 * Method to check the extensions to be available
	 * @param name
	 * @param extensions
	 * @return
	 */
	public static boolean checkExtensions(String name, String[] extensions) {
		if(extensions != null){
			for(int i=0;i<extensions.length;i++){
				if(name != null && name.endsWith(extensions[i])){
					return true;
				}
			}
		}
		return false;
	}

	

	public static String extractEpisodeFromFile(String subFile) {
            String finalSubFile;
            if(subFile != null){
                    finalSubFile = subFile.toUpperCase();
                    Pattern p = Pattern.compile("(.*)\\.(S[0-9][0-9]E[0-9][0-9]|[0-9]?[0-9]X[0-9]?[0-9]).*");
                    Matcher m = p.matcher(finalSubFile);
                    if(m.matches()){
                            return m.group(2);
                    }
            }
		return null;
	}

	public static String createFeatureClassName(String feature) {
		if(feature == null || feature.equals("")){
			return null;
		}
		return "com.xandrev.jdorg.features."+feature.toLowerCase()+"."+feature+"Feature";
	}
	
	

	public static List<String>listFeaturesAvailables(){
		
		String packageName = "com.xandrev.jdorg.features";
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    URL packageURL;
	    ArrayList<String> names = new ArrayList<String>();

	    packageName = packageName.replace(".", "/");
	    packageURL = classLoader.getResource(packageName);

	    
	        File folder = new File(packageURL.getFile());
	        File[] contenuti = folder.listFiles();
	        for(File actual: contenuti){
	        	if(actual.isDirectory()){
	        		String featureName = actual.getName();
	        		featureName = featureName.replaceAll("com.xandrev.jdorg.features.","");
	        		names.add(featureName);
	        	}
	        }
	    return names;
	}

	public static String toString(List<String> videoExtensions) {
		StringBuffer strBuffer = new StringBuffer(1024);
		for(int i=0;i<videoExtensions.size();i++){
			String str = videoExtensions.get(i);
			strBuffer.append(str);
			if( i + 1 != videoExtensions.size()){
				strBuffer.append(",");
			}
		}
		return strBuffer.toString();
	}

}
