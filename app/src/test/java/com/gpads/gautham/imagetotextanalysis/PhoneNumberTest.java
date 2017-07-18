package com.gpads.gautham.imagetotextanalysis;

import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhoneNumberTest {
    @Test
    public void nice(){
        String text = "HANS SCHREFF Program Manager Conservation Activities London Hydro 111 Horton Street, P.0. Box 2700 London, ON N6A 4H6 Tel519 661 5800 Ext. 5014 Cell: 519 630 8210 Powering London. E-mailschreffh@londonhydro.com Empowering You. Fax: 519 661 5863 :";
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numberMatches = phoneNumberUtil.findNumbers(text, Locale.CANADA.getCountry());
        List<String> data = new ArrayList<>();
        for(PhoneNumberMatch number : numberMatches){
            String s = number.rawString();
            System.out.println(s);
        }
    }
    public static void main(String[] args) {

    }

}
