package com.brightloop.subscription.common.util;

public final class MoneyUtils {

    private MoneyUtils() {
    }

    /**
     * Formats money stored in BIGINT paise to Indian Rupees with Indian grouping.
     * Example: 149900 paise -> "₹1,499.00"
     */
    public static String formatPaiseToRupees(Long paise) {
        if (paise == null) {
            paise = 0L;
        }

        boolean negative = paise < 0;
        long absPaise = Math.abs(paise);
        long rupees = absPaise / 100;
        long paiseRemainder = absPaise % 100;

        String rupeeStr = String.valueOf(rupees);
        StringBuilder formattedRupees = new StringBuilder();

        if (rupeeStr.length() <= 3) {
            formattedRupees.append(rupeeStr);
        } else {
            String lastThree = rupeeStr.substring(rupeeStr.length() - 3);
            String rest = rupeeStr.substring(0, rupeeStr.length() - 3);
            StringBuilder groupedRest = new StringBuilder();
            int count = 0;
            for (int i = rest.length() - 1; i >= 0; i--) {
                groupedRest.insert(0, rest.charAt(i));
                count++;
                if (count == 2 && i > 0) {
                    groupedRest.insert(0, ",");
                    count = 0;
                }
            }
            formattedRupees.append(groupedRest).append(",").append(lastThree);
        }

        return (negative ? "-₹" : "₹") + formattedRupees + "." + String.format("%02d", paiseRemainder);
    }
}
