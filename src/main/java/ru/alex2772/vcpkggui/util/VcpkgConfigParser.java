package ru.alex2772.vcpkggui.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class VcpkgConfigParser {

    public interface IVisitor {
        void vcpkgConfigVisit(String key, String value);
    }


    /**
     * Parses the file with the following structure:
     * <pre>
     *     key1: value1
     *     key2: value2
     *     ...
     *     keyN: valueN
     * </pre>
     * The input being read until EOF or blank line.
     * @param input input
     * @param visitor visitor to handle config data
     */
    public static void parse(Reader input, IVisitor visitor) throws IOException {
        BufferedReader buffered = new BufferedReader(input);
        String line;
        while ((line = buffered.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            int indexOfColon = line.indexOf(':');
            if (indexOfColon == -1) {
                continue;
            }
            visitor.vcpkgConfigVisit(line.substring(0, indexOfColon).trim(),
                                     line.substring(indexOfColon + 1).trim());
        }
    }
}
