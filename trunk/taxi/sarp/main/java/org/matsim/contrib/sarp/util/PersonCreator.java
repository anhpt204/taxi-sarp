/**
 * taxi
 * org.matsim.contrib.sarp.util
 * tuananh
 * Nov 27, 2014
 */
package org.matsim.contrib.sarp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tuananh
 *
 */
public class PersonCreator
{
    public static List<String> readTaxiCustomerIds(String taxiCustomersFile)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(taxiCustomersFile)))) {
            List<String> taxiCustomerIds = new ArrayList<>();

            String line;
            while ( (line = br.readLine()) != null) {
                taxiCustomerIds.add(line);
            }

            return taxiCustomerIds;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
