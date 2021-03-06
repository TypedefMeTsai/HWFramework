package gov.nist.javax.sip.header;

public class AcceptEncodingList extends SIPHeaderList<AcceptEncoding> {
    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject, java.lang.Object
    public Object clone() {
        AcceptEncodingList retval = new AcceptEncodingList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public AcceptEncodingList() {
        super(AcceptEncoding.class, "Accept-Encoding");
    }
}
