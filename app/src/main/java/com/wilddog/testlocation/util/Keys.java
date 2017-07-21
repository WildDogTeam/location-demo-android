package com.wilddog.testlocation.util;


import com.wilddog.location.Position;

/**
 * Created by he on 2017/7/21.
 */

public class Keys {


    
    private String key;
    private Position p;

    public Keys(String key) {
        this.key = key;
    }

    public Keys(String key, Position p) {
        this.key = key;
        this.p = p;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof Keys)) {
            return false;
        } else if(this == obj) {
            return true;
        } else {
            Keys other = (Keys)obj;
            return this.key.equals(other.key);
        }
    }
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }
}
