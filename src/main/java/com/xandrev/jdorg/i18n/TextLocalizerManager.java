package com.xandrev.jdorg.i18n;

import com.xandrev.jdorg.configuration.Constants;
import java.util.Locale;
import java.util.ResourceBundle;

public final class TextLocalizerManager {

	private static TextLocalizerManager instance;
	
	private ResourceBundle  captions;
	
	public static TextLocalizerManager getInstance(Locale locale){
		if(instance == null || !instance.isSameLocale(locale)){
			instance = new TextLocalizerManager(locale);
		}
		return instance;
	}
	
	
	private boolean isSameLocale(Locale locale) {
                boolean out = false;
		if(locale == null){
                    out=true;
		}
		else{
                    if(captions != null){
                            out =locale.toString().equals(captions.getLocale().toString());
                    }
		}
		return out;
	}


	private TextLocalizerManager(Locale locale){
            Locale finalLocal;
            if(locale == null){
                    finalLocal = Locale.getDefault();
            }
            else{
                    finalLocal = locale;
            }
            captions = ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE_NAME,finalLocal);
	}
	
	public String getLocalizerText(String i18nKey){
		return captions.getString(i18nKey);
	}
}
