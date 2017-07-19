package com.gpads.gautham.imagetotextanalysis;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.junit.Test;
import java.util.Locale;

public class PhoneNumberTest {

    @Test
    public void phoneNumberParsingTest(){
        String text = "Dank Pepe Program Manager Org Activities Multi Studios 555 Horton Street, " +
                "P.0. Box 143 London, ON N6A 4H6 519 661 9000 Ext. 5014 Cell: 519 456 5463 Powering London. " +
                "E-mailschreffh@londonhydro.com Empowering You. Fax: 519 611 5841 :";
        String text2 = "onlinestudiomarketing Darth Vader R Web Developer/ Designer/ Wordpress " +
                "Consultant 519-333-541 info@onlinestudiomarketing.ca London, ON Canada www.onlinestudiomarketing.ca";

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numberMatches = phoneNumberUtil.findNumbers(text2, Locale.US.getCountry());
        for(PhoneNumberMatch number : numberMatches){
            String s = number.rawString();
            System.out.println(s);
        }
    }
}
