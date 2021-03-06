package gov.nist.javax.sip.header;

import gov.nist.core.NameValue;
import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.ParameterNamesIms;
import java.util.Locale;
import javax.sip.InvalidArgumentException;
import javax.sip.header.AcceptLanguageHeader;

public final class AcceptLanguage extends ParametersHeader implements AcceptLanguageHeader {
    private static final long serialVersionUID = -4473982069737324919L;
    protected String languageRange;

    public AcceptLanguage() {
        super(AcceptLanguageHeader.NAME);
    }

    protected String encodeBody() {
        StringBuffer encoding = new StringBuffer();
        if (this.languageRange != null) {
            encoding.append(this.languageRange);
        }
        if (!this.parameters.isEmpty()) {
            encoding.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return encoding.toString();
    }

    public String getLanguageRange() {
        return this.languageRange;
    }

    public float getQValue() {
        if (hasParameter(ParameterNamesIms.Q)) {
            return ((Float) this.parameters.getValue(ParameterNamesIms.Q)).floatValue();
        }
        return -1.0f;
    }

    public boolean hasQValue() {
        return hasParameter(ParameterNamesIms.Q);
    }

    public void removeQValue() {
        removeParameter(ParameterNamesIms.Q);
    }

    public void setLanguageRange(String languageRange) {
        this.languageRange = languageRange.trim();
    }

    public void setQValue(float q) throws InvalidArgumentException {
        if (((double) q) < 0.0d || ((double) q) > 1.0d) {
            throw new InvalidArgumentException("qvalue out of range!");
        } else if (q == -1.0f) {
            removeParameter(ParameterNamesIms.Q);
        } else {
            setParameter(new NameValue(ParameterNamesIms.Q, Float.valueOf(q)));
        }
    }

    public Locale getAcceptLanguage() {
        if (this.languageRange == null) {
            return null;
        }
        int dash = this.languageRange.indexOf(45);
        if (dash >= 0) {
            return new Locale(this.languageRange.substring(0, dash), this.languageRange.substring(dash + 1));
        }
        return new Locale(this.languageRange);
    }

    public void setAcceptLanguage(Locale language) {
        if ("".equals(language.getCountry())) {
            this.languageRange = language.getLanguage();
        } else {
            this.languageRange = language.getLanguage() + '-' + language.getCountry();
        }
    }
}
