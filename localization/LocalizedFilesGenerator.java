
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class LocalizedFilesGenerator {

    private static final char SEPARATOR = ',';

    private static final String FILE_ID = "1W5vhD-1S3iZ3EUztceW-aVgJtNOLjqUwxfK71wYvNrg";
    private static final String FILE_PATH = "https://docs.google.com/spreadsheets/d/%s/export?exportFormat=csv&gid=%s";

    private static final String COMMENT_KEY = "";
    private static final String KEY_KEY = "Key";
    private static final String ENGLISH_KEY = "English";
    private static final String FRENCH_KEY = "French";

    private static final String ANDROID = "android";
    private static final String IOS = "iOS";
    private static Map<String, FileWriter[]> writersMap;

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            printUsageAndExit();
        }

        String type = args[0];
        String gid = args[1];
        String englishOutputFile = args[2];
        String frenchOutputFile = args[3];

        writersMap = new HashMap<>();
        writersMap.put(ANDROID, new FileWriter[] {
                new AndroidXMLFileWriter("Android English", englishOutputFile, COMMENT_KEY, KEY_KEY, ENGLISH_KEY),
                new AndroidXMLFileWriter("Android French", frenchOutputFile, COMMENT_KEY, KEY_KEY, FRENCH_KEY),});
        writersMap.put(IOS, new FileWriter[] {
                new IOSStringsFileWriter("iOS English", englishOutputFile, COMMENT_KEY, KEY_KEY, ENGLISH_KEY),
                new IOSStringsFileWriter("iOS French", frenchOutputFile, COMMENT_KEY, KEY_KEY, FRENCH_KEY),});

        if (!writersMap.containsKey(type)) {
            printUsageAndExit();
        }

        // download the file.
        System.out.print("Opening connection to download file...");
        URL url = new URL(String.format(FILE_PATH, FILE_ID, gid));
        InputStream inputStream = url.openStream();
        System.out.println(" Done.");

        // parse file
        System.out.print("Downloading & parsing...");
        List<Map<String, String>> sheetData = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            // parse file
            String line = bufferedReader.readLine();
            String[] keys = line.split("" + SEPARATOR);
            while ((line = readLine(bufferedReader, keys.length)) != null) {
                Map<String, String> entry = new HashMap<>();
                String[] parts = line.split("" + SEPARATOR);
                for (int i = 0; i < parts.length && i < keys.length; i++) {
                    entry.put(keys[i], parts[i]);
                }
                sheetData.add(entry);
            }
            bufferedReader.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println(" Done.");

        // Rewrite file
        FileWriter[] fileWriters = writersMap.get(type);
        for (FileWriter fileWriter : fileWriters) {
            System.out.print("Writing " + fileWriter.getLabel() + " file...");
            fileWriter.initFile();
            for (Map<String, String> entry : sheetData) {
                fileWriter.writeEntry(entry);
            }
            fileWriter.finalizeFile();
            System.out.println(" Done.");
        }
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java com.ntrey.script.LocalizedFilesGenerator <android|iOS> <gid> <english_output_file> <french_output_file>");
        System.exit(-1);
    }

    private static String readLine(BufferedReader bufferedReader, int sectionsToRead) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int readSeparators = 0;
        String line;
        boolean[] quoteOpen = {false};
        while (readSeparators < sectionsToRead - 1 && (line = bufferedReader.readLine()) != null) {
            if (readSeparators > 0) {
                stringBuilder.append("\n");
            }
            String[] lineArray = {line};
            readSeparators += countSeparators(lineArray, quoteOpen);
            line = lineArray[0];
            stringBuilder.append(line);
        }
        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }

    private static int countSeparators(String[] line, boolean[] openQuote) {
        int count = 0;
        for (int i = 0; i < line[0].length(); i++) {
            final char currentChar = line[0].charAt(i);
            if (currentChar == '\"') {
                openQuote[0] = !openQuote[0];
            }
            if (currentChar == SEPARATOR) {
                if (openQuote[0]) {
                    line[0] = line[0].substring(0, i) + skipSeparator(SEPARATOR) + line[0].substring(i + 1, line[0].length());
                } else {
                    count++;
                }
            }
        }

        return count;
    }

    private static String skipSeparator(char separator) {
        int code = (int) separator;
        String hex = Integer.toHexString(code);
        return "\\u" + "0000".substring(hex.length()) + hex;
    }

    private interface FileWriter {
        void initFile() throws Exception;

        void writeEntry(Map<String, String> entry) throws Exception;

        void finalizeFile() throws Exception;

        String getLabel();
    }

    private static abstract class BaseFileWriter implements FileWriter {

        protected final String outputFilePath;
        protected final String commentKey;
        protected final String keyKey;
        protected final String languageKey;
        protected final String label;

        public BaseFileWriter(String label, String outputFilePath, String commentKey, String keyKey, String languageKey) {
            this.label = label;
            this.outputFilePath = outputFilePath;
            this.commentKey = commentKey;
            this.keyKey = keyKey;
            this.languageKey = languageKey;
        }

        protected File initOutputFile() throws Exception {
            File outputFile = new File(outputFilePath);
            File parentDir = outputFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            } else if (outputFile.exists()) {
                outputFile.delete();
            }

            return outputFile;
        }

        protected String unescapeUnicodeChars(String input) throws NumberFormatException {
            int index = 0;
            while ((index = input.indexOf("\\u", index)) >= 0) {
                String hexCode = input.substring(index + 2, index + 6);
                char unSkippedChar = (char) Integer.parseInt(hexCode, 16);
                input = input.substring(0, index) + unSkippedChar + input.substring(index + 6);
            }
            return input;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    private static class AndroidXMLFileWriter extends BaseFileWriter {

        private XMLStreamWriter xmlStreamWriter;

        public AndroidXMLFileWriter(String label, String outputFilePath, String commentKey, String keyKey, String languageKey) {
            super(label, outputFilePath, commentKey, keyKey, languageKey);
        }

        @Override
        public void initFile() throws Exception {
            File outputFile = initOutputFile();
            xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(new FileOutputStream(outputFile), "UTF-8");
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("resources");
            if (languageKey.equals(ENGLISH_KEY)) {
                xmlStreamWriter.writeAttribute("xmlns:tools", "http://schemas.android.com/tools");
                xmlStreamWriter.writeAttribute("xmlns:local", "en");
            }
            xmlStreamWriter.writeCharacters("\n");
        }

        @Override
        public void writeEntry(Map<String, String> entry) throws Exception {
            String comment = entry.get(commentKey);
            if (comment != null && !comment.equals("")) {
                xmlStreamWriter.writeCharacters("\n    ");
                xmlStreamWriter.writeComment(comment);
                xmlStreamWriter.writeCharacters("\n");
            } else {
                String key = entry.get(keyKey);
                String value = entry.get(languageKey);
                if (key != null && key.length() > 0 && value != null) {
                    xmlStreamWriter.writeCharacters("    ");
                    xmlStreamWriter.writeStartElement("string");
                    xmlStreamWriter.writeAttribute("name", key.toLowerCase());
                    final String cdataPprefix = "<![CDATA[";
                    final String cdataSuffix = "]]>";
                    if (value.startsWith(cdataPprefix) && value.endsWith(cdataSuffix)) {
                        xmlStreamWriter.writeCData(format(value.substring(cdataPprefix.length(), value.length() - cdataSuffix.length())));
                    } else {
                        value = format(value);
                        if (!value.startsWith("\"")) {
                            value = "\"" + value;
                        }
                        if (!value.endsWith("\"")) {
                            value = value + "\"";
                        }
                        xmlStreamWriter.writeCharacters(value);
                    }
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
            }
        }

        public String format(String input) {
            input = input.replaceAll("\\'", "\\\\'").replaceAll("%@", "%s").replaceAll("\\$@", "\\$s");

            input = unescapeUnicodeChars(input);

            int index = 0;
            int endIndex;
            while ((index = input.indexOf('%', index)) >= 0 && (endIndex = input.indexOf("ld", index)) > 0) {
                final String regex = "%\\d*ld";
                if (input.substring(index, endIndex + 2).matches(regex)) {
                    input = input.replaceAll(regex, "%" + input.substring(index + 1, endIndex) + "d");
                }
                index = endIndex + 1;
            }

            return input;
        }

        @Override
        public void finalizeFile() throws Exception {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.close();
        }
    }

    private static class IOSStringsFileWriter extends BaseFileWriter {

        private BufferedWriter bufferedWriter;

        public IOSStringsFileWriter(String label, String outputFilePath, String commentKey, String keyKey, String languageKey) {
            super(label, outputFilePath, commentKey, keyKey, languageKey);
        }

        @Override
        public void initFile() throws Exception {
            File outputFile = initOutputFile();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            bufferedWriter.write("//\n"
                    + "// Localizable.strings\n"
                    + "// NTREY\n"
                    + "//\n"
                    + "// Imported from the localization spreadsheet on " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".\n"
                    + "// Copyright (c) " + new SimpleDateFormat("yyyy").format(new Date()) + " NTREY. All rights reserved.\n"
                    + "//\n\n");
        }

        @Override
        public void writeEntry(Map<String, String> entry) throws Exception {
            String comment = entry.get(commentKey);
            if (comment != null && !comment.equals("")) {
                bufferedWriter.write("\n// " + comment + "\n");
            } else {
                String key = entry.get(keyKey);
                String value = entry.get(languageKey);
                if (key != null && value != null) {
                    bufferedWriter.write("\"" + key + "\"");
                    bufferedWriter.write(" = ");
                    value = unescapeUnicodeChars(value);
                    if (!value.startsWith("\"")) {
                        value = "\"" + value;
                    }
                    if (!value.endsWith("\"")) {
                        value = value + "\"";
                    }
                    bufferedWriter.write(value + ";\n");
                }
            }
        }

        @Override
        public void finalizeFile() throws Exception {
            bufferedWriter.close();
        }
    }
}
