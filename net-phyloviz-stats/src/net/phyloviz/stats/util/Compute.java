/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.stats.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Pedro T. Monteiro
 */
public class Compute {

    private TreeMap<String, Integer> tmData;
    private int iSize;
    private DecimalFormat df;

    public Compute() {
        tmData = new TreeMap<String, Integer>();
        iSize = 0;
        df = new DecimalFormat("#.####");
    }

    public void add(String value) {
        int count = 1;
        if (tmData.containsKey(value)) {
            count += tmData.get(value);
        }
        tmData.put(value, count);
        iSize++;
    }

    public StringBuffer getHTML(String fieldName) {
        StringBuffer sb = new StringBuffer();
        sb.append("<b>Simpson's Index of Diversity</b>: ");
        if (iSize > 1) {
            double sid = this.computeSID();
            sb.append(df.format(sid));
            sb.append("<br/><b>Confidence Interval<sub>95%</sub></b>: ");
            sb.append(this.computeSIDci95(sid));
        } else {
            // Doesn't make sense to compute both
            sb.append("ND<br/><b>Confidence Interval<sub>95%</sub></b>: ND");
        }
        sb.append("<br/><table border=1>");
        sb.append("<tr><th><b>").append(fieldName).append("</b></th>");
        sb.append("<th><b>Absolute Frequency</b></th>");
        sb.append("<th><b>Relative Frequency</b></th>").append("</tr>");

        double dTotal = 0.0, dPartial;
        for (String id : sortByValue(tmData)) {
            dPartial = (double) tmData.get(id) / iSize;
            dTotal += dPartial;
            sb.append("<tr><td>").append(id).append("</td><td>");
            sb.append(tmData.get(id)).append("</td><td>");
            sb.append(df.format(dPartial)).append("</td></tr>");
        }
        sb.append("<tr><td><b>Uniques</b>: ").append(tmData.size());
        sb.append("</td><td><b>Total</b>: ").append(iSize);
        sb.append("</td><td><b>Total</b>: ");
        sb.append((new DecimalFormat("#.##")).format(dTotal));
        sb.append("</tr></table>");
        return sb;
    }

    private List<String> sortByValue(final Map<String, Integer> m) {
        List<String> keys = new ArrayList<String>();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer v1 = m.get(o1);
                Integer v2 = m.get(o2);
                if (v1 < v2) // reverse order
                {
                    return 1;
                } else if (v1 == v2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return keys;
    }

    private double computeSID() {
        double fSID = 0;
        for (String s : tmData.keySet()) {
            int count = tmData.get(s);
            fSID += count * (count - 1);
        }
        fSID /= iSize;
        fSID /= (iSize - 1);
        return 1 - fSID;
    }

    private String computeSIDci95(double sid) {
        double sumPi2 = 0;
        double sumPi3 = 0;
        if (tmData.size() == iSize) {
            // Avoids numerical problems for the sqrt(num<0)
            return "[" + sid + ", " + sid + "]";
        }
        for (String s : tmData.keySet()) {
            double pi = (double) tmData.get(s) / iSize;
            double i2 = pi * pi;
            double i3 = i2 * pi;
            sumPi2 += i2;
            sumPi3 += i3;
        }
        double var = ((double) 4.0 / iSize) * (sumPi3 - sumPi2 * sumPi2);
        double diff = 2 * Math.sqrt(var);
        return "[" + df.format(sid - diff) + ", " + df.format(sid + diff) + "]";
    }
}
