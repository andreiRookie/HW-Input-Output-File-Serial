package log;

import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientLog {

    public static final String LOG_DIR_NAME = "./client log";
    public static final String LOG_CSV_FILE_NAME = "./client log/log.csv";

    private List<ClientLogEntry> entries = new ArrayList<>();

    public void log(int productNum, int amount) {
        ClientLogEntry entry = new ClientLogEntry(productNum, amount);
        this.entries.add(entry);
    }

    public void exportAsCSV(File txtFile) {
        List<String[]> entriesAsList = entriesToList();
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(txtFile))) {
                csvWriter.writeAll(entriesAsList);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    private ColumnPositionMappingStrategy<ClientLogEntry> getStrategy() {
        ColumnPositionMappingStrategy<ClientLogEntry> strategy =
                new ColumnPositionMappingStrategy<>();
        strategy.setType(ClientLogEntry.class);
        strategy.setColumnMapping(new String[]{"productNum","amount"});
        return strategy;
    }
    
    private List<String[]> entriesToList() {
        List<String[]> list = new ArrayList<>();
        for (ClientLogEntry entry : entries) {
            list.add(entry.toStringArray());
        }
        return list;
    }

    public static void makeClientLogDir(String dirName) {
        File dir = new File(dirName);
        try {
            dir.mkdir();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
//    public static File makeClientLogFile(String fileName) {
//        File file = new File(fileName);
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return file;
//    }

    public List<ClientLogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ClientLogEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "ClientLog{" +
                "\n\tentries=" +
                entries.stream().map(ClientLogEntry::toString).reduce((s, s1) -> "\n" + s + "\n" + s1) +
                "\n}";
    }
}
