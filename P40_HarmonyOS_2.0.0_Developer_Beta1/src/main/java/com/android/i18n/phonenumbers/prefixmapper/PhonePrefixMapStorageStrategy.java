package com.android.i18n.phonenumbers.prefixmapper;

import gov.nist.core.Separators;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.SortedMap;
import java.util.TreeSet;

/* access modifiers changed from: package-private */
public abstract class PhonePrefixMapStorageStrategy {
    protected int numOfEntries = 0;
    protected final TreeSet<Integer> possibleLengths = new TreeSet<>();

    public abstract String getDescription(int i);

    public abstract int getPrefix(int i);

    public abstract void readExternal(ObjectInput objectInput) throws IOException;

    public abstract void readFromSortedMap(SortedMap<Integer, String> sortedMap);

    public abstract void writeExternal(ObjectOutput objectOutput) throws IOException;

    PhonePrefixMapStorageStrategy() {
    }

    public int getNumOfEntries() {
        return this.numOfEntries;
    }

    public TreeSet<Integer> getPossibleLengths() {
        return this.possibleLengths;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        int numOfEntries2 = getNumOfEntries();
        for (int i = 0; i < numOfEntries2; i++) {
            output.append(getPrefix(i));
            output.append("|");
            output.append(getDescription(i));
            output.append(Separators.RETURN);
        }
        return output.toString();
    }
}
