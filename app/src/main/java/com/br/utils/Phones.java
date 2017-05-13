package com.br.utils;

import android.content.Context;

import com.br.primeirabuscavoiceremover.R;

/**
 * Created by MarioJ on 08/05/15.
 */
public class Phones {

    public static final String INTERNATIONAL_IDENTIFIER = "+";

    public static String formatNumber(String phone) {

        phone = phone.replaceAll("[^\\d" + INTERNATIONAL_IDENTIFIER + "]", "").trim();

        while (phone.startsWith("0")) {
            phone = phone.replaceFirst("0", "");
        }

        return phone;
    }

    public static String getCountryISO(Context context, String countryCode) {

        String[] countries = context.getResources().getStringArray(R.array.countries);

        for (String country : countries) {

            String tokens[] = country.split(",");

            if (countryCode.equals(tokens[0]))
                return tokens[1];

        }

        return null;

    }

}
