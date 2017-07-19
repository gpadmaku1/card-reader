package com.gpads.gautham.imagetotextanalysis;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.junit.Test;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void emailParsingTest(){
        String text = "Dank Pepe Program Manager Org Activities Multi Studios 555 Horton Street, " +
                "P.0. Box 143 London, ON N6A 4H6 519 661 9000 Ext. 5014 Cell: 519 456 5463 Powering London. " +
                "E-mailschreffh@londonhydro.com Empowering You. Fax: 519 611 5841 :";
        String text2 = "onlinestudiomarketing Darth Vader R Web Developer/ Designer/ Wordpress " +
                "Consultant 519-333-541 info@onlinestudiomarketing.ca London, ON Canada www.onlinestudiomarketing.ca";
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(text2);
        String parsedEmail = "Error";
        while (m.find()) {
            parsedEmail = m.group();
        }
        System.out.println(parsedEmail);
    }

    @Test
    public void sampleNameParseTest(){
        String text = "Dank Pepe Program Manager Org Activities Multi Studios 555 Horton Street, " +
                "P.0. Box 143 London, ON N6A 4H6 519 661 9000 Ext. 5014 Cell: 519 456 5463 Powering London. " +
                "E-mailschreffh@londonhydro.com Empowering You. Fax: 519 611 5841 :";
        String text2 = "onlinestudiomarketing Darth Vader R Web Developer/ Designer/ Wordpress " +
                "Consultant 519-333-541 info@onlinestudiomarketing.ca London, ON Canada www.onlinestudiomarketing.ca";
        String[] arr = text.split("\\s+");
        System.out.println(arr[0] + " " + arr[1]);
    }
}
