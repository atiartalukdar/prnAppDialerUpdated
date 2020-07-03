package model;

import android.content.Intent;

public class LimitModdel {

    String number;
    String website;

    public LimitModdel() {
    }

    public LimitModdel(String number, String website) {
        this.number = number;
        this.website = website;
    }

    public int getNumber() {
        return Integer.parseInt(number);
    }

    public int getWebsite() {
        return Integer.parseInt(website);
    }

    @Override
    public String toString() {
        return "LimitModdel{" +
                "number=" + number +
                ", website=" + website +
                '}';
    }
}
