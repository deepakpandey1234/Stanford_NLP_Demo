
package com.common;

public class RetrievedLinks {
    
    String URL ;
    int depth;


    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getURL() {
        return URL;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetrievedLinks that = (RetrievedLinks) o;

        return URL.equals(that.URL);
    }

    @Override
    public int hashCode() {
        return URL.hashCode();
    }
}
